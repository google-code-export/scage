package net.scage.support.tracer3

import net.scage.support.Vec
import net.scage.support.ScageProperties._
import net.scage.handlers.Renderer._
import com.weiglewilczek.slf4s.Logger

class CoordTracer(field_from_x:Int        = property("field.from.x", 0),
                  field_to_x:Int          = property("field.to.x", screen_width),
                  field_from_y:Int        = property("field.from.y", 0),
                  field_to_y:Int          = property("field.to.y", screen_height),
                  init_h_x:Int            = property("field.h_x", 0),
                  init_h_y:Int            = property("field.h_y", 0),
                  init_N_x:Int            = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                  init_N_y:Int            = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                  are_solid_edges:Boolean = property("field.solid_edges", true))
extends ScageTracer(field_from_x,field_to_x,field_from_y,field_to_y,init_h_x,init_h_y,init_N_x,init_N_y,are_solid_edges) {
  private val log = Logger(this.getClass.getName);
  override def addTrace(coord:Vec, trace:Trace = Trace()) = {
    val updateable_trace = trace match {
      case updateable:LocationUpdateableTrace => {
        updateable.location = coord
        updateable
      }
      case _ => new LocationUpdateableTrace(coord, trace)
    }
    val p = point(coord)
    if(isPointOnArea(p)) {
      point_matrix(p.ix)(p.iy) = updateable_trace :: point_matrix(p.ix)(p.iy)
      traces_by_ids += updateable_trace.id -> updateable_trace
      traces_list = updateable_trace :: traces_list
      log.debug("added new trace #"+updateable_trace.id+" in point ("+updateable_trace.location+")")
    } else log.warn("failed to add trace: point ("+p+") is out of area")
    val nonupdateable_trace:LocationImmutableTrace = updateable_trace
    nonupdateable_trace
  }

  override def removeTraces(traces_to_remove:HaveLocationAndId*) {
    if(!traces_to_remove.isEmpty) {
      traces_to_remove.foreach(trace => {
        if(traces_by_ids.contains(trace.id)) {
          val trace_point = point(trace.location)
          point_matrix(trace_point.ix)(trace_point.iy) = point_matrix(trace_point.ix)(trace_point.iy).filterNot(_.id == trace.id)
          traces_by_ids -= trace.id
          log.debug("removed trace #"+trace.id)
        } else log.warn("trace #"+trace.id+" not found")
      })
      traces_list = traces_list.filterNot(trace => traces_to_remove.contains(trace))
    } else {
      initMatrix(point_matrix)
      traces_by_ids.clear()
      traces_list = Nil
      log.info("deleted all traces")
    }
  }

  def tracesNearCoord(coord:Vec, xrange:Range, yrange:Range, condition:LocationImmutableTrace => Boolean):IndexedSeq[LocationImmutableTrace] = {
    val p = point(coord)
    super.tracesNearPoint(p, xrange, yrange, condition)
  }
  def tracesNearCoord(coord:Vec, xrange:Range, condition:LocationImmutableTrace => Boolean):IndexedSeq[LocationImmutableTrace] = tracesNearCoord(coord, xrange, xrange, condition)
  def tracesNearCoord(coord:Vec, xrange:Range, yrange:Range):IndexedSeq[LocationImmutableTrace] = {
    val p = point(coord)
    super.tracesNearPoint(p, xrange, yrange)
  }
  def tracesNearCoord(coord:Vec, xrange:Range):IndexedSeq[LocationImmutableTrace] = tracesNearCoord(coord:Vec, xrange:Range, xrange:Range)

  override def updateLocation(trace_id:Int, new_coord:Vec):Int = { // TODO: maybe return tuple (new_location, operation_status)
    traces_by_ids.get(trace_id) match {
      case Some(updateable_trace) => {
        val old_coord = updateable_trace.location
        val old_point = point(old_coord)
        val new_coord_edges_affected = outsideCoord(new_coord)
        if(isCoordOnArea(new_coord_edges_affected)) {
          val new_point_edges_affected = point(new_coord_edges_affected)
          if(old_coord != new_coord_edges_affected) {
            point_matrix(old_point.ix)(old_point.iy) = point_matrix(old_point.ix)(old_point.iy).filterNot(_.id == trace_id)
            point_matrix(new_point_edges_affected.ix)(new_point_edges_affected.iy) = updateable_trace :: point_matrix(new_point_edges_affected.ix)(new_point_edges_affected.iy)
            updateable_trace.location = new_coord_edges_affected
            LOCATION_UPDATED
          } else {
            //log.warn("didn't update trace "+trace.id+": new point is the same as the old one")  // don'tknow exactly if I need such debug message
            SAME_LOCATION
          }
        } else {
          log.warn("failed to update trace "+trace_id+": new point is out of area")
          OUT_OF_AREA
        }
      }
      case None => {
        log.warn("trace with id "+trace_id+" not found")
        TRACE_NOT_FOUND
      }
    }
  }

  def outsideCoord(coord:Vec):Vec = {
    def checkC(c:Float, from:Float, to:Float):Float = {
      val dist = to - from
      if(c >= to) checkC(c - dist, from, to)
      else if(c < from) checkC(c + dist, from, to)
      else c
    }

    if(are_solid_edges) coord else {
      val x = checkC(coord.x, field_from_x, field_to_x)
      val y = checkC(coord.y, field_from_y, field_to_y)

      Vec(x, y)
    }
  }

  def isCoordOnArea(coord:Vec) = {
    coord.x >= field_from_x && coord.x < field_to_x && coord.y >= field_from_y && coord.y < field_to_y
  }

  def hasCollisions(target_trace_id:Int, tested_coord:Vec, min_dist:Float, condition:LocationImmutableTrace => Boolean = (trace) => true) = {
    if(are_solid_edges && !isCoordOnArea(tested_coord)) true
    else {
      val tested_coord_edges_affected = outsideCoord(tested_coord)
      val min_dist2 = min_dist*min_dist
      val modified_condition = (trace:LocationImmutableTrace) => trace.id != target_trace_id && condition(trace)

      val xrange = (2*min_dist/h_x).toInt + 1
      val yrange = (2*min_dist/h_y).toInt + 1
      tracesNearCoord(tested_coord_edges_affected, -xrange to xrange, -yrange to yrange, modified_condition)
        .exists(trace => (trace.location dist2 tested_coord_edges_affected) < min_dist2)
    }
  }
}