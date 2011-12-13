package net.scage.support.tracer3

import net.scage.support.Vec
import net.scage.support.ScageProperties._
import net.scage.handlers.Renderer._
import collection.mutable.HashMap

trait HaveCoord {
  def coord:Vec
}

class CoordTracer(field_from_x:Int = property("field.from.x", 0),
                  field_to_x:Int   = property("field.to.x", screen_width),
                  field_from_y:Int = property("field.from.y", 0),
                  field_to_y:Int   = property("field.to.y", screen_height),
                  init_h_x:Int     = property("field.h_x", 0),
                  init_h_y:Int     = property("field.h_y", 0),
                  init_N_x:Int     = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                  init_N_y:Int     = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                  are_solid_edges:Boolean = property("field.solid_edges", true))
extends ScageTracer(field_from_x,field_to_x,field_from_y,field_to_y,init_h_x,init_h_y,init_N_x,init_N_y,are_solid_edges) {
  protected[this] class CoordUpdateableTrace(var coord:Vec, trace:Trace) extends PointUpdateableTrace(point(coord), trace) with HaveCoord
  type CoordImmutableTrace = Trace with HaveCoord with HaveLocation with HaveId
  
  def isCoordOnArea(coord:Vec) = {
    coord.x >= field_from_x && coord.x < field_to_x && coord.y >= field_from_y && coord.y < field_to_y
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

  override def addTrace(coord:Vec, trace:Trace = Trace()) = {
    val updateable_trace = trace match {
      case updateable:CoordUpdateableTrace => {
        updateable.coord = coord
        updateable
      }
      case _ => new CoordUpdateableTrace(coord, trace)
    }
    super.addTrace(point(coord), updateable_trace)
    val immutable_trace:CoordImmutableTrace = updateable_trace
    immutable_trace
  }
  
  override def updateLocation(trace_id:Int, new_coord:Vec):Int = {
    traces_by_ids.find(_._1 == trace_id) match {
      case Some((_, updateable_trace:CoordUpdateableTrace)) => {
        val new_coord_edges_affected = outsideCoord(new_coord)
        val result = super.updateLocation(trace_id, point(new_coord_edges_affected))
        if(result == POINT_UPDATED || result == SAME_POINT) updateable_trace.coord = new_coord_edges_affected
        result
      }
      case None => {
        log.warn("trace with id "+trace_id+" not found")
        TRACE_NOT_FOUND
      }  
    }
  }
}