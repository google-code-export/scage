package su.msk.dunno.scage.handlers.controller

import su.msk.dunno.scage.prototypes.Handler
object Controller extends Handler {
  var listeners = List[TListener]()

  var to_add = List[TListener]()
  def addKeyListener(key:Int, onKeyDown: () => Any) = {
    to_add = new KeyListener(key, onKeyDown) :: to_add
  }
  def addKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Any) = {
    to_add = new KeyListener(key, repeatTime, onKeyDown) :: to_add
  }
  def addKeyListener(key:Int, onKeyDown: () => Any, onKeyUp: () => Any) = {
	  to_add = new KeyListener(key, onKeyDown, onKeyUp) :: to_add
  }
  def addKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Any, onKeyUp: () => Any) = {
	  to_add = new KeyListener(key, repeatTime, onKeyDown, onKeyUp) :: to_add
  }
  def addListeners(ll:List[TListener]) = {
    to_add = ll ::: to_add
  }

  var to_remove = List[TListener]()
  def removeKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Any) = {
    to_remove = new KeyListener(key, repeatTime, onKeyDown) :: to_remove
  }

  var last_key:Int = 0
  override def actionSequence():Unit = {
    if(to_add.length > 0) {
      listeners = to_add ::: listeners
      to_add = List[TListener]()
    }
    if(to_remove.length > 0) {
      listeners.filterNot(l => to_remove.contains(l))
      to_remove = List[TListener]()
    }
    listeners.foreach(l => l.check)
  }
}