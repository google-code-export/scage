package su.msk.dunno.screens.handlers.controller

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

class KeyListener(val key:Int, repeatTime: => Long, onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!KeyListener.wasPressed(key) || (isRepeatable && System.currentTimeMillis()-KeyListener.last_pressed_time > repeatTime)) {
        KeyListener.lastKeyDown(key)
        onKeyDown        
      }
    }
    else if(KeyListener.wasPressed(key)) {
      KeyListener.lastKeyUp
      onKeyUp
    }
  }
}
