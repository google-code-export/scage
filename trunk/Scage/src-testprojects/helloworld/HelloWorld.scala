package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.messages.Message._
import org.lwjgl.input.Keyboard

object HelloWorld extends Application {

  val myScreen = new ScageScreen("My Screen", is_main_screen = true, "blame-properties.txt") {
    addRender(new Renderable {
      override def interface = {
         print(xml("hello.world"), Renderer.width/2, Renderer.height/2)
         Renderer.drawCircle(Vec(Renderer.width/2, Renderer.height/2), 5)         
      }
    })
    
    private var my_key = Keyboard.KEY_1
    keyListener(my_key, onKeyDown = {
      println("current my_key = " + my_key)
      if(my_key == Keyboard.KEY_1) my_key = Keyboard.KEY_2
      else my_key = Keyboard.KEY_1
    })

    run
  }
}
