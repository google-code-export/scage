package su.msk.dunno.scage.screens.physics.support.objects

import net.phys2d.raw.shapes.Line
import net.phys2d.math.Vector2f
import net.phys2d.raw.StaticBody
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.physics.Physical

class StaticLine(start:Vec, end:Vec) extends Physical {
  val line = new Line((end-start).x, (end-start).y)

  val body = new StaticBody("line", line)
  body.setRestitution(1.0f)
  body.setPosition(start.x, start.y)

  def points = {
    val verts:Array[Vector2f] = line.getVertices(body.getPosition(), body.getRotation());
    for(v <- verts) yield Vec(v.getX(), v.getY())
  }

  /*def render() {
    val verts:Array[Vector2f] = line.getVertices(body.getPosition(), body.getRotation());
    Renderer.color = WHITE
    Renderer.drawLine(Vec(verts(0).getX, verts(0).getY),
                      Vec(verts(1).getX, verts(1).getY))
  }*/
}