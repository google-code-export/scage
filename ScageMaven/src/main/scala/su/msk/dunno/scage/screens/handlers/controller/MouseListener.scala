package su.msk.dunno.scage.screens.handlers.controller

import org.lwjgl.input.Mouse
import su.msk.dunno.scage.single.support.Vec

object MouseListener {
  def mouseCoord = Vec(Mouse.getX, Mouse.getY)
}

class MouseButtonsListener(button:Int, repeatTime: => Long, onBtnDown: Vec => Any, onBtnUp: Vec => Any) extends UIListener {
  val isRepeatable = repeatTime > 0
  private var was_pressed = false
  private var last_pressed_time:Long = 0

  private def btnDown() {
    was_pressed = true
    last_pressed_time = System.currentTimeMillis
  }

  private def btnUp() {
    was_pressed = false
  }

  def check() {
    if(Mouse.isButtonDown(button)) {
      if(!was_pressed ||
         (isRepeatable && System.currentTimeMillis() - last_pressed_time > repeatTime)) {
        btnDown()
        onBtnDown(Vec(Mouse.getX, Mouse.getY))
      }
    }
    else if(was_pressed) {
      btnUp()
      onBtnUp(Vec(Mouse.getX, Mouse.getY))
    }
  }
}

class MouseMotionListener(onMotion: Vec => Any) extends UIListener {
  def check() {
    if(Mouse.getDX != 0 || Mouse.getDY != 0) {
      val mouse_coord = Vec(Mouse.getX, Mouse.getY)
      onMotion(mouse_coord)
    }
  }
}

class MouseDragListener(button:Int, onDrag: Vec => Any) extends UIListener {
  def check() {
    if(Mouse.isButtonDown(button) && (Mouse.getDX != 0 || Mouse.getDY != 0)) {
      val mouse_coord = Vec(Mouse.getX, Mouse.getY)
      onDrag(mouse_coord)
    }
  }
}

class MouseWheelFactory {
  def wheelUpListener(onWheelUp: Vec => Any) = {
    if(Mouse.hasWheel) {
      new UIListener {
        def check() {
          if(Mouse.getDWheel > 0) {
            val mouse_coord = Vec(Mouse.getX, Mouse.getY)
            onWheelUp(mouse_coord)
          }
        }
      }
    }
    else new UIListener {def check() {}}
  }

  def wheelDownListener(onWheelDown: Vec => Any) = {
    if(Mouse.hasWheel) {
      new UIListener {
        def check() {
          if(Mouse.getDWheel < 0) {
            val mouse_coord = Vec(Mouse.getX, Mouse.getY)
            onWheelDown(mouse_coord)
          }
        }
      }
    }
    else new UIListener {def check() {}}
  }
}
