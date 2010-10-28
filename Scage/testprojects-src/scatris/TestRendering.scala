package scatris

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.Vec

object TestRendering {
	properties = "scage-properties.txt"
	
	private val BOX = Renderer.createList("img/Crate.png", 40, 40, 0, 0, 256, 256)
	render {
	  (0 to 20-1).foreachpair(0 to 15-3) ((i, j) => {
	    val coord = Vec(0 + i*40 + 20, 0 + j*40 + 20)
	    GL11.glPushMatrix();
	    GL11.glTranslatef(coord.x, coord.y, 0.0f);
	    Renderer.setColor(WHITE)
	    GL11.glCallList(BOX)
	    GL11.glPopMatrix()
	  })
	}
	
	interface {
    	  Message.print("FPS: "+fps, 200, height-25)
  	}
	
	def main(args:Array[String]) = start
}
