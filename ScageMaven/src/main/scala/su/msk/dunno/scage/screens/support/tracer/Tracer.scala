package su.msk.dunno.scage.screens.support.tracer

import org.apache.log4j.Logger
import su.msk.dunno.scage.screens.support.ScageLibrary._
import su.msk.dunno.scage.single.support.{Vec}

object Tracer {
  private val log = Logger.getLogger(this.getClass);

  private var next_trace_id = 0
  /*private[tracer] */def nextTraceID:Int = {
    try {
      next_trace_id
    }
    finally {
      next_trace_id += 1
    }

  }
}

import Tracer._

trait Trace {
  private val _id = nextTraceID
  def id = _id
  def getCoord:Vec
  def getState:State
  def changeState(state:State):Unit
}

class Tracer[T <: Trace](val field_from_x:Int = property("field.from.x", 0), 
                         val field_to_x:Int = property("field.to.x", 800),
                         val field_from_y:Int = property("field.from.y", 0), 
                         val field_to_y:Int = property("field.to.y", 600),
                         val N_x:Int = property("field.N_x", 16),
                         val N_y:Int = property("field.N_y", 12),
                         val are_solid_edges:Boolean = property("field.solid_edges", true)) {
  protected val log = Logger.getLogger(this.getClass);

  log.info("creating tracer "+this.getClass.getName)

  val field_width = field_to_x - field_from_x
  val field_height = field_to_y - field_from_y

  protected var coord_matrix = Array.ofDim[List[T]](N_x, N_y)
  (0 to N_x-1).foreachpair(0 to N_y-1) ((i, j) => coord_matrix(i)(j) = Nil)
  //def matrix = coord_matrix

  val h_x = field_width/N_x
	val h_y = field_height/N_y

  def addTrace(t:T) = {
    val p = point(t.getCoord)
    if(isPointOnArea(p)) {
      coord_matrix(p.ix)(p.iy) = t :: coord_matrix(p.ix)(p.iy)
      log.debug("added new trace #"+t.id+" in coord ("+t.getCoord+")")
    }
    else log.warn("failed to add trace: coord ("+t.getCoord+") is out of area")
    t.id
  }

  def removeTrace(trace_id:Int, coord:Vec) = {
    removeTraceFromPoint(trace_id, point(coord))
  }
  def removeTraceFromPoint(trace_id:Int, point:Vec) = {
    coord_matrix(point.ix)(point.iy) = coord_matrix(point.ix)(point.iy).filterNot(_.id == trace_id)
  }

  def point(v:Vec):Vec = Vec(((v.x - field_from_x)/field_width*N_x).toInt,
                              ((v.y - field_from_y)/field_height*N_y).toInt)
  def pointCenter(p:Vec):Vec = Vec(field_from_x + p.x*h_x + h_x/2, field_from_y + p.y*h_y + h_y/2)
  def pointCenter(x:Int, y:Int):Vec = Vec(field_from_x + x*h_x + h_x/2, field_from_y + y*h_y + h_y/2)

  def neighbours(trace_id:Int, coord:Vec, range:Range, condition:(T) => Boolean):List[T] = {
    val p = point(coord)
    var neighbours:List[T] = Nil
    range.foreachpair((i, j) => {
      val near_point = checkPointEdges(p + Vec(i, j))
    	neighbours = coord_matrix(near_point.ix)(near_point.iy).foldLeft(List[T]())((acc, trace) => {
    	  if(condition(trace) && trace.id != trace_id) trace :: acc
    		else acc
    	}) ::: neighbours
    })
    neighbours
  }

  def updateLocation(trace_id:Int, old_coord:Vec, new_coord:Vec):Boolean = {
    if(are_solid_edges && !isCoordOnArea(new_coord)) false
    else {
      val new_coord_edges_affected = checkEdges(new_coord)
      val old_p = point(old_coord)
      val new_p = point(new_coord_edges_affected)
      if(old_p != new_p) {
        coord_matrix(old_p.ix)(old_p.iy).find(trace => trace.id == trace_id) match {
          case Some(target_trace) => {
            coord_matrix(old_p.ix)(old_p.iy) = coord_matrix(old_p.ix)(old_p.iy).filter(trace => trace.id != trace_id)
            coord_matrix(new_p.ix)(new_p.iy) = target_trace :: coord_matrix(new_p.ix)(new_p.iy)

            old_coord is new_coord_edges_affected
            true
          }
          case None => false
        }
      }
      else true
    }
  }

  protected def checkEdges(coord:Vec):Vec = {
    def checkC(c:Float, from:Float, to:Float):Float = {
      val dist = to - from
      if(c >= to) checkC(c - dist, from, to)
      else if(c < from) checkC(c + dist, from, to)
      else c
    }
    val x = checkC(coord.x, field_from_x, field_to_x)
    val y = checkC(coord.y, field_from_y, field_to_y)
    Vec(x, y)
  }

  def isCoordOnArea(coord:Vec) = {
    coord.x >= field_from_x && coord.x < field_to_x && coord.y >= field_from_y && coord.y < field_to_y
  }

  def hasCollisions(trace_id:Int, coord:Vec, range:Range, min_dist:Float, condition:(T) => Boolean) = {
    if(are_solid_edges && !isCoordOnArea(coord)) true
    else {
      val coord_edges_affected = checkEdges(coord)
      val min_dist2 = min_dist*min_dist
      neighbours(trace_id, coord_edges_affected, range, condition).exists(neighbour =>
        (neighbour.getCoord dist2 coord_edges_affected) < min_dist2)
    }
  }

  def isPointOnArea(point:Vec):Boolean = isPointOnArea(point.ix, point.iy)
  def isPointOnArea(x:Int, y:Int) = {
    x >= 0 && x < N_x && y >= 0 && y < N_y
  }

  protected def checkPointEdges(point:Vec):Vec = {
    def checkC(c:Float, dist:Int):Float = {
      if(c >= dist) checkC(c - dist, dist)
      else if(c < 0) checkC(c + dist, dist)
      else c
    }
    Vec(checkC(point.x, N_x), checkC(point.y, N_y))
  }
}
