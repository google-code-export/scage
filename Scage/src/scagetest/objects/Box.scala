package scagetest.objects

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.objects.DynaBox
import su.msk.dunno.scage.support.{Vec, Color}
import su.msk.dunno.scage.handlers.tracer.{Tracer, State, Trace}
import su.msk.dunno.scage.handlers.Renderer

class Box(leftup_coord:Vec) extends DynaBox(leftup_coord, 50, 50) {
  val trace = new Trace {
    		def getCoord = coord()
    		def getState = new State("name", "Box")
    		def changeState(s:State) = {
          if(s.contains("pull"))addForce((s.getVec("pull") - coord).n*1000)
        }
  }
  Tracer.addTrace(trace)
  
  private val BOX = Renderer.createList("img/Crate.png", 50, 50, 0, 0, 256, 256)
  override protected def render() = {
	  GL11.glPushMatrix();
	  GL11.glTranslatef(coord.x, coord.y, 0.0f);
	  Renderer.setColor(Color.WHITE)
	  GL11.glCallList(BOX)
	  GL11.glPopMatrix()
  }
}