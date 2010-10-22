package su.msk.dunno.scage.objects

import net.phys2d.raw.shapes.Line
import net.phys2d.math.Vector2f
import org.lwjgl.opengl.GL11
import net.phys2d.raw.StaticBody
import su.msk.dunno.scage.handlers.{Physics, Renderer}
import su.msk.dunno.scage.prototypes.Physical
import su.msk.dunno.scage.support.{Colors, Vec}

class StaticLine(val start:Vec, val end:Vec) extends Physical with Colors {
  val line = new Line((end-start).x, (end-start).y)
  val body = new StaticBody("line", line)
  body.setPosition(start.x, start.y)
  Physics.addBody(body)

  Renderer.addRender(() => {
    val verts:Array[Vector2f] = line.getVertices(body.getPosition(), body.getRotation());
    Renderer.setColor(BLACK)
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINES);
    		GL11.glVertex2f(verts(0).getX, verts(0).getY);
    		GL11.glVertex2f(verts(1).getX, verts(1).getY);
    	GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  })
}