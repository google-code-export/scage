package su.msk.dunno.scage.objects

import su.msk.dunno.scage.prototypes.Physical
import net.phys2d.raw.Body
import net.phys2d.raw.shapes.Circle
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.{Color, Vec}

class DynaBall(init_coord:Vec, radius:Int) extends Physical {
  val body = new Body(new Circle(radius), 2);
  body.setPosition(init_coord.x, init_coord.y)

  override def render() = {
    Renderer.setColor(Color.BLACK)
    Renderer.drawCircle(coord, radius)
    Renderer.drawLine(coord, coord+velocity)
  }
}