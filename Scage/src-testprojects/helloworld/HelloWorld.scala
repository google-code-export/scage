package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.ScageColors

object HelloWorld extends Application {

  val myScreen = new ScageScreen("My Screen", is_main_screen = true, "blame-properties.txt") {
    Renderer.backgroundColor = ScageColors.GREEN
    println(Renderer.backgroundColor)

    Renderer.color = ScageColors.CYAN
    println(Renderer.color)

    /*addRender(new ScageRender {
      override def interface = {
         print(xml("hello.world"), Renderer.width/2, Renderer.height/2)
         Renderer.drawCircle(Vec(Renderer.width/2, Renderer.height/2), 5)         
      }
    })

    run*/
  }
}
