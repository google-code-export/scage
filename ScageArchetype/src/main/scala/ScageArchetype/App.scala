package ScageArchetype;

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.prototypes.ScageRender
import su.msk.dunno.scage.screens.ScageScreen

/**
 * Hello world!
 *
 */
object App extends Application {
  new ScageScreen("Hello World", is_main_screen = true, properties = "app-properties.txt") {
    backgroundColor = WHITE

    interface {
      print(xml("hello.world"), width/2, height/2, BLACK)
    }
  }.run
}
