package su.msk.dunno.scage.objects

import su.msk.dunno.scage.support.Vec
class Movable(val coord:Vec,
              val velocity:Vec,
              val acceleration:Vec,
              val update: () => Vec,
              val renderFunc: Vec => Unit) {
  /*def evolve():Movable = {
    /*val new_coord = coord + velocity*Engine.dt
    val new_velocity = velocity + acceleration*Engine.dt
    val new_acceleration = update()
    new Movable(new_coord, new_velocity, new_acceleration, update, renderFunc)*/
  }*/

  def render() = renderFunc(coord)

  def ::(o:Movable) = o :: List[Movable](this)
}