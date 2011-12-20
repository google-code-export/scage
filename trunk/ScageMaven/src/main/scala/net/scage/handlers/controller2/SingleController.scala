package net.scage.handlers.controller2

import net.scage.support.Vec
import collection.mutable.HashMap
import org.lwjgl.input.{Keyboard, Mouse}
import net.scage.Scage

trait SingleController extends ScageController with Scage {
  private var keyboard_keys = HashMap[Int, KeyData]()  // was_pressed, last_pressed_time, repeat_time, onKeyDown, onKeyUp
  private var anykey: () => Any = () => {}
  private var mouse_buttons = HashMap[Int, MouseButtonData]()
  private var on_mouse_motion: Vec => Any = Vec => {}
  private var on_mouse_drag_motion = HashMap[Int, Vec => Any]()
  private var on_mouse_wheel_up: Vec => Any = Vec => {}
  private var on_mouse_wheel_down: Vec => Any = Vec => {}

  def key(key_code:Int, repeat_time: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
    keyboard_keys(key_code) = KeyData(false, 0, () => repeat_time, () => onKeyDown, () => onKeyUp)
  }
  def anykey(onKeyDown: => Any) {
    anykey = () => onKeyDown
  }

  def mouseCoord = Vec(Mouse.getX, Mouse.getY)
  def isMouseMoved = Mouse.getDX != 0 || Mouse.getDY != 0
  private def mouseButton(button_code:Int, repeat_time: => Long = 0, onButtonDown: Vec => Any, onButtonUp: Vec => Any = Vec => {}) {
    mouse_buttons(button_code) = MouseButtonData(false, 0, () => repeat_time, onButtonDown, onButtonUp)
  }
  def leftMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    mouseButton(0, repeat_time, onBtnDown, onBtnUp)
  }
  def rightMouse(repeat_time: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    mouseButton(1, repeat_time, onBtnDown, onBtnUp)
  }
  def mouseMotion(onMotion: Vec => Any) {
    on_mouse_motion = onMotion
  }
  private def mouseDrag(button_code:Int, onDrag: Vec => Any) {
    on_mouse_drag_motion(button_code) = onDrag
  }
  def leftMouseDrag(onDrag: Vec => Any) {
    mouseDrag(0, onDrag)
  }
  def rightMouseDrag(onDrag: Vec => Any) {
    mouseDrag(1, onDrag)
  }
  def mouseWheelUp(onWheelUp: Vec => Any) {
    on_mouse_wheel_up = onWheelUp
  }
  def mouseWheelDown(onWheelDown: Vec => Any) {
    on_mouse_wheel_down = onWheelDown
  }

  def checkControls() {
    for {
      (key, key_data) <- keyboard_keys
      KeyData(was_pressed, last_pressed_time, repeat_time_func, onKeyDown, onKeyUp) = key_data
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

    if(Keyboard.next && Keyboard.getEventKeyState) anykey()

    val mouse_coord = mouseCoord
    val is_mouse_moved = isMouseMoved
    if(is_mouse_moved) on_mouse_motion(mouse_coord)

    for {
      (button, button_data) <- mouse_buttons
      MouseButtonData(was_pressed, last_pressed_time, repeat_time_func, onButtonDown, onButtonUp) = button_data
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
        (button, onDragMotion) <- on_mouse_drag_motion
        if Mouse.isButtonDown(button)
      } onDragMotion(mouse_coord)
    }

    Mouse.getDWheel match {
      case x if(x > 0) => on_mouse_wheel_up(mouse_coord)
      case x if(x < 0) => on_mouse_wheel_down(mouse_coord)
      case _ =>
    }
  }

  action {
    checkControls()
  }
}