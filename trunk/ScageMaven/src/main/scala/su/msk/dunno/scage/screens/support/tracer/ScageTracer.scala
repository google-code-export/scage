package su.msk.dunno.scage.screens.support.tracer

import org.apache.log4j.Logger
import su.msk.dunno.scage.single.support.ScageProperties.property
import collection.mutable.HashMap
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.Vec
import io.BytePickle.Def

object ScageTracer {
  private val log = Logger.getLogger(this.getClass);

  private var next_trace_id = 0
  def nextTraceID:Int = {
    next_trace_id += 1
    next_trace_id
  }
}

import ScageTracer._

trait Trace {
  val id = nextTraceID

  val point:Vec = Vec(-1,-1)

  def getState:State
  def changeState(changer:Trace, state:State)
}

class ScageTracer[T <: Trace](val field_from_x:Int        = property("field.from.x", 0),
                              val field_to_x:Int          = property("field.to.x", width),
                              val field_from_y:Int        = property("field.from.y", 0),
                              val field_to_y:Int          = property("field.to.y", height),
                              val N_x:Int                 = property("field.N_x", width/50),
                              val N_y:Int                 = property("field.N_y", height/50),
                              val are_solid_edges:Boolean = property("field.solid_edges", true)) {
  protected val log = Logger.getLogger(this.getClass);

  log.debug("creating tracer "+this.getClass.getName)

  val field_width = field_to_x - field_from_x
  val field_height = field_to_y - field_from_y

  val h_x = field_width/N_x
  val h_y = field_height/N_y                         
  
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

  def pointCenter(p:Vec):Vec = Vec(field_from_x + p.x*h_x + h_x/2, field_from_y + p.y*h_y + h_y/2)

  protected def initMatrix(matrix:Array[Array[List[T]]]) {
    for(i <- 0 until matrix.length; j <- 0 until matrix.head.length) {matrix(i)(j) = Nil}
  }

  protected val point_matrix = Array.ofDim[List[T]](N_x, N_y)
  initMatrix(point_matrix)
  protected val traces_in_point = new HashMap[Int, Vec]()

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

  def removeTraces(traces:T*) { // TODO: add log messages and return result
    if(traces.size > 0) {
      traces.foreach(trace => {
        point_matrix(trace.point.ix)(trace.point.iy) = point_matrix(trace.point.ix)(trace.point.iy).filterNot(_.id == trace.id)
        traces_in_point -= trace.id
      })
    }
    else {
      initMatrix(point_matrix)
      traces_in_point.clear()
      log.info("deleted all traces")
    }
  }

  def tracesInPoint(point:Vec, condition:(T) => Boolean) = {
    if(!isPointOnArea(point)) Nil
    else {
      for{
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

  // TODO: a bug here: returning value can be (-1; -1)
  def randomPoint(leftup_x:Int = 0, leftup_y:Int = N_y, width:Int = N_x, height:Int = N_y) = {
    val x = leftup_x + (math.random*width).toInt
    val y = leftup_y - (math.random*height).toInt
    Vec(x,y)
  }
  def randomCoord(leftup_x:Int = 0, leftup_y:Int = field_to_y, width:Int = field_to_x - field_from_x, height:Int = field_to_y - field_from_y) = {
    randomPoint(leftup_x, leftup_y, width, height)
  }
}
