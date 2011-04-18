package su.msk.dunno.scage.screens.support.newtracer

import org.apache.log4j.Logger
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageProperties.property
import collection.mutable.HashMap

object Tracer {
  private val log = Logger.getLogger(this.getClass);

  private var next_trace_id = 0
  def nextTraceID:Int = {
    next_trace_id += 1
    next_trace_id
  }
}

import Tracer._

trait Trace {
  val id = nextTraceID

  val point:Vec = Vec(-1,-1)

  def getState:State
  def changeState(changer:Trace, state:State)
}

class Tracer[T <: Trace](val field_from_x:Int = property("field.from.x", 0), 
                         val field_to_x:Int = property("field.to.x", 800),
                         val field_from_y:Int = property("field.from.y", 0), 
                         val field_to_y:Int = property("field.to.y", 600),
                         val N_x:Int = property("field.N_x", 16),
                         val N_y:Int = property("field.N_y", 12),
                         val are_solid_edges:Boolean = property("field.solid_edges", true)) {
  protected val log = Logger.getLogger(this.getClass);

  log.debug("creating tracer "+this.getClass.getName)

  val field_width = field_to_x - field_from_x
  val field_height = field_to_y - field_from_y

  val h_x = field_width/N_x
  val h_y = field_height/N_y                         
  
  def isPointOnArea(point:Vec):Boolean = point.x >= 0 && point.x < N_x && point.y >= 0 && point.y < N_y
  protected def checkPointEdges(point:Vec):Vec = {
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
  
  
  private var point_matrix = Array.ofDim[List[T]](N_x, N_y)
  for(i <- 0 until N_x; j <- 0 until N_y) {point_matrix(i)(j) = Nil}
  private val traces_in_point = new HashMap[Int, Vec]()

  def addTrace(point:Vec, trace:T) = {
    if(isPointOnArea(point)) {
      trace.point is point
      point_matrix(point.ix)(point.iy) = trace :: point_matrix(point.ix)(point.iy)
      traces_in_point += trace.id -> trace.point.copy
      log.debug("added new trace #"+trace.id+" in point ("+trace.point+")")
    }
    else log.warn("failed to add trace: point ("+trace.point+") is out of area")

    trace
  }
  def removeTrace(trace:T) {
    point_matrix(trace.point.ix)(trace.point.iy).filterNot(_.id == trace.id)
    traces_in_point -= trace.id
  }
  
  def neighbours(target_trace:T, range:Range, condition:(T) => Boolean):List[T] = {
    (for {
      i <- range
      j <- range
      near_point = checkPointEdges(target_trace.point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- point_matrix(near_point.ix)(near_point.iy)
      if condition(trace) && trace.id != target_trace.id
    } yield trace).toList
  }
  
  def updateLocation(trace:T, _new_point:Vec) {
    val old_point = traces_in_point(trace.id)
    val new_point = checkPointEdges(_new_point)
    if(isPointOnArea(new_point) && old_point != new_point) {
      point_matrix(old_point.ix)(old_point.iy) = point_matrix(old_point.ix)(old_point.iy).filterNot(_.id == trace.id)
      point_matrix(new_point.ix)(new_point.iy) = trace :: point_matrix(new_point.ix)(new_point.iy)
      traces_in_point(trace.id) is trace.point
      trace.point is new_point
    }
  }

  def move(trace:T, delta:Vec) {
    updateLocation(trace, trace.point + delta)
  }
}
