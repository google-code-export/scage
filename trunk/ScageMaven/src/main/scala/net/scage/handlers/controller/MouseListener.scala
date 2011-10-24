package net.scage.handlers.controller

import org.lwjgl.input.Mouse
import _root_.net.scage.support.Vec

object MouseListener {
  def mouseCoord = Vec(Mouse.getX, Mouse.getY)
}

import MouseListener._

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
        onBtnDown(mouseCoord)
      }
    }
    else if(was_pressed) {
      btnUp()
      onBtnUp(mouseCoord)
    }
  }
}

class MouseMotionListener(onMotion: Vec => Any) extends UIListener {
  def check() {
    if(Mouse.getDX != 0 || Mouse.getDY != 0) {
      onMotion(mouseCoord)
    }
  }
}

class MouseDragListener(button:Int, onDrag: Vec => Any) extends UIListener {
  def check() {
    if(Mouse.isButtonDown(button) && (Mouse.getDX != 0 || Mouse.getDY != 0)) {
      onDrag(mouseCoord)
    }
  }
}

object MouseWheelFactory {
  def wheelUpListener(onWheelUp: Vec => Any) = {
    if(Mouse.hasWheel) {
      new UIListener {
        def check() {
          if(Mouse.getDWheel > 0) {
            onWheelUp(mouseCoord)
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
            onWheelDown(mouseCoord)
          }
        }
      }
    }
    else new UIListener {def check() {}}
  }

  def wheelListener(onWheelUp: Vec => Any, onWheelDown: Vec => Any) = {
    if(Mouse.hasWheel) {
      new UIListener {
        def check() {
          Mouse.getDWheel match {
            case x if(x > 0) => onWheelUp(mouseCoord)
            case x if(x < 0) => onWheelDown(mouseCoord)
            case _ =>
          }
        }
      }
    }
    else new UIListener {def check() {}}
  }
}
