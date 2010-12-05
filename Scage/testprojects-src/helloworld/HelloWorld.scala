package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.messages.Message._

object HelloWorld extends Application {

  val myScreen = new ScageScreen("My Screen", is_main_screen = true, "blame-properties.txt") {
    addRender(new Renderable {
      override def interface = {
         print(xml("hello.world"), Renderer.width/2, Renderer.height/2)
         Renderer.drawCircle(Vec(Renderer.width/2, Renderer.height/2), 5)
      }
    })

    run
  }
}