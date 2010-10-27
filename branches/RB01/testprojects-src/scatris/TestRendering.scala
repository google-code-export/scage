package scatris

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.ScageLibrary._

object TestRendering {
	properties = "scatris-properties.txt"
	
	private val BOX = Renderer.createList("img/Crate.png", h_x, h_y, 0, 0, 256, 256)
	render {
	  (0 to N_x-1).foreachpair(0 to N_y-3)((i, j) => {
	    val coord = pointCenter(i, j)
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
