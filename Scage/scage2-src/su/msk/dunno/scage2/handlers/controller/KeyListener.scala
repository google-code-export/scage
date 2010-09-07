package su.msk.dunno.scage2.handlers.controller

import org.lwjgl.input.Keyboard

case class KeyListener(
        protected val key:Int,
        protected val repeatTime:Long,
        onKeyDown: () => Unit,
        onKeyUp: () => Unit) extends Listener {
  def this(key:Int,
           repeatTime:Long,
           onKeyDown: () => Unit) = this(key, repeatTime, onKeyDown, () => {})
  def this(key:Int, onKeyDown: () => Unit) = this(key,0,onKeyDown,() => {})
  def this(key:Int, onKeyDown: () => Unit, onKeyUp: () => Unit) = this(key,0,onKeyDown,onKeyUp)

  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed || (isRepeatable && System.currentTimeMillis()-lastPressed > repeatTime)) {
        onKeyDown()
        //Controller.last_key = key
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