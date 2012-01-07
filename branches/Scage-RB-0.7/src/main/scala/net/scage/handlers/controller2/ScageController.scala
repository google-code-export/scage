package net.scage.handlers.controller2

import net.scage.support.Vec
import net.scage.Scage

case class KeyData(var was_pressed:Boolean, var last_pressed_time:Long, repeat_time: () => Long, onKeyDown: () => Any, onKeyUp: () => Any)
case class MouseButtonData(var was_pressed:Boolean, var last_pressed_time:Long, repeat_time: () => Long, onButtonDown: Vec => Any, onButtonUp: Vec => Any)

trait ScageController extends Scage {
  def key(key_code:Int, repeat_time: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {})
  def anykey(onKeyDown: => Any)

  def mouseCoord:Vec
  def isMouseMoved:Boolean

  def leftMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {})
  def rightMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {})

  def mouseMotion(onMotion: Vec => Any)

  def leftMouseDrag(onDrag: Vec => Any)
  def rightMouseDrag(onDrag: Vec => Any)

  def mouseWheelUp(onWheelUp: Vec => Any)
  def mouseWheelDown(onWheelDown: Vec => Any)

  def checkControls()
}