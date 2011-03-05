package su.msk.dunno.scar

import net.phys2d.raw.StaticBody
import net.phys2d.raw.shapes.Box
import net.phys2d.math.Vector2f
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.screens.handlers.{Renderer}
import su.msk.dunno.scage.single.support.{Vec}
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.prototypes.ScageRender

class StaticBox(leftup_coord:Vec, width:Float, height:Float) extends Physical {
  val box = new Box(width, height)

  val body = new StaticBody("StaticBox", box)
  body.setRestitution(1.0f)
  body.setPosition(leftup_coord.x+width/2, leftup_coord.y-height/2)

  def renderFunc = {
    val verts:Array[Vector2f] = box.getPoints(body.getPosition(), body.getRotation());
    Renderer.color = WHITE
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
}