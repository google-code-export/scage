package su.msk.dunno.screens.handlers.controller

import org.lwjgl.input.Keyboard

class KeyListener(val key:Int, repeatTime: => Long, onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed || (isRepeatable && System.currentTimeMillis()-lastPressedTime > repeatTime)) {
        onKeyDown
        wasPressed = true
        lastPressedTime = System.currentTimeMillis
      }
    }
    else if(wasPressed) {
      onKeyUp
      wasPressed = false
    }
  }
}
