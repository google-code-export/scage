package su.msk.dunno.scage.objects

import net.phys2d.raw.Body
import org.lwjgl.opengl.GL11
import net.phys2d.raw.shapes.Box
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.handlers.{Physics, Renderer}
import su.msk.dunno.scage.prototypes.Physical

class DynaBox(val leftup_coord:Vec, width:Float, height:Float) extends Physical {
  val box = new Box(width, height)
  val body = new Body(box, 1)
  body.setPosition(leftup_coord.x+width/2, leftup_coord.y-height/2)
  Physics.addBody(body)

  Renderer.addRender(() => render())
  protected def render() = {
    val verts:Array[Vector2f] = box.getPoints(body.getPosition(), body.getRotation());
    Renderer.setColor(Color.BLACK)
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINE_LOOP);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
}