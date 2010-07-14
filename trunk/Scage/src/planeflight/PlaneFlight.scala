package planeflight

import objects.OurPlane
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{Vec, Color}
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.controller.Controller
import org.newdawn.slick.opengl.Texture

object PlaneFlight {
  // common images
  val ROCKET_ANIMATION:Array[Int] = {
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val next_key = Renderer.createList(texture, 10, 29, 14*(arr.length), 0, 14, 44)
      val new_arr = next_key :: arr
      if(new_arr.length == 3)new_arr
      else nextFrame(next_key :: arr, texture)
    }
    nextFrame(List[Int](), Renderer.getTexture("img/rocket_animation.png")).toArray
  }

  def main(args: Array[String]): Unit = {
    // background
    val LAND = Renderer.createList("img/land.png", 800, 600, 0, 0, 800, 600)
    Renderer.addRender(() => {
      GL11.glPushMatrix();
      Renderer.setColor(Color.WHITE)

      GL11.glTranslatef(Renderer.width/2, Renderer.height/2, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)

      GL11.glPopMatrix()
    })

    // objects
    new OurPlane(400, 300)

    // game pause
    Controller.addKeyListener(Keyboard.KEY_P,() => Scage.switchPause)
    Renderer.addInterfaceElement(() => if(Scage.onPause)Message.print("PAUSE", Renderer.width/2-20, Renderer.height/2+60, Color.WHITE))

    // fps
    Renderer.addInterfaceElement(() => Message.print("fps: "+Renderer.fps, 20, Renderer.height-20, Color.YELLOW))
    
    Idler
    Scage.start
  }
}