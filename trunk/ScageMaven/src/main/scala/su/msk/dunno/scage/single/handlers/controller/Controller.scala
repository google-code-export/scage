package su.msk.dunno.scage.single.handlers.controller

import su.msk.dunno.scage.single.Scage

object Controller {
  private var listeners = List[UIListener]()

  private var to_add = List[UIListener]()
  def keyListener(key:Int, repeatTime:Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) = {
	  to_add = new KeyListener(key, repeatTime, onKeyDown, onKeyUp) :: to_add
  }

  def isKeyPressed = listeners.exists(l => l.wasPressed)

  /*private var to_remove = List[UIListener]()
    def removeKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Any) = {
      to_remove = new KeyListener(key, repeatTime, onKeyDown) :: to_remove
  }*/

  var last_key:Int = 0

  Scage.action {
    if(to_add.length > 0) {
      listeners = to_add ::: listeners
      to_add = List[UIListener]()
    }
    /*if(to_remove.length > 0) {
      listeners.filterNot(l => to_remove.contains(l))
      to_remove = List[UIListener]()
    }*/
    listeners.foreach(l => l.check)
  }
}