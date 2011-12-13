package net.scage.support.tracer3

import net.scage.support.Vec
import net.scage.support.ScageId._
import net.scage.support.ScageProperties._
import org.apache.log4j.Logger
import collection.mutable.HashMap
import _root_.net.scage.handlers.Renderer._

object Trace {
  def apply(changeState:(Trace, List[(String, Any)]) => Unit = (changer, state) => {},
            state:List[(String, Any)] = Nil) = {
    val (_changeState, _state) = (changeState, state)
    new Trace {
      def changeState(changer:Trace,  state:List[(String, Any)]) {_changeState(changer, state)}
      def state:List[(String, Any)] = _state
    }
  }
}

trait Trace {
  def changeState(changer:Trace,  state:List[(String, Any)])
  def changeState(state:List[(String, Any)]) {changeState(null, state)}
  def state:List[(String, Any)]
}

trait HaveId {
  val id:Int
}

trait HaveLocation {   // maybe make HaveId as separate trait
  def location:Vec
}

class ScageTracer(val field_from_x:Int        = property("field.from.x", 0),
                  val field_to_x:Int          = property("field.to.x", screen_width),
                  val field_from_y:Int        = property("field.from.y", 0),
                  val field_to_y:Int          = property("field.to.y", screen_height),
                  init_h_x:Int                = property("field.h_x", 0),
                  init_h_y:Int                = property("field.h_y", 0),
                  init_N_x:Int                = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                  init_N_y:Int                = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                  val are_solid_edges:Boolean = property("field.solid_edges", true)) {
  protected[this] class PointUpdateableTrace(var location:Vec, trace:Trace) extends Trace with HaveLocation with HaveId {
    val id = nextId
    override def changeState(changer:Trace, state:List[(String, Any)]) {trace.changeState(changer, state)}
    override def state:List[(String, Any)] = trace.state
    override def toString = "{id="+id+", location="+location+"}"
  }
  type PointImmutableTrace = Trace with HaveLocation with HaveId

  protected val log = Logger.getLogger(this.getClass);

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

  protected def initMatrix(matrix:Array[Array[List[PointImmutableTrace]]]) {
    for(i <- 0 until matrix.length; j <- 0 until matrix.head.length) {matrix(i)(j) = Nil}
  }

  // it is very critical for the structures below to be changed only inside ScageTracer
  // but for convenience I keep them protected, so client classes - children of ScageTracer can read them
  protected val point_matrix = Array.ofDim[List[PointImmutableTrace]](N_x, N_y)  // cannot operate with ArrayBuffer because if so I have to add additional toList everywhere
  initMatrix(point_matrix)
  protected val traces_by_ids = HashMap[Int, PointUpdateableTrace]()
  protected var traces_list:List[PointImmutableTrace] = Nil
  def tracesList = traces_list
  
  def addTrace(point:Vec, trace:Trace = Trace()) = {
    val updateable_trace = trace match {
      case updateable:PointUpdateableTrace => {
        updateable.location = point
        updateable
      }
      case _ => new PointUpdateableTrace(point, trace)
    }
    if(isPointOnArea(point)) {
      point_matrix(point.ix)(point.iy) = updateable_trace :: point_matrix(point.ix)(point.iy)
      traces_by_ids += updateable_trace.id -> updateable_trace
      traces_list = updateable_trace :: traces_list
      log.debug("added new trace #"+updateable_trace.id+" in point ("+updateable_trace.location+")")
    } else log.warn("failed to add trace: point ("+point+") is out of area")
    val nonupdateable_trace:PointImmutableTrace = updateable_trace
    nonupdateable_trace
  }

  def containsTrace(trace:HaveId) = traces_by_ids.contains(trace.id)
  def containsTraceById(id:Int) = traces_by_ids.contains(id)

  def removeTraces(traces_to_remove:HaveLocation with HaveId*) { // TODO: add existence check (plan it carefully), log messages and return result
    if(!traces_to_remove.isEmpty) {
      traces_to_remove.foreach(trace => {
        point_matrix(trace.location.ix)(trace.location.iy) = point_matrix(trace.location.ix)(trace.location.iy).filterNot(_.id == trace.id)
        traces_by_ids -= trace.id
      })
      traces_list = traces_list.filterNot(trace => traces_to_remove.contains(trace))
    } else {
      initMatrix(point_matrix)
      traces_by_ids.clear()
      traces_list = Nil
      log.info("deleted all traces")
    }
  }
  
  def removeTracesById(trace_ids:Int*) { // TODO: add log messages
    removeTraces(trace_ids.filter(traces_by_ids.contains(_)).map(traces_by_ids(_)):_*)
  }

  def tracesInPoint(point:Vec, condition:PointImmutableTrace => Boolean) = {
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
    else (point_matrix(point.ix)(point.iy))
  }

  def traces(point:Vec, xrange:Range, yrange:Range, condition:PointImmutableTrace => Boolean):IndexedSeq[PointImmutableTrace] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point, condition)
    } yield trace)
  }
  def traces(point:Vec, xrange:Range, condition:(PointImmutableTrace) => Boolean):IndexedSeq[PointImmutableTrace] = traces(point, xrange, xrange, condition)
  def traces(point:Vec, xrange:Range, yrange:Range):IndexedSeq[PointImmutableTrace] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point)
    } yield trace)
  }
  def traces(point:Vec, xrange:Range):IndexedSeq[PointImmutableTrace] = traces(point, xrange, xrange)

  val POINT_UPDATED = 0
  val SAME_POINT = 1
  val OUT_OF_AREA = 2
  val TRACE_NOT_FOUND = 3
  def updateLocation(trace_id:Int, new_point:Vec):Int = { // TODO: maybe instead of warnings return tuple where second field is operation status... smth like that
    traces_by_ids.find(_._1 == trace_id) match {    // seems necessary: if we failed to add trace but missed that fact and then trying to update trace's location,
      case Some((_, updateable_trace)) => {           // we must not succeed in doing this too
        val old_point = updateable_trace.location
        val new_point_edges_affected = outsidePoint(new_point)
        if(isPointOnArea(new_point_edges_affected)) {
          if(old_point != new_point_edges_affected) {
            point_matrix(old_point.ix)(old_point.iy) = point_matrix(old_point.ix)(old_point.iy).filterNot(_.id == trace_id)
            point_matrix(new_point_edges_affected.ix)(new_point_edges_affected.iy) = updateable_trace :: point_matrix(new_point_edges_affected.ix)(new_point_edges_affected.iy)
            updateable_trace.location = new_point_edges_affected
            POINT_UPDATED
          } else {
            // don'tknow exactly if I need such debug message
            //log.warn("didn't update trace "+trace.id+": new point is the same as the old one")
            SAME_POINT
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

  def move(trace:HaveLocation with HaveId, delta:Vec) = {
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