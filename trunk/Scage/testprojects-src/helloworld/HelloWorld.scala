package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.ActionHandler

object HelloWorld extends Application {
  val myScreen = new ScageScreen("My Screen", is_main = true, "blame-properties.txt") {
    addHandler(new ActionHandler {
      override def action = println("here we are!")
    }, period = 5000)

    run
  }
}