package screentest

import su.msk.dunno.screens.Screen
import su.msk.dunno.scage.support.messages.Message
import org.lwjgl.input.Keyboard
import su.msk.dunno.screens.support.ScageLibrary._

object ScreenTest extends Screen("Main Screen") {
  interface {
    Message.print("Press Esc to Exit", width/2, height/2)
  }

  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)

  def main(args:Array[String]):Unit = run
}