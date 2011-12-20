package net.scage.support.tracer3

import net.scage.support.Vec
import net.scage.support.ScageId._
import net.scage.support.ScageProperties._
import _root_.net.scage.handlers.Renderer._
import com.weiglewilczek.slf4s.Logger
import collection.mutable.{ArrayBuffer, HashMap}

class ScageTracer[T <: Trace](val field_from_x:Int        = property("field.from.x", 0),
                              val field_to_x:Int          = property("field.to.x", screen_width),
                              val field_from_y:Int        = property("field.from.y", 0),
                              val field_to_y:Int          = property("field.to.y", screen_height),
                              init_h_x:Int                = property("field.h_x", 0),
                              init_h_y:Int                = property("field.h_y", 0),
                              init_N_x:Int                = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                              init_N_y:Int                = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                              val are_solid_edges:Boolean = property("field.solid_edges", true)) {
  trait LocationImmutableTrace extends Trace with HaveLocationAndId {
    def inner_trace:T // if we use tracer only for one type of objects, than inner_trace would represent all its props,
                      // otherwise we may use property 'state' from Trace trait to handle different props of different objects
  }

  protected[this] class LocationUpdateableTrace(var location:Vec, val inner_trace:T) extends LocationImmutableTrace {
    val id = nextId
    override def changeState(changer:Trace, state:State) {inner_trace.changeState(changer, state)}
    override def state:State = inner_trace.state
    override def toString = "{id="+id+", location="+location+"}"
  }

  private val log = Logger(this.getClass.getName)

  log.debug("creating tracer "+this.getClass.getName)

  val field_width = field_to_x - field_from_x
  val field_height = field_to_y - field_from_y

  val N_x = if(init_h_x != 0) field_width/init_h_x else init_N_x
  val N_y = if(init_h_y != 0) field_height/init_h_y else init_N_y

  val h_x = if(init_h_x == 0) field_width/init_N_x else init_h_x
  val h_y = if(init_h_y == 0) field_height/init_N_y else init_h_y

  def isPointOnArea(point:Vec):Boolean = point.x >= 0 && point.x < N_x && point.y >= 0 && point.y < N_y
  def outsidePoint(point:Vec):Vec = {
    def checkC(c:Float, dist:Int):Float = {
      if(c >= dist) checkC(c - dist, dist)
      else if(c < 0) checkC(c + dist, dist)
      else c
    }
    if(are_solid_edges) point
    else Vec(checkC(point.x, N_x), checkC(point.y, N_y))
  }

  def point(v:Vec):Vec = Vec(((v.x - field_from_x)/field_width*N_x).toInt,
                             ((v.y - field_from_y)/field_height*N_y).toInt)
  def pointCenter(p:Vec):Vec = Vec(field_from_x + p.x*h_x + h_x/2, field_from_y + p.y*h_y + h_y/2)

  protected def initMatrix(matrix:Array[Array[ArrayBuffer[LocationImmutableTrace]]]) {
    for(i <- 0 until matrix.length; j <- 0 until matrix.head.length) {matrix(i)(j) = ArrayBuffer[LocationImmutableTrace]()}
  }
  protected def clearMatrix(matrix:Array[Array[ArrayBuffer[LocationImmutableTrace]]]) {
    for(i <- 0 until matrix.length; j <- 0 until matrix.head.length) {matrix(i)(j).clear()}
  }

  // it is very critical for the structures below to be changed only inside ScageTracer
  // but for convenience I keep them protected, so client classes - children of ScageTracer can read them
  protected val point_matrix = Array.ofDim[ArrayBuffer[LocationImmutableTrace]](N_x, N_y)
  initMatrix(point_matrix)
  protected val traces_by_ids = HashMap[Int, LocationUpdateableTrace]()
  protected var traces_list:ArrayBuffer[LocationImmutableTrace] = ArrayBuffer[LocationImmutableTrace]()
  def tracesList = traces_list.toList
  
  def addTrace(point:Vec, trace:T) = {
    val updateable_trace = trace match {
      case updateable:LocationUpdateableTrace => {
        updateable.location = point
        updateable
      }
      case _ => new LocationUpdateableTrace(point, trace)
    }
    if(isPointOnArea(point)) {
      point_matrix(point.ix)(point.iy) += updateable_trace
      traces_by_ids += updateable_trace.id -> updateable_trace
      traces_list += updateable_trace
      log.debug("added new trace #"+updateable_trace.id+" in point ("+updateable_trace.location+")")
    } else log.warn("failed to add trace: point ("+point+") is out of area")
    val nonupdateable_trace:LocationImmutableTrace = updateable_trace
    nonupdateable_trace
  }

  def containsTrace(trace_id:Int) = traces_by_ids.contains(trace_id)
  def containsTrace(have_id:HaveLocationAndId) = traces_by_ids.contains(have_id.id)

  def removeTraces(traces_to_remove:LocationImmutableTrace*) {
    if(!traces_to_remove.isEmpty) {
      traces_to_remove.foreach(trace => {
        if(traces_by_ids.contains(trace.id)) {
          point_matrix(trace.location.ix)(trace.location.iy) -= trace
          traces_by_ids -= trace.id
          traces_list -= trace
          log.debug("removed trace #"+trace.id)
        } else log.warn("trace #"+trace.id+" not found")
      })
    } else {
      clearMatrix(point_matrix)
      traces_by_ids.clear()
      traces_list.clear()
      log.info("deleted all traces")
    }
  }
  def removeTracesById(trace_ids:Int*) {
    removeTraces(traces_list.filter(elem => trace_ids.contains(elem.id)):_*)
  }

  def tracesInPoint(point:Vec, condition:LocationImmutableTrace => Boolean) = {
    if(!isPointOnArea(point)) Nil
    else {
      for {
        trace <- point_matrix(point.ix)(point.iy)
        if condition(trace)
      } yield trace
    }
  }
  def tracesInPoint(point:Vec) = {
    if(!isPointOnArea(point)) Nil
    else (point_matrix(point.ix)(point.iy)).toList
  }

  def tracesNearPoint(point:Vec, xrange:Range, yrange:Range, condition:LocationImmutableTrace => Boolean):IndexedSeq[LocationImmutableTrace] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point, condition)
    } yield trace)
  }
  def tracesNearPoint(point:Vec, xrange:Range, condition:LocationImmutableTrace => Boolean):IndexedSeq[LocationImmutableTrace] = tracesNearPoint(point, xrange, xrange, condition)
  def tracesNearPoint(point:Vec, xrange:Range, yrange:Range):IndexedSeq[LocationImmutableTrace] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point)
    } yield trace)
  }
  def tracesNearPoint(point:Vec, xrange:Range):IndexedSeq[LocationImmutableTrace] = tracesNearPoint(point, xrange, xrange)

  val LOCATION_UPDATED = 0
  val SAME_LOCATION    = 1
  val OUT_OF_AREA      = 2
  val TRACE_NOT_FOUND  = 3
  def updateLocation(trace_id:Int, new_point:Vec):Int = { // TODO: maybe return tuple (new_location, operation_status)
    traces_by_ids.get(trace_id) match {
      case Some(updateable_trace) => {
        val old_point = updateable_trace.location
        val new_point_edges_affected = outsidePoint(new_point)
        if(isPointOnArea(new_point_edges_affected)) {
          if(old_point != new_point_edges_affected) {
            point_matrix(old_point.ix)(old_point.iy) -= updateable_trace
            point_matrix(new_point_edges_affected.ix)(new_point_edges_affected.iy) += updateable_trace
            updateable_trace.location = new_point_edges_affected
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
  def updateLocation(trace:HaveLocationAndId, new_point:Vec):Int = updateLocation(trace.id, new_point)

  def move(trace:HaveLocationAndId, delta:Vec) = {
    updateLocation(trace.id, trace.location + delta)
  }

  def randomPoint(leftup_x:Int = 0, leftup_y:Int = N_y-1, width:Int = N_x, height:Int = N_y) = {
    val x = leftup_x + (math.random*width).toInt
    val y = leftup_y - (math.random*height).toInt
    Vec(x,y)
  }
  def randomCoord(leftup_x:Int = field_from_x, leftup_y:Int = field_to_y-1, width:Int = field_to_x - field_from_x, height:Int = field_to_y - field_from_y) = {
    randomPoint(leftup_x, leftup_y, width, height)
  }
}

object ScageTracer {
  def apply(field_from_x:Int        = property("field.from.x", 0),
            field_to_x:Int          = property("field.to.x", screen_width),
            field_from_y:Int        = property("field.from.y", 0),
            field_to_y:Int          = property("field.to.y", screen_height),
            init_h_x:Int            = property("field.h_x", 0),
            init_h_y:Int            = property("field.h_y", 0),
            init_N_x:Int            = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
            init_N_y:Int            = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
            are_solid_edges:Boolean = property("field.solid_edges", true)) = {
    new ScageTracer[Trace](field_from_x,field_to_x,field_from_y,field_to_y,init_h_x,init_h_y,init_N_x,init_N_y,are_solid_edges) {
      def addTrace(point:Vec):LocationImmutableTrace = {addTrace(point, Trace())}
    }
  }

  // maybe some other name for this factory method (like 'newTracer', etc)
  def create[T <: Trace](field_from_x:Int        = property("field.from.x", 0),
                         field_to_x:Int          = property("field.to.x", screen_width),
                         field_from_y:Int        = property("field.from.y", 0),
                         field_to_y:Int          = property("field.to.y", screen_height),
                         init_h_x:Int            = property("field.h_x", 0),
                         init_h_y:Int            = property("field.h_y", 0),
                         init_N_x:Int            = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                         init_N_y:Int            = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                         are_solid_edges:Boolean = property("field.solid_edges", true)) = {
    new ScageTracer[T](field_from_x,field_to_x,field_from_y,field_to_y,init_h_x,init_h_y,init_N_x,init_N_y,are_solid_edges)
  }
}