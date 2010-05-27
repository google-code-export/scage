package su.msk.dunno.scage.handlers.eventmanager

import org.lwjgl.input.Keyboard

case class KeyListener(
        protected val key:Int,
        protected val repeatTime:Long,
        onKeyDown: () => Unit,
        onKeyUp: () => Unit) extends TListener {
  def this(key:Int,
           repeatTime:Long,
           onKeyDown: () => Unit) = this(key, repeatTime, onKeyDown, () => {})
  def this(key:Int, onKeyDown: () => Unit) = this(key,0,onKeyDown,() => {})

  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed || (isRepeatable && System.currentTimeMillis()-lastPressed > repeatTime)) {
        onKeyDown()
        EventManager.last_key = key
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