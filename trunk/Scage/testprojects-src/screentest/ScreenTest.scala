package screentest

import su.msk.dunno.screens.Screen
import org.lwjgl.input.Keyboard
object ScreenTest extends Screen("Main Screen") {
  /*interface {
    Message.print("Press Esc to\nExit", width/2, height/2)
  }*/

  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)

  def main(args:Array[String]):Unit = run
}