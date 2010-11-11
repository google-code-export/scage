package su.msk.dunno.screens.support.tracer

import su.msk.dunno.scage.support.Vec
import org.apache.log4j.Logger
import su.msk.dunno.screens.support.ScageLibrary._

object Tracer {
  private val log = Logger.getLogger(this.getClass);

  private var next_trace_id = 0
  private[tracer] def nextTraceID = {
    val trace_id = next_trace_id
    next_trace_id += 1
    trace_id
  }
}

import Tracer._

class Tracer[T <: Trace](val game_from_x:Int = 0, val game_to_x:Int = 800,
                         val game_from_y:Int = 0, val game_to_y:Int = 600,
                         val N_x:Int = 20, val N_y:Int = 15,
                         val are_solid_edges:Boolean = true) {
  protected val log = Logger.getLogger(this.getClass);

  log.info("using tracer "+this.getClass.getName)

  val game_width = game_to_x - game_from_x
  val game_height = game_to_y - game_from_y

  private var coord_matrix = Array.ofDim[List[T]](N_x, N_y)
  (0 to N_x-1).foreachpair(0 to N_y-1) ((i, j) => coord_matrix(i)(j) = Nil)
  def matrix = coord_matrix

  val h_x = game_width/N_x
	val h_y = game_height/N_y

  def addTrace(t:T) = {
    val p = point(t.getCoord())
    if(isPointOnArea(p)) {
      coord_matrix(p.ix)(p.iy) = t :: coord_matrix(p.ix)(p.iy)
      t._id = nextTraceID
      log.debug("added new trace #"+t.id+" in coord ("+t.getCoord+")")
    }
    else log.error("failed to add trace: coord ("+t.getCoord+") is out of area")
    t.id
  }

  def point(v:Vec):Vec = Vec(((v.x - game_from_x)/game_width*N_x).toInt,
                              ((v.y - game_from_y)/game_height*N_y).toInt)
  def pointCenter(p:Vec):Vec = Vec(game_from_x + p.x*h_x + h_x/2, game_from_y + p.y*h_y + h_y/2)
  def pointCenter(x:Int, y:Int):Vec = Vec(game_from_x + x*h_x + h_x/2, game_from_y + y*h_y + h_y/2)

  def getNeighbours(coord:Vec, range:Range):List[T] = {
    val p = point(coord)
    var neighbours = List[T]()
    range.foreachpair((i, j) => {
      val near_point = checkPointEdges(p + Vec(i, j))
      neighbours = coord_matrix(near_point.ix)(near_point.iy).foldLeft(List[T]())((acc, trace) => {
    	  if(trace.getCoord != coord) trace :: acc
    		else acc
    	}) ::: neighbours
    })
    neighbours
  }

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

  private def checkPointEdges(p:Vec):Vec = {
    def checkC(c:Float, dist:Int):Float = {
      if(c >= dist) checkC(c - dist, dist)
      else if(c < 0) checkC(c + dist, dist)
      else c
    }
    Vec(checkC(p.x, N_x), checkC(p.y, N_y))
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
          }
          case _ =>
        }
      }
      old_coord is new_coord_edges_affected
      true
    }
  }

  def checkEdges(coord:Vec):Vec = {
    def checkC(c:Float, from:Float, to:Float):Float = {
      val dist = to - from
      if(c >= to) checkC(c - dist, from, to)
      else if(c < from) checkC(c + dist, from, to)
      else c
    }
    val x = checkC(coord.x, game_from_x, game_to_x)
    val y = checkC(coord.y, game_from_y, game_to_y)
    Vec(x, y)
  }

  def isCoordOnArea(coord:Vec) = {
    coord.x >= game_from_x && coord.x < game_to_x && coord.y >= game_from_y && coord.y < game_to_y
  }

  def isPointOnArea(point:Vec) = point.x >= 0 && point.x < N_x && point.y >= 0 && point.y < N_y

  def hasCollisions(trace_id:Int, coord:Vec, range:Range, min_dist:Float, condition:(T) => Boolean) = {
    if(are_solid_edges && !isCoordOnArea(coord)) true
    else {
      val coord_edges_affected = checkEdges(coord)
      val min_dist2 = min_dist*min_dist
      neighbours(trace_id, coord_edges_affected, range, condition).exists(neighbour =>
        (neighbour.getCoord dist2 coord_edges_affected) < min_dist2)
    }
  }
}