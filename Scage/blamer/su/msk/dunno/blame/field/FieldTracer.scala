package su.msk.dunno.blame.field

import su.msk.dunno.screens.support.tracer.{Tracer, Trace}
import su.msk.dunno.scage.support.{Vec, Color}
import su.msk.dunno.screens.handlers.Renderer
import rlforj.los.{ILosBoard, PrecisePermissive}

trait FieldObject extends Trace {
  def getSymbol:Int
  def getColor:Color
  def isTransparent:Boolean
  def isPassable:Boolean
  
  private var was_drawed = false
  def draw = {
    Renderer.drawDisplayList(getSymbol, getCoord, getColor)
    was_drawed = true
  }
  def wasDrawed = was_drawed
}

class FieldTracer(game_from_x:Int = 0, game_to_x:Int = 800,
                  game_from_y:Int = 0, game_to_y:Int = 600,
                  N_x:Int = 20, N_y:Int = 15,
                  are_solid_edges:Boolean = true)
extends Tracer[FieldObject] (game_from_x, game_to_x, game_from_y, game_to_y, N_x, N_y, are_solid_edges) {
  def onArea(x:Int, y:Int) = {
    x >= 0 && x < N_x && y >= 0 && y < N_y
  }

  def isPointPassable(x:Int, y:Int):Boolean = {
    onArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isPassable))
  }
  def isPointPassable(point:Vec):Boolean = isPointPassable(point.ix, point.iy)
  
  def isPointTransparent(x:Int, y:Int) = {
    onArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isTransparent))  
  }
  
  def isLocationPassable(coord:Vec) = {
    val p = point(coord)
    isPointPassable(p.ix, p.iy)
  }

  def getRandomPassablePoint:Vec = {
    log.debug("looking for new random passable point")

    var x = -1
    var y = -1

    var count = 10
    while(!isPointPassable(x, y) && count > 0) {
      x = (math.random*N_x).toInt
      y = (math.random*N_y).toInt

      count -= 1
    }
    if(count == 0 && !isPointPassable(x, y))
      log.warn("warning: cannot locate random passable point within "+count+" tries")

    Vec(x, y)
  }
  
  def move2PointIfPassable(trace_id:Int, old_point:Vec, new_point:Vec) = {
    if(isPointPassable(new_point)) {
      val old_coord = pointCenter(old_point)
      val new_coord = pointCenter(new_point)
      updateLocation(trace_id, old_coord, new_coord)
      old_point is new_point
      true
    }
    else false
  }
  
  def neighboursOfPoint(trace_id:Int, point:Vec, range:Range) = {
    neighbours(trace_id, pointCenter(point), -1 to 1, (f) => true)
  }

  private var lightSources:List[() => Vec] = Nil
  def addLightSource(coord: => Vec) = lightSources = (() => coord) :: lightSources
  
  private val pp = new PrecisePermissive();  
  private val drawView = new RL4JMapView() {
    def isObstacle(x:Int, y:Int):Boolean = !isPointTransparent(x, y);

    def visit(x:Int, y:Int) = {
      if(matrix(x)(y).length > 0) matrix(x)(y).head.draw
    }   
  }
  
  def drawField(player_point:Vec) = {
    lightSources.foreach(source => {
      pp.visitFieldOfView(drawView, source().ix, source().iy, 5)  
    })
  }

  def drawField = {
    for(x <- 0 to N_x-1) {
      for(y <- 0 to N_y-1) {
        if(matrix(x)(y).length > 0) matrix(x)(y).head.draw
      }
    }
  }
  
  private[FieldTracer] abstract class RL4JMapView extends ILosBoard {
    def contains(x:Int, y:Int):Boolean = x >= 0 && x < N_x && y >= 0 && y < N_y
  }
}
