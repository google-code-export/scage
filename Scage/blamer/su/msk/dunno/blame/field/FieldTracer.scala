package su.msk.dunno.blame.field

import su.msk.dunno.screens.support.tracer.{Tracer, Trace}
import su.msk.dunno.scage.support.{Vec, Color}

trait FieldObject extends Trace {
  def getSymbol:Int
  def getColor:Color
  def isTransparent:Boolean
  def isPassable:Boolean
}

class FieldTracer(game_from_x:Int = 0, game_to_x:Int = 800,
                  game_from_y:Int = 0, game_to_y:Int = 600,
                  N_x:Int = 20, N_y:Int = 15,
                  are_solid_edges:Boolean = true)
extends Tracer[FieldObject] (game_from_x, game_to_x, game_from_y, game_to_y, N_x, N_y, are_solid_edges) {
  def onArea(x:Int, y:Int) = {
    x >= 0 && x < N_x-1 && y >= 0 && y < N_y-1
  }

  def isPassable(x:Int, y:Int) = {
    onArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isPassable))
  }

  def getRandomPassablePoint:Vec = {
    log.debug("looking for new random passable point")

    var x = -1
    var y = -1

    var count = 10
    while(!isPassable(x, y) && count > 0) {
      x = (math.random*N_x).toInt
      y = (math.random*N_y).toInt

      count -= 1
    }
    if(count == 0 && !isPassable(x, y))
      log.warn("warning: cannot locate random passable point within "+count+" tries")

    Vec(x, y)
  }
}
