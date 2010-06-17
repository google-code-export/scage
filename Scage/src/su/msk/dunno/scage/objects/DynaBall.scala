package su.msk.dunno.scage.objects

import net.phys2d.raw.Body
import net.phys2d.raw.shapes.Circle
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.prototypes.Physical
import su.msk.dunno.scage.handlers.{Tracer, Physics, Renderer}
class DynaBall(init_coord:Vec, radius:Int, val enableRender:Boolean = true) extends Physical {
  val body = new Body(new Circle(radius), 2);
  body.setPosition(init_coord.x, init_coord.y)
  Physics.addBody(body)
  Tracer.addTrace(coord)

  Renderer.addRender(() => render())
  protected def render() = {
    Renderer.setColor(Color.BLACK)
    Renderer.drawCircle(coord, radius)
//`    Message.print(Tracer.point(coord), coord)
    Renderer.drawLine(coord, coord+velocity)
  }
}