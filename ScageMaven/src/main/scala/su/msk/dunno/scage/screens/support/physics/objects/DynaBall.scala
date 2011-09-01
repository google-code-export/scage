package su.msk.dunno.scage.screens.support.physics.objects

import net.phys2d.raw.Body
import su.msk.dunno.scage.single.support.Vec
import net.phys2d.raw.shapes.Circle
import su.msk.dunno.scage.screens.support.physics.Physical
import su.msk.dunno.scage.single.support.ScageProperties._
import java.lang.Float

class DynaBall(init_coord:Vec, val radius:Int, mass:Float = 1) extends Physical {
  val body = new Body(new Circle(radius), mass)
  if(property("physics.restitution", false)) body.setRestitution(1.0f)
  body.setPosition(init_coord.x, init_coord.y)

  def points = Array(coord)

  /*def render() {
    color = WHITE
    drawCircle(coord, radius)
  }*/
}