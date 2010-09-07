package scage2test

import su.msk.dunno.scage2.prototypes.Screen
import su.msk.dunno.scage2.support.messages.Message

object Scage2Test {
  def main(args:Array[String]):Unit = {
    new Screen("Scage2Test") {
      renderer.addInterfaceElement(() => Message.print("Hello World!", 400, 300))
    }
  }
}