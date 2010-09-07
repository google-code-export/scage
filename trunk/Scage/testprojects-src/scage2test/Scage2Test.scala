package scage2test

import su.msk.dunno.scage2.prototypes.Screen
import su.msk.dunno.scage2.support.messages.Message
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage2.handlers.controller.{KeyListener}

object Scage2Test {
  def main(args:Array[String]):Unit = {
    val main_screen = new Screen("MainScreen", true) {
      val second_screen = new Screen("SecondScreen") {
        renderer.addInterfaceElement(() => Message.print("Second Screen", 400, 350))
        controller.addKeyListener(Keyboard.KEY_1, 2000, () => stop)
      }
      
      renderer.addInterfaceElement(() => Message.print("First Screen", 400, 350))
      controller.addKeyListener(Keyboard.KEY_1, 2000, () => second_screen.start)
    }.start
  }
}