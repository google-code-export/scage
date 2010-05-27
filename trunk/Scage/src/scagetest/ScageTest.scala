package scagetest

import su.msk.dunno.scage.main.Engine
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.eventmanager.EventManager
import su.msk.dunno.scage.handlers.{AI, Renderer}
import su.msk.dunno.scage.objects.{StaticBox, DynaBall, StaticLine}
import su.msk.dunno.scage.support.{Color, Vec}
import org.lwjgl.opengl.GL11
import org.lwjgl.input.Keyboard
import org.newdawn.slick.opengl.Texture

object ScageTest {
  def main(args:Array[String]):Unit = {
    val tr0yka = new Tr0yka(Vec(300,400)){
      Renderer.addInterfaceElement(() => Message.print("touching: "+(if(isTouching)"true" else "false"), 20, 420))
      Renderer.addInterfaceElement(() => Message.print("speed: "+velocity.norma2, 20, 400))
      addForce(Vec(-100,0))
    }
    Renderer.setCentral(tr0yka)

    Engine.setDefaultHandlers
    Engine.addObjects(
      tr0yka ::
      new StaticLine(Vec(0,240), Vec(250,140)) ::
      new StaticLine(Vec(390,140), Vec(640,240)) ::
      new StaticLine(Vec(0,65), Vec(640,65)) ::
      new StaticBox(Vec(320,250), 140, 10){
        var dir:Int = 1
        AI.registerAI(() => {
          val vec:Vec = new Vec(body.getPosition)+Vec(0,1)*dir
          if(vec.y <= 200)dir = 1
          else if(vec.y >= 400)dir = -1
          body.setPosition(vec.x, vec.y)
        })
      } ::
      new DynaBall(Vec(360,400), 15){
        addForce(Vec(100,0))
      }
    )
    Renderer.addInterfaceElement(() => Message.print("fps: "+Engine.fps, 20, 460))
    Renderer.addInterfaceElement(() => Message.print("last key: "+EventManager.last_key, 20, 440))
    Engine.start
  }
}

class Tr0yka(init_coord:Vec) extends DynaBall(init_coord:Vec, 30) {
  private val TR0YKA = Renderer.nextDisplayListKey
  Renderer.createList(TR0YKA, "img/stay2.png", 24, 30, 0, 0, 160, 200)

  private val ANIMATION:Array[Int] = {
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val next_key = Renderer.nextDisplayListKey
      Renderer.createList(next_key, texture, 24, 30, 160*(arr.length), 0, 160, 200)
      val new_arr = next_key :: arr
      if(new_arr.length == 6)new_arr
      else nextFrame(next_key :: arr, texture)
    }
    nextFrame(List[Int](), Renderer.getTexture("img/loli.png")).toArray
  }

  private var last_key:Int = 0
  EventManager.addKeyListener(Keyboard.KEY_S,() => {last_key = Keyboard.KEY_S; if(isTouching)body.setForce(0, 0)})
  EventManager.addKeyListener(Keyboard.KEY_SPACE,() => {last_key = Keyboard.KEY_SPACE; if(isTouching)addForce(Vec(0,3500))})
  EventManager.addKeyListener(Keyboard.KEY_UP,() => {last_key = Keyboard.KEY_UP; if(isTouching)addForce(Vec(0,3500))})
  EventManager.addKeyListener(Keyboard.KEY_LEFT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(-2000,0))
    else if(last_key != Keyboard.KEY_LEFT) addForce(Vec(-1500,0))
    last_key = Keyboard.KEY_LEFT
  })
  EventManager.addKeyListener(Keyboard.KEY_RIGHT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(2000,0))
    else if(last_key != Keyboard.KEY_RIGHT) addForce(Vec(1500,0))
    last_key = Keyboard.KEY_RIGHT
  })

  private var next_frame:Float = 0
  override def render() = {
   /* Renderer.setColor(Color.BLACK)
    Renderer.drawCircle(coord, 15)
    Renderer.drawLine(coord, coord+velocity)*/

    GL11.glPushMatrix();
		GL11.glTranslatef(coord.x, coord.y, 0.0f);

    Renderer.setColor(Color.WHITE)
    last_key match {
      case Keyboard.KEY_LEFT => GL11.glScalef(1,1,1)
      case Keyboard.KEY_RIGHT => GL11.glScalef(-1,1,1)
      case Keyboard.KEY_SPACE => {
        val x_dir = Math.signum(velocity * Vec(-1,0))
        GL11.glScalef(if(x_dir == 0)1 else x_dir,1,1)
      }
      case _ => GL11.glScalef(1,1,1)
    }

    val vel = velocity.norma2
    if(isTouching && vel > 10){
      GL11.glCallList(ANIMATION(next_frame.toInt));
      next_frame += vel/2000
      if(next_frame >= 6)next_frame = 0
    }
    else GL11.glCallList(TR0YKA)

    GL11.glPopMatrix()
  }
}