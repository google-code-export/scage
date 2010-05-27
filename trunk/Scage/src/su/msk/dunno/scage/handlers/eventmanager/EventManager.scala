package su.msk.dunno.scage.handlers.eventmanager

import su.msk.dunno.scage.prototypes.THandler
import org.lwjgl.opengl.Display
import su.msk.dunno.scage.main.Engine
object EventManager extends THandler {
  var listeners = List[TListener]()

  var to_add = List[TListener]()
  def addKeyListener(key:Int, onKeyDown: () => Unit) = {
    to_add = new KeyListener(key, onKeyDown) :: to_add
  }
  def addKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Unit) = {
    to_add = new KeyListener(key, repeatTime, onKeyDown) :: to_add
  }
    def addListeners(ll:List[TListener]) = {
    to_add = ll ::: to_add
  }

  var to_remove = List[TListener]()
  def removeKeyListener(key:Int, repeatTime:Long, onKeyDown: () => Unit) = {
    to_remove = new KeyListener(key, repeatTime, onKeyDown) :: to_remove
  }

  var last_key:Int = 0
  override def actionSequence():Unit = {
    if(Display.isCloseRequested())Engine.stop
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