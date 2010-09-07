package su.msk.dunno.scage2.handlers.controller

import su.msk.dunno.scage2.prototypes.{Screen, Handler}

class Controller(screen:Screen) extends Handler(screen:Screen) {
  var listeners = List[Listener]()

  var to_add = List[Listener]()
  def addKeyListener(key:Int, onKeyDown: () => Unit) = {
    to_add = new KeyListener(key, onKeyDown) :: to_add
  }
  def addKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Unit) = {
    to_add = new KeyListener(key, repeatTime, onKeyDown) :: to_add
  }
  def addKeyListener(key:Int, onKeyDown: () => Unit, onKeyUp: () => Unit) = {
	  to_add = new KeyListener(key, onKeyDown, onKeyUp) :: to_add
  }
  def addKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Unit, onKeyUp: () => Unit) = {
	  to_add = new KeyListener(key, repeatTime, onKeyDown, onKeyUp) :: to_add
  }
  def addListeners(ll:List[Listener]) = {
    to_add = ll ::: to_add
  }

  var to_remove = List[Listener]()
  def removeKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Unit) = {
    to_remove = new KeyListener(key, repeatTime, onKeyDown) :: to_remove
  }

  var last_key:Int = 0
  override def actionSequence():Unit = {
    if(to_add.length > 0) {
      listeners = to_add ::: listeners
      to_add = List[Listener]()
    }
    if(to_remove.length > 0) {
      listeners.filterNot(l => to_remove.contains(l))
      to_remove = List[Listener]()
    }
    listeners.foreach(l => l.check)
  }
}