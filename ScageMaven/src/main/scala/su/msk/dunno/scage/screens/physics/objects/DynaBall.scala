package su.msk.dunno.scage.screens.physics.objects

import su.msk.dunno.scage.single.support.ScageColors._
import net.phys2d.raw.Body
import su.msk.dunno.scage.single.support.Vec
import net.phys2d.raw.shapes.Circle
import su.msk.dunno.scage.screens.handlers.Renderer
import su.msk.dunno.scage.screens.physics.Physical

class DynaBall(init_coord:Vec, val radius:Int) extends Physical {
  val body = new Body(new Circle(radius), 1)
  body.setRestitution(1.0f)
  body.setPosition(init_coord.x, init_coord.y)

  def render {
    Renderer.color = WHITE
    Renderer.drawCircle(coord, radius)
  }
}