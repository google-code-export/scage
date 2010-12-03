package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.messages.{TrueTypeFont, MyFont, Message}

object HelloWorld extends Application {
  //private val uFont = new MyFont

  val myScreen = new ScageScreen("My Screen", is_main = true, "blame-properties.txt") {
    addRender(new Renderable {
      override def interface = {
         Message.print("Привет, Мир!", Renderer.width/2, Renderer.height/2)
         //Message.print("Hello, World!", Renderer.width/2, Renderer.height/2-20)
         Renderer.drawCircle(Vec(Renderer.width/2, Renderer.height/2), 5)
      }
    })

    run
  }
}