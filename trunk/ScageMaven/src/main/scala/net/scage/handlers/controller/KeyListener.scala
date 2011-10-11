package net.scage.handlers.controller

import org.lwjgl.input.Keyboard
import collection.mutable.HashMap

object KeyListener {
  private var keys = new HashMap[Int, (Boolean, Long)]
  def wasPressed(key_code:Int):Boolean = {
    if(!keys.contains(key_code)) keys += (key_code -> (false, 0))
    keys(key_code)._1
  }
  def lastPressedTime(key_code:Int):Long = keys(key_code)._2
  def keyDown(key_code:Int) {keys(key_code) = (true, System.currentTimeMillis)}
  def keyUp(key_code:Int) {keys(key_code) = (false, keys(key_code)._2)}
}

import KeyListener._

class KeyListener(key: => Int, repeatTime: => Long, onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  val isRepeatable = repeatTime > 0

  def check() {
    if(Keyboard.isKeyDown(key)) {
      if(!wasPressed(key) ||
         (isRepeatable && System.currentTimeMillis() - lastPressedTime(key) > repeatTime)) {
        keyDown(key)
        onKeyDown        
      }
    }
    else if(wasPressed(key)) {
      keyUp(key)
      onKeyUp
    }
  }
}

class AnyKeyListener(onKeyDown: => Any, onKeyUp: => Any) extends UIListener {
  def check() {
    if(Keyboard.next) {
      val key = Keyboard.getEventKey
      if(Keyboard.getEventKeyState) {
        if(!wasPressed(key)) {
          keyDown(key)
          onKeyDown
        }
      }
      else if(wasPressed(key)) {
        keyUp(key)
        onKeyUp
      }
    }
  }
}
