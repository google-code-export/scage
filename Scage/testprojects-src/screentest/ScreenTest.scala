package screentest

import su.msk.dunno.screens.ScageScreen
import org.lwjgl.input.Keyboard
object ScreenTest extends ScageScreen("Main ScageScreen") {
  /*interface {
    Message.print("Press Esc to\nExit", width/2, height/2)
  }*/

  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)

  def main(args:Array[String]):Unit = run
}