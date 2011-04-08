package su.msk.dunno.scage.screens.support.newtracer

import org.apache.log4j.Logger
import su.msk.dunno.scage.single.support.Vec
import collection.mutable.HashMap
import su.msk.dunno.scage.single.support.ScageProperties.property

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
  def getPoint:Vec
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
    Vec(checkC(point.x, N_x), checkC(point.y, N_y))
  }
  
  def point(v:Vec):Vec = Vec(((v.x - field_from_x)/field_width*N_x).toInt,
                             ((v.y - field_from_y)/field_height*N_y).toInt)
  def pointCenter(p:Vec):Vec = Vec(field_from_x + p.x*h_x + h_x/2, field_from_y + p.y*h_y + h_y/2)
  
  
  private var point_matrix = Array.ofDim[List[T]](N_x, N_y)
  for(i <- 0 until N_x; j <- 0 until N_y) {point_matrix(i)(j) = Nil}
  private val traces_in_point = new HashMap[Int, Vec]()
  def addTrace(t:T) = {
    val p = t.getPoint
    if(isPointOnArea(p)) {
      point_matrix(p.ix)(p.iy) = t :: point_matrix(p.ix)(p.iy)
      traces_in_point += (t.id -> p)
      log.debug("added new trace #"+t.id+" in point ("+t.getPoint+")")
    }
    else log.warn("failed to add trace: point ("+t.getPoint+") is out of area")
    t.id
  }
  def removeTrace(trace_id:Int) = {
    traces_in_point.find(_._1 == trace_id) match {
      case Some((id, p)) => {
        point_matrix(p.ix)(p.iy).find(_.id == trace_id) match {
          case Some(trace) => {
            point_matrix(p.ix)(p.iy).filterNot(_.id == trace_id)
            traces_in_point -= trace_id
            Some(trace)
          }
          case None => None
        }  
      }
      case None => None
    }
  }
  
  def neighbours(trace_id:Int, range:Range, condition:(T) => Boolean):List[T] = {
    if(traces_in_point.contains(trace_id)) {
      val p = traces_in_point(trace_id)
      (for {
        i <- range
        j <- range
        near_point = checkPointEdges(p + Vec(i, j))
        trace <- point_matrix(near_point.ix)(near_point.iy)
        if condition(trace) && trace.id != trace_id
      } yield trace).toList
    }
    else Nil
  }
  
  def updateLocation(trace_id:Int):Boolean = {
    if(are_solid_edges &&
       traces_in_point.contains(trace_id) && !isPointOnArea(traces_in_point(trace_id))) false
    else {
      removeTrace(trace_id) match {
        case Some(trace) => {
      	  addTrace(trace)
      	  true
        }
        case None => false
      }
    }
  }
}
