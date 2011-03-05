package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageColors._
import net.phys2d.raw.Body
import su.msk.dunno.scage.single.support.Vec
import net.phys2d.raw.shapes.Circle
import su.msk.dunno.scage.screens.prototypes.{ScageRender, ScageAction}
import su.msk.dunno.scage.screens.handlers.Renderer

class DynaBall(init_coord:Vec, val radius:Int) extends Physical {
  val body = new Body(new Circle(radius), 1)
  body.setRestitution(1.0f)
  body.setPosition(init_coord.x, init_coord.y)

  def renderFunc = {
    Renderer.color = WHITE
    Renderer.drawCircle(coord, radius)
  }
}