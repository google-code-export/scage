package ${package}

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.ScageScreen

/**
 * Hello world!
 *
 */
object ScageApp extends App {
  new ScageScreen("Hello World", is_main_screen = true, properties = "scageapp.properties") {
    backgroundColor = WHITE

    interface {
      print(xml("hello.world"), width/2, height/2, BLACK)
    }
  }.run
}
