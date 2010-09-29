package su.msk.dunno.scage.support.tracer

import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.{Colors, ScageProperties, Vec}

class Tracer[S <: State] extends Colors {
  val game_from_x = ScageProperties.intProperty("game_from_x")
  val game_to_x = ScageProperties.intProperty("game_to_x")
  val game_from_y = ScageProperties.intProperty("game_from_y")
  val game_to_y = ScageProperties.intProperty("game_to_y")

  val game_width = game_to_x - game_from_x
  val game_height = game_to_y - game_from_y

  val N_x = ScageProperties.intProperty("N_x")
  val N_y = ScageProperties.intProperty("N_y")

  private var object_points:List[(Point, Trace[S])] = List[(Point, Trace[S])]()
  private var coord_matrix = Array.ofDim[List[Trace[S]]](N_x, N_y)
  for(i <- 0 to N_x-1) {
    for(j <- 0 to N_y-1) {
      coord_matrix(i)(j) = List[Trace[S]]()
    }
  }

  def addTrace(t:Trace[S]) = {
    val p = point(t.getCoord())
    if(p.x >= 0 && p.y < N_x && p.x >= 0 && p.y < N_y/* && !coord_matrix(p._1)(p._2).contains(coord)*/) {
      coord_matrix(p.x)(p.y) = t :: coord_matrix(p.x)(p.y)
      object_points = (point(t.getCoord), t) :: object_points  
    }
  }

  def point(v:Vec):Point = Point(((v.x - game_from_x)/game_width*N_x).toInt,
                                 ((v.y - game_from_y)/game_height*N_y).toInt)
  
  def getNeighbours(coord:Vec, r:Range):List[Trace[S]] = {
    val p = point(coord)
    var neighbours = List[Trace[S]]()
    for(i <- r) {
    	for(j <- r) {
    		if(p.x+i >= 0 && p.x+i < N_x && p.y+j >= 0 && p.y+j < N_y) {
    			val x = p.x+i
    			val y = p.y+j
    			neighbours = coord_matrix(x)(y).foldLeft(List[Trace[S]]())((acc, trace) => {
    				if(trace.getCoord() != coord) trace :: acc
    				else acc
    			}) ::: neighbours
    		}
    	}
    }
    neighbours
  }

  if(ScageProperties.booleanProperty("show_grid")) {
    val h_x = game_width/N_x
	  val h_y = game_height/N_y
	  Renderer.addRender(() => {
	 	  Renderer.setColor(LIME_GREEN)
	 	  for(i <- 0 to N_x) Renderer.drawLine(Vec(i*h_x + game_from_x, game_from_y), Vec(i*h_x + game_from_x, game_to_y))
	 	  for(j <- 0 to N_y) Renderer.drawLine(Vec(game_from_x, j*h_y + game_from_y), Vec(game_to_x, j*h_y + game_from_y))
	  })
  }

  /*override def actionSequence() = {
    object_points = object_points.map(obj => {
      val old_p = obj._1
      val new_p = point(obj._2.getCoord)
      if(old_p != new_p) {
        coord_matrix(old_p.x)(old_p.y) = coord_matrix(old_p.x)(old_p.y).filter(trace => trace != obj._2)
        coord_matrix(new_p.x)(new_p.y) = obj._2 :: coord_matrix(new_p.x)(new_p.y)
      }
      (new_p, obj._2)
    })
  }*/

  def checkEdges(coord:Vec):Vec = {
    def checkC(c:Float, from:Float, to:Float, dist:Float):Float = {
      if(c >= to) checkC(c - dist, from, to, dist)
      else if(c < from) checkC(c + dist, from, to, dist)
      else c
    }
    val x = checkC(coord.x, game_from_x, game_to_x, game_width)
    val y = checkC(coord.y, game_from_y, game_to_y, game_height)
    Vec(x, y)
  }

  def updateLocation(old_coord:Vec, new_coord:Vec) = {
    val new_coord_edges_affected = checkEdges(new_coord)
    val old_p = point(old_coord)
    val new_p = point(new_coord_edges_affected)
    if(old_p != new_p) {
      coord_matrix(old_p.x)(old_p.y).find(trace => trace.getCoord == old_coord) match {
        case Some(target_trace) => {
          coord_matrix(old_p.x)(old_p.y) = coord_matrix(old_p.x)(old_p.y).filter(trace => trace.getCoord != old_coord)
          coord_matrix(new_p.x)(new_p.y) = target_trace :: coord_matrix(new_p.x)(new_p.y)
        }
        case _ =>
      }
    }
    new_coord_edges_affected
  }

  case class Point(val x:Int, val y:Int) {
    def ==(p:Point) = x == p.x && y == p.y
    def !=(p:Point) = null == p || x != p.x || y != p.y
    override def toString = x+":"+y
  }
}