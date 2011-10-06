package su.msk.dunno.scage.single.handlers.controller

import org.lwjgl.input.Keyboard

class KeyListener(val key:Int, val repeatTime:Long, onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed || (isRepeatable && System.currentTimeMillis()-lastPressedTime > repeatTime)) {
        onKeyDown
        Controller.last_key = key
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