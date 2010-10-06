package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.Handler
import collection.mutable.HashMap
import org.lwjgl.input.Keyboard

object Controller2 extends Handler {
  private var keys:HashMap[Int, List[KeyState]] = new HashMap[Int, List[KeyState]]()

  private var next_listener_id = 0
  def nextListener = {
    val next_listener = next_listener_id
    next_listener_id += 1
    next_listener
  }

  def addKey(key:Int) = {
    val listener = nextListener
    val ks = new KeyState(listener)
    if(keys.contains(key)) keys(key) = ks :: keys(key)
    else keys += key -> List(ks)
    ks
  }

  override def actionSequence():Unit = {
    keys = keys.map(key => {
      if(Keyboard.isKeyDown(key._1)) key._2.foreach(keystate => {
        keystate.is_pressed = true
        keystate.num_pressed += 1
        //println(keystate)
      })
      else key._2.foreach(keystate => {
        keystate.is_pressed = false
      })
      //println(key)
      key
    })
  }
}

class KeyState(val listener_id:Int) {
  private[handlers] var is_pressed:Boolean = false
  def isPressed = is_pressed

  private[handlers] var num_pressed:Int = 0
  def numPressed = num_pressed

  override def toString:String = isPressed + " : " + numPressed
}