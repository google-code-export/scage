package planeflight

import objects.{EnemyPlane, OurPlane}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.main.Scage
import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.controller.Controller
import org.newdawn.slick.opengl.Texture
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.handlers.{AI, Idler, Renderer}
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

object PlaneFlight extends ScageLibrary {
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
  val EXPLOSION_ANIMATION:Array[Int] = {
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val next_key = Renderer.createList(texture, 36, 35, 72*(arr.length), 0, 72, 69)
      val new_arr = arr ::: List(next_key)
      if(new_arr.length == 3)new_arr
      else nextFrame(next_key :: arr, texture)
    }
    nextFrame(List[Int](), Renderer.getTexture("img/explosion_animation.png")).toArray
  }
  val PLAYER_PLANE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
  val ENEMY_PLANE = Renderer.createList("img/plane2.png", 60, 60, 0, 0, 122, 121)

  def main(args: Array[String]): Unit = {
    // background
    val LAND = Renderer.createList("img/land.png", 800, 600, 0, 0, 800, 600)
    Renderer.addRender(() => {
      GL11.glPushMatrix();
      Renderer.setColor(WHITE)

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
    var enemy = new EnemyPlane(100, 200)
    var player = new OurPlane(400, 300)
    Renderer.setCentral(() => if(Renderer.scale == 1)Vec(Renderer.width/2, Renderer.height/2) else player.coord)

    // interface
    Renderer.addInterfaceElement(() => Message.print("HP: "+player.health, 20, Renderer.height-60, YELLOW))
    Renderer.addInterfaceElement(() => Message.print(StandardTracer.point(player.coord), 20, Renderer.height-80, YELLOW))

    // highscore
    var player_victories = 0
    var enemy_victories = 0
    AI.registerAI(() => {
      if(enemy.health <= 0) {
          player_victories += 1
          enemy = new EnemyPlane(100, 200)
      }
      if(player.health <= 0) {
        enemy_victories += 1
        player = new OurPlane(400, 300)
      }
    })
    Renderer.addInterfaceElement(() => Message.print(player_victories+" : "+enemy_victories, Renderer.width/2-20, Renderer.height-60, YELLOW))

    // game pause
    Controller.addKeyListener(Keyboard.KEY_P,() => Scage.switchPause)
    Renderer.addInterfaceElement(() => if(Scage.on_pause)Message.print("PAUSE", Renderer.width/2-20, Renderer.height/2+60, WHITE))

    // fps
    Renderer.addInterfaceElement(() => Message.print("fps: "+fps, 20, Renderer.height-20, YELLOW))
    
    Idler
    Scage.start
  }
}