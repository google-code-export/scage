package su.msk.dunno.scage.support.tracer

import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.{Colors, ScageProperties, Vec}
import org.apache.log4j.Logger

object Tracer {
  private val log = Logger.getLogger(this.getClass);

  private var current_tracer:Tracer[_] = null
  def currentTracer = current_tracer

  private var next_trace_id = 0
  def nextTraceID = {
    val trace_id = next_trace_id
    next_trace_id += 1
    trace_id
  }
}

class Tracer[S <: State] extends Colors {
  private val log = Logger.getLogger(this.getClass);

  Tracer.current_tracer = this
  log.debug("using tracer "+this.getClass.getName)

  val game_from_x = ScageProperties.intProperty("game_from_x", 0)
  val game_to_x = ScageProperties.intProperty("game_to_x", 800)
  val game_from_y = ScageProperties.intProperty("game_from_y", 0)
  val game_to_y = ScageProperties.intProperty("game_to_y", 600)

  val game_width = game_to_x - game_from_x
  val game_height = game_to_y - game_from_y

  val N_x = ScageProperties.intProperty("N_x", 20)
  val N_y = ScageProperties.intProperty("N_y", 15)

  private var coord_matrix = Array.ofDim[List[Trace[S]]](N_x, N_y)
  for(i <- 0 to N_x-1) {
    for(j <- 0 to N_y-1) {
      coord_matrix(i)(j) = Nil
    }
  }
  def matrix = coord_matrix

  val h_x = game_width/N_x
	val h_y = game_height/N_y
  if(ScageProperties.booleanProperty("show_grid", true)) {
	  Renderer.addRender(() => {
	 	  Renderer.setColor(LIME_GREEN)
	 	  for(i <- 0 to N_x) Renderer.drawLine(Vec(i*h_x + game_from_x, game_from_y), Vec(i*h_x + game_from_x, game_to_y))
	 	  for(j <- 0 to N_y) Renderer.drawLine(Vec(game_from_x, j*h_y + game_from_y), Vec(game_to_x, j*h_y + game_from_y))
	  })
  }

  def addTrace(t:Trace[S]) = {
    val p = if(are_solid_edges) point(t.getCoord()) else checkPointEdges(point(t.getCoord()))
    if(isPointOnArea(p)) {
      coord_matrix(p.ix)(p.iy) = t :: coord_matrix(p.ix)(p.iy)
      log.debug("added new trace #"+t.id)
    }
    else log.debug("failed to add trace #"+t.id+": coord "+t.getCoord+" is out of area")
    t.id
  }

  def point(v:Vec):Vec = Vec(((v.x - game_from_x)/game_width*N_x).toInt,
                              ((v.y - game_from_y)/game_height*N_y).toInt)
  def pointCenter(p:Vec):Vec = Vec(game_from_x + p.x*h_x + h_x/2, game_from_y + p.y*h_y + h_y/2)
  def pointCenter(x:Int, y:Int):Vec = Vec(game_from_x + x*h_x + h_x/2, game_from_y + y*h_y + h_y/2)
  
  def getNeighbours(coord:Vec, range:Range):List[Trace[S]] = {
    val p = point(coord)
    var neighbours = List[Trace[S]]()
    for(i <- range) {
    	for(j <- range) {
        val near_point = checkPointEdges(p + Vec(i, j))
    		neighbours = coord_matrix(near_point.ix)(near_point.iy).foldLeft(List[Trace[S]]())((acc, trace) => {
    		  if(trace.isActive && trace.getCoord != coord) trace :: acc
    			else acc
    		}) ::: neighbours
    	}
    }
    neighbours
  }

  def getNeighbours(trace_id:Int, coord:Vec, range:Range):List[Trace[S]] = {
    val p = point(coord)
    var neighbours:List[Trace[S]] = Nil
    for(i <- range) {
    	for(j <- range) {
        val near_point = checkPointEdges(p + Vec(i, j))
    		neighbours = coord_matrix(near_point.ix)(near_point.iy).foldLeft(List[Trace[S]]())((acc, trace) => {
    		  if(trace.isActive && trace.id != trace_id) trace :: acc
    			else acc
    		}) ::: neighbours
    	}
    }
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

  val are_solid_edges = ScageProperties.booleanProperty("solid_edges")
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

  def hasCollisions(trace_id:Int, coord:Vec, range:Range, min_dist:Float, excluded_traces:List[Int]) = {
    if(are_solid_edges && !isCoordOnArea(coord)) true
    else {
      val coord_edges_affected = checkEdges(coord)
      val min_dist2 = min_dist*min_dist
      getNeighbours(trace_id, coord_edges_affected, range).foldLeft(false)((is_collision, neighbour) => {
        (!excluded_traces.contains(neighbour.id) && neighbour.id != trace_id && (neighbour.getCoord dist2 coord_edges_affected) < min_dist2) || is_collision
      })
    }
  }
}