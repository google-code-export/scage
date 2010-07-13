package su.msk.dunno.scage.objects

import su.msk.dunno.scage.prototypes.Physical
import net.phys2d.raw.StaticBody
import net.phys2d.math.{Vector2f, ROVector2f}
import net.phys2d.raw.shapes.Polygon
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.handlers.{Physics, Renderer}
import org.lwjgl.opengl.GL11

class StaticPolygon(val vertices:Array[Vec]) extends Physical {
	val polygon_vertices = vertices.foldLeft(List[ROVector2f]())((acc, vertice) => {
		val new_vertice = vertice - vertices(0)
		new Vector2f(new_vertice.x, new_vertice.y) :: acc
	}).toArray	
	val polygon = new Polygon(polygon_vertices)
	val body = new StaticBody("StaticPolygon", polygon)
	body.setPosition(vertices(0).x, vertices(0).y)
	Physics.addBody(body)
	
	Renderer.addRender(() => {
    val verts:Array[Vector2f] = polygon.getVertices(body.getPosition(), body.getRotation());
    Renderer.setColor(Color.BLACK)
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINE_LOOP);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  })
}