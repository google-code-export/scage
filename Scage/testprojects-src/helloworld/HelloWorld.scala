package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.messages.{MyFont, Message}

object HelloWorld extends Application {
  //private val uFont = new MyFont

  val myScreen = new ScageScreen("My Screen", is_main = true, "blame-properties.txt") {
    addRender(new Renderable {
      override def interface = {
        //uFont.uFont.drawString(Renderer.width/2, Renderer.height/2, "Hello World!", org.newdawn.slick.Color.white)
         Message.print("Привет Мир!", Renderer.width/2, Renderer.height/2)
        Message.print("Овлоло, овлоло, вы все быдло и хуйло!", Renderer.width/2, Renderer.height/2 - 20)
      }
    })

    run
  }
}