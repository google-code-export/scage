package net.scage.support.tracer3

import net.scage.support.Vec
import net.scage.support.ScageId._

object Trace {
  def apply(changeState:(Trace, State) => Unit = (changer, state) => {},
            state:State = State()) = {
    val (_changeState, _state) = (changeState, state)
    new Trace {
      def changeState(changer:Trace,  state:State) {_changeState(changer, state)}
      def state:State = _state
    }
  }
}

trait Trace {
  def changeState(changer:Trace,  state:State)  // changer type must be the type of actual Trace's child in client code
  def changeState(state:State) {changeState(null, state)}
  def state:State

  val id = nextId
  private[tracer3] var _location = Vec(0, 0)
  def location:Vec = _location
}