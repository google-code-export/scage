package net.scage.handlers.controller

import _root_.net.scage.support.Vec

class Controller {
  private var listeners = List[UIListener]()

  def addListener(listener:UIListener) {
    listeners = listener :: listeners
  }

  /*/*private var to_add = List[UIListener]()*/
  def key(key: => Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
	  /*to_add = new KeyListener(key, repeatTime, onKeyDown, onKeyUp) :: to_add*/
    listeners = new KeyListener(key, repeatTime, onKeyDown, onKeyUp) :: listeners
  }

  def leftMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    listeners = new MouseButtonsListener(0, repeatTime, onBtnDown, onBtnUp) :: listeners
  }
  def rightMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    listeners = new MouseButtonsListener(1, repeatTime, onBtnDown, onBtnUp) :: listeners
  }*/

  /*private var to_remove = List[UIListener]()
    def removeKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Any) = {
      to_remove = new KeyListener(key, repeatTime, onKeyDown) :: to_remove
  }*/

  def checkControls {
    /*if(to_add.length > 0) {
      listeners = to_add ::: listeners
      to_add = List[UIListener]()
    }*/
    /*if(to_remove.length > 0) {
      listeners.filterNot(l => to_remove.contains(l))
      to_remove = List[UIListener]()
    }*/
    listeners.foreach(l => l.check())
  }
}
