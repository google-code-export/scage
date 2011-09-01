package su.msk.dunno.scage.screens.support.physics.objects

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.physics.Physical
import net.phys2d.raw.Body
import net.phys2d.raw.shapes.Box
import java.lang.Float

class DynaBox(init_coord:Vec, val box_width:Float, val box_height:Float, val box_mass:Float = 1) extends Physical {
  val box = new Box(box_width, box_height)
  val body = new Body(box, box_mass)
  body.setPosition(init_coord.x, init_coord.y)

  def points = {
    val verts = box.getPoints(body.getPosition, body.getRotation);
    for(v <- verts) yield Vec(v.getX, v.getY)
  }
}