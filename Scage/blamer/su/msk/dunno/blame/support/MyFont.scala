package su.msk.dunno.blame.support

import su.msk.dunno.screens.handlers.Renderer
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.ScageLibrary._

object MyFont {
  val A = Renderer.createList("img/font.png", 32, 32, 32, 4*32, 32, 32)
  val q = Renderer.createList("img/font.png", 32, 32, 32, 7*32, 32, 32)
  val G = Renderer.createList("img/font.png", 32, 32, 7*32, 4*32, 32, 32)

  def drawList(list_code:Int, coord:Vec) = {
    GL11.glPushMatrix();
	  GL11.glTranslatef(coord.x, coord.y, 0.0f);
	  Renderer.setColor(WHITE)
	  GL11.glCallList(list_code)
	  GL11.glPopMatrix()
  }
}