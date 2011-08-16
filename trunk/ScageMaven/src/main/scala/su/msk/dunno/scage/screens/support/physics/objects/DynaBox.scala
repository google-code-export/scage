package su.msk.dunno.scage.screens.support.physics.objects

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.physics.Physical
import net.phys2d.raw.Body
import net.phys2d.raw.shapes.Box

class DynaBox(leftup_coord:Vec, width:Float, height:Float) extends Physical {
  val box = new Box(width, height)
  val body = new Body(box, 1)
  body.setPosition(leftup_coord.x+width/2, leftup_coord.y-height/2)

  def points = {
    val verts = box.getPoints(body.getPosition, body.getRotation);
    for(v <- verts) yield Vec(v.getX, v.getY)
  }
}