package su.msk.dunno.blame.tiles

import su.msk.dunno.screens.support.tracer.{Tracer, Trace}
import su.msk.dunno.scage.support.Color

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
extends Tracer[FieldObject] (game_from_x, game_to_x, game_from_y, game_to_y, N_x, N_y, are_solid_edges)
