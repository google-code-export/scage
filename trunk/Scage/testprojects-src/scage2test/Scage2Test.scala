package scage2test

import su.msk.dunno.scage2.prototypes.Screen
import su.msk.dunno.scage2.support.messages.Message
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage2.support.Colors

object Scage2Test extends Colors {
  def main(args:Array[String]):Unit = {
    val second_screen = new Screen("SecondScreen") {
      renderer.addInterfaceElement(() => {
        renderer.setBackground(RED)
        Message.print("Second Screen", 400, 350)
      })
      controller.addKeyListener(Keyboard.KEY_1, 2000, () => stop)
    }

    val main_screen = new Screen("MainScreeen", true) {
      renderer.addInterfaceElement(() => {
        renderer.setBackground(GREEN)
        Message.print("First Screeen", 400, 350)
      })
      controller.addKeyListener(Keyboard.KEY_1, 2000, () => second_screen.start)
    }

    main_screen.start
  }
}