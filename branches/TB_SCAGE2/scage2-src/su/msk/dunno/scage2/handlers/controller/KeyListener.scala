package su.msk.dunno.scage2.handlers.controller

import org.lwjgl.input.Keyboard

object KeyListener {
  private var last_key = -1
  private var isKeyPressed = false
  private var last_pressed_time:Long = 0

  def wasPressed(key:Int):Boolean = key == last_key && isKeyPressed
  def lastKeyDown(key:Int) = {
    last_key = key
    last_pressed_time = System.currentTimeMillis
    isKeyPressed = true
  }
  def lastKeyUp = isKeyPressed = false
}

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
      if(!KeyListener.wasPressed(key) || (isRepeatable &&
         System.currentTimeMillis() - KeyListener.last_pressed_time > repeatTime)) {
        KeyListener.lastKeyDown(key)
        onKeyDown()
      }
    }
    else if(KeyListener.wasPressed(key)) {
      onKeyUp()
      KeyListener.lastKeyUp
    }
  }
}