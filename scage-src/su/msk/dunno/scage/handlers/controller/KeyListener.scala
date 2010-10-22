package su.msk.dunno.scage.handlers.controller

import org.lwjgl.input.Keyboard

case class KeyListener(
        protected val key:Int,
        protected val repeatTime:Long,
        onKeyDown: () => Any,
        onKeyUp: () => Any) extends TListener {
  def this(key:Int,
           repeatTime:Long,
           onKeyDown: () => Any) = this(key, repeatTime, onKeyDown, () => {})
  def this(key:Int, onKeyDown: () => Any) = this(key,0,onKeyDown,() => {})
  def this(key:Int, onKeyDown: () => Any, onKeyUp: () => Any) = this(key,0,onKeyDown,onKeyUp)

  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed || (isRepeatable && System.currentTimeMillis()-lastPressed > repeatTime)) {
        onKeyDown()
        Controller.last_key = key
        wasPressed = true
        lastPressed = System.currentTimeMillis
      }
    }
    else if(wasPressed) {
      onKeyUp()
      wasPressed = false
    }
  }
}