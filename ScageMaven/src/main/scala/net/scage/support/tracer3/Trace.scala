package net.scage.support.tracer3

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
  def changeState(changer:Trace,  state:State)
  def changeState(state:State) {changeState(null, state)}
  def state:State
}