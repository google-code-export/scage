package su.msk.dunno.scage.objects

import su.msk.dunno.scage.prototypes.Physical
import net.phys2d.raw.Body
import org.lwjgl.opengl.GL11
import net.phys2d.raw.shapes.Box
import net.phys2d.math.{Vector2f}
import util.Random
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.support.messages.TrueTypeFont

class DynaBox(val init_coord:Vec,width:Float, height:Float) extends Physical {
  val box = new Box(width, height)
  val body = new Body(box, 1)
  body.setPosition(init_coord.x, init_coord.y)
  body.setRotation((Random.nextFloat() * 2 * Math.Pi).toFloat);

  override def render() = {
    val verts:Array[Vector2f] = box.getPoints(body.getPosition(), body.getRotation());
    Renderer.setColor(Color.BLACK)
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_POLYGON);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    TrueTypeFont.instance.drawString(body.getVelocity.getX+": "+body.getVelocity.getY,body.getPosition.getX,body.getPosition.getY)
  }
}