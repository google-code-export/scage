package scagetest

import scagetest.objects.Tr0yka
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