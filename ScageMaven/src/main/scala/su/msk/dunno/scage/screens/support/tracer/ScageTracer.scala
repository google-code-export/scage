package su.msk.dunno.scage.screens.support.tracer

import org.apache.log4j.Logger
import su.msk.dunno.scage.single.support.ScageProperties.property
import collection.mutable.HashMap
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.ScageId._

/*object ScageTracer {
  private val log = Logger.getLogger(this.getClass);

  private var next_trace_id = 0
  def nextTraceID:Int = {
    next_trace_id += 1
    next_trace_id
  }
}

import ScageTracer._*/

trait Trace {
  val id = /*nextTraceID*/nextId

  val point:Vec = Vec(-1,-1)

  def changeState(changer:Trace, state:State)
  def getState:State
}

class ScageTracer[T <: Trace](val field_from_x:Int        = property("field.from.x", 0),
                              val field_to_x:Int          = property("field.to.x", width),
                              val field_from_y:Int        = property("field.from.y", 0),
                              val field_to_y:Int          = property("field.to.y", height),
                              init_h_x:Int                = property("field.h_x", 0),
                              init_h_y:Int                = property("field.h_y", 0),
                              init_N_x:Int                 = property("field.N_x", width/50),
                              init_N_y:Int                 = property("field.N_y", height/50),
                              val are_solid_edges:Boolean = property("field.solid_edges", true)) {
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

  protected def initMatrix(matrix:Array[Array[List[T]]]) {
    for(i <- 0 until matrix.length; j <- 0 until matrix.head.length) {matrix(i)(j) = Nil}
  }

  // it is very critical for the structures below to be changed only inside ScageTracer
  // but for convenience I keep them protected, so client classes - children of ScageTracer can read them
  protected val point_matrix = Array.ofDim[List[T]](N_x, N_y)
  initMatrix(point_matrix)
  protected val traces_in_point = new HashMap[Int, Vec]()
  protected var traces_list:List[T] = Nil
  def tracesList = traces_list

  def addTrace(point:Vec, trace:T) = {
    if(isPointOnArea(point)) {
      trace.point is point
      point_matrix(point.ix)(point.iy) = trace :: point_matrix(point.ix)(point.iy)
      traces_in_point += trace.id -> trace.point.copy
      traces_list = trace :: traces_list
      log.debug("added new trace #"+trace.id+" in point ("+trace.point+")")
    }
    else log.warn("failed to add trace: point ("+point+") is out of area")
    trace
  }

  def containsTrace(trace:T) = traces_in_point.contains(trace.id)
  def containsTraceById(id:Int) = traces_in_point.contains(id)

  def removeTraces(traces:T*) { // TODO: add existence check (plan it carefully), log messages and return result
    if(traces.size > 0) {
      traces.foreach(trace => {
        point_matrix(trace.point.ix)(trace.point.iy) = point_matrix(trace.point.ix)(trace.point.iy).filterNot(_.id == trace.id)
        traces_in_point -= trace.id
        traces_list = traces_list.filterNot(other_trace => other_trace.id == trace.id)
      })
    }
    else {
      initMatrix(point_matrix)
      traces_in_point.clear()
      traces_list = Nil
      log.info("deleted all traces")
    }
  }
  def removeTraces(traces:List[T]) {
    removeTraces(traces:_*)
  }

  def removeTracesById(trace_ids:Int*) { // TODO: add log messages
    if(trace_ids.size > 0) {
      trace_ids.foreach(trace_id => {
        val trace_point = traces_in_point(trace_id)
        if(trace_point != null) {
          point_matrix(trace_point.ix)(trace_point.iy) = point_matrix(trace_point.ix)(trace_point.iy).filterNot(_.id == trace_id)
          traces_in_point -= trace_id
          traces_list = traces_list.filterNot(other_trace => other_trace.id == trace_id)
        }
      })
    }
    else {
      initMatrix(point_matrix)
      traces_in_point.clear()
      traces_list = Nil
      log.info("deleted all traces")
    }
  }

  def tracesInPoint(point:Vec, condition:(T) => Boolean) = {
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
    else point_matrix(point.ix)(point.iy)
  }
  
  def traces(point:Vec, xrange:Range, yrange:Range, condition:(T) => Boolean):List[T] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point, condition)
    } yield trace).toList
  }
  def traces(point:Vec, xrange:Range, yrange:Range):List[T] = {
    (for {
      i <- xrange
      j <- yrange
      near_point = outsidePoint(point + Vec(i, j))
      if isPointOnArea(near_point)
      trace <- tracesInPoint(near_point)
    } yield trace).toList
  }
  
  def updateLocation(trace:T, _new_point:Vec):Vec = {
    if(traces_in_point.contains(trace.id)) {
      val old_point = traces_in_point(trace.id)
      val new_point = outsidePoint(_new_point)
      if(isPointOnArea(new_point) && old_point != new_point) {
        point_matrix(old_point.ix)(old_point.iy) = point_matrix(old_point.ix)(old_point.iy).filterNot(_.id == trace.id)
        point_matrix(new_point.ix)(new_point.iy) = trace :: point_matrix(new_point.ix)(new_point.iy)
        traces_in_point(trace.id) is new_point
        trace.point is new_point
      }
    }
    else log.warn("trace with id "+trace.id+" not found")
    trace.point
  }

  def move(trace:T, delta:Vec):Vec = {
    updateLocation(trace, trace.point + delta)
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
