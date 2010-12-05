package su.msk.dunno.screens.handlers.controller

import org.lwjgl.input.Keyboard
import collection.mutable.HashMap

object KeyListener {
  private var keys = new HashMap[Int, (Boolean, Long)]
  def addKey(key_code:Int) = if(!keys.contains(key_code)) keys += (key_code -> (false, 0))
  def wasPressed(key_code:Int):Boolean = keys(key_code)._1
  def lastPressedTime(key_code:Int):Long = keys(key_code)._2
  def keyDown(key_code:Int) = keys(key_code) = (true, System.currentTimeMillis)
  def keyUp(key_code:Int) = keys(key_code) = (false, keys(key_code)._2)
}

class KeyListener(val key:Int, repeatTime: => Long, onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  KeyListener.addKey(key)
  val isRepeatable = repeatTime > 0

  def check() = {
    if(Keyboard.isKeyDown(key)) {
      if(!KeyListener.wasPressed(key) || (isRepeatable && System.currentTimeMillis()-KeyListener.lastPressedTime(key) > repeatTime)) {
        KeyListener.keyDown(key)
        onKeyDown        
      }
    }
    else if(KeyListener.wasPressed(key)) {
      KeyListener.keyUp(key)
      onKeyUp
    }
  }
}
