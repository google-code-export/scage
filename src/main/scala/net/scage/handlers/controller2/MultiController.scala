package net.scage.handlers.controller2

import net.scage.support.Vec
import org.lwjgl.input.{Keyboard, Mouse}
import collection.mutable.{HashMap, ArrayBuffer}

case class MultiKeyEvent(var was_pressed:Boolean, var last_pressed_time:Long, repeat_time: () => Long, onKeyDown: () => Any, onKeyUp: () => Any)
case class MultiMouseButtonEvent(var was_pressed:Boolean, var last_pressed_time:Long, repeat_time: () => Long, onButtonDown: Vec => Any, onButtonUp: Vec => Any)

trait MultiController extends ScageController {
  private var keyboard_keys = HashMap[Int, ArrayBuffer[MultiKeyEvent]]()  // was_pressed, last_pressed_time, repeat_time, onKeyDown, onKeyUp
  private var anykeys = ArrayBuffer[() => Any]()
  private var mouse_buttons = HashMap[Int, ArrayBuffer[MultiMouseButtonEvent]]()
  private var mouse_motions = ArrayBuffer[Vec => Any]()
  private var mouse_drag_motions = HashMap[Int, ArrayBuffer[Vec => Any]]()
  private var mouse_wheel_ups = ArrayBuffer[Vec => Any]()
  private var mouse_wheel_downs = ArrayBuffer[Vec => Any]()

  def key(key_code:Int, repeat_time: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
    if(keyboard_keys.contains(key_code)) keyboard_keys(key_code) += MultiKeyEvent(false, 0, () => repeat_time, () => onKeyDown, () => onKeyUp)
    else keyboard_keys(key_code) = ArrayBuffer(MultiKeyEvent(false, 0, () => repeat_time, () => onKeyDown, () => onKeyUp))
  }
  def anykey(onKeyDown: => Any) {
    anykeys += (() => onKeyDown)
  }

  def mouseCoord = Vec(Mouse.getX, Mouse.getY)
  def isMouseMoved = Mouse.getDX != 0 || Mouse.getDY != 0
  private def mouseButton(button_code:Int, repeat_time: => Long = 0, onButtonDown: Vec => Any, onButtonUp: Vec => Any = Vec => {}) {
    if(mouse_buttons.contains(button_code)) mouse_buttons(button_code) += MultiMouseButtonEvent(false, 0, () => repeat_time, onButtonDown, onButtonUp)
    else mouse_buttons(button_code) = ArrayBuffer(MultiMouseButtonEvent(false, 0, () => repeat_time, onButtonDown, onButtonUp))
  }
  def leftMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    mouseButton(0, repeat_time, onBtnDown, onBtnUp)
  }
  def rightMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    mouseButton(1, repeat_time, onBtnDown, onBtnUp)
  }
  def mouseMotion(onMotion: Vec => Any) {
    mouse_motions += onMotion
  }
  private def mouseDrag(button_code:Int, onDrag: Vec => Any) {
    if(mouse_drag_motions.contains(button_code)) mouse_drag_motions(button_code) += onDrag
    else mouse_drag_motions(button_code) = ArrayBuffer(onDrag)
  }
  def leftMouseDrag(onDrag: Vec => Any) {
    mouseDrag(0, onDrag)
  }
  def rightMouseDrag(onDrag: Vec => Any) {
    mouseDrag(1, onDrag)
  }
  def mouseWheelUp(onWheelUp: Vec => Any) {
    mouse_wheel_ups += onWheelUp
  }
  def mouseWheelDown(onWheelDown: Vec => Any) {
    mouse_wheel_downs += onWheelDown
  }

  def checkControls() {
    for {
      (key, events_for_key) <- keyboard_keys
      key_data <- events_for_key
      MultiKeyEvent(was_pressed, last_pressed_time, repeat_time_func, onKeyDown, onKeyUp) = key_data
      repeat_time = repeat_time_func()
      is_repeatable = repeat_time > 0
    } {
      if(Keyboard.isKeyDown(key)) {
        if(!was_pressed || (is_repeatable && System.currentTimeMillis() - last_pressed_time > repeat_time)) {
          key_data.was_pressed = true
          key_data.last_pressed_time = System.currentTimeMillis()
          onKeyDown()
        }
      } else if(was_pressed) {
        key_data.was_pressed = false
        onKeyUp()
      }
    }

    if(Keyboard.next && Keyboard.getEventKeyState) for(anykeydown <- anykeys) anykeydown()

    val mouse_coord = mouseCoord
    val is_mouse_moved = isMouseMoved
    if(is_mouse_moved) {
      mouse_motions.foreach(onMotion => onMotion(mouse_coord))
    }

    for {
      (button, events_for_button) <- mouse_buttons
      button_data <- events_for_button
      MultiMouseButtonEvent(was_pressed, last_pressed_time, repeat_time_func, onButtonDown, onButtonUp) = button_data
      repeat_time = repeat_time_func()
      is_repeatable = repeat_time > 0
    } {
      if(Mouse.isButtonDown(button)) {
        if(!was_pressed || (is_repeatable && System.currentTimeMillis() - last_pressed_time > repeat_time)) {
          button_data.was_pressed = true
          button_data.last_pressed_time = System.currentTimeMillis()
          onButtonDown(mouse_coord)
        }
      } else if(was_pressed) {
        button_data.was_pressed = false
        onButtonUp(mouse_coord)
      }
    }

    if(is_mouse_moved) {
      for {
        (button, drag_motions_for_button) <- mouse_drag_motions
        if Mouse.isButtonDown(button)
        onDragMotion <- drag_motions_for_button
      } onDragMotion(mouse_coord)
    }

    Mouse.getDWheel match {
      case x if(x > 0) => mouse_wheel_ups.foreach(onWheelUp => onWheelUp(mouse_coord))
      case x if(x < 0) => mouse_wheel_downs.foreach(onWheelDown => onWheelDown(mouse_coord))
      case _ =>
    }
  }
}