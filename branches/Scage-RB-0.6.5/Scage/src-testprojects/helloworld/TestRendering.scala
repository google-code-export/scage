package helloworld

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.ScageLibrary._

object TestRendering {
	properties = "scatris-properties.txt"
	
	private val BOX = Renderer.createList("img/Crate.png", h_x, h_y, 0, 0, 256, 256)
//  private var start_time = System.currentTimeMillis
	render {
    var start_time = System.currentTimeMillis
    
//    var i = 0
//    var j = 0
//    while(i <= N_x-1) {
//      while(j <= N_y-3) {
//        val coord = pointCenter(i, j)
//    	  GL11.glPushMatrix();
//        GL11.glTranslatef(coord.x, coord.y, 0.0f);
//        Renderer.color(WHITE)
//        GL11.glCallList(BOX)
//        GL11.glPopMatrix()

//        j += 1
//      }
//      j = 0
//      i += 1
//    }
	  (0 to N_x-1).foreachpair(0 to N_y-3) ((i, j) => {
	    val coord = pointCenter(i, j)
	    GL11.glPushMatrix();
	    GL11.glTranslatef(coord.x, coord.y, 0.0f);
	    Renderer.setColor(WHITE)
	    GL11.glCallList(BOX)
	    GL11.glPopMatrix()
	  })
      	  ScageMessage.print("Time: "+(System.currentTimeMillis - start_time), 200, height-40)
	}
	
	interface {
    ScageMessage.print("FPS: "+fps, 200, height-25)
  }
	
	def main(args:Array[String]) = run
}
