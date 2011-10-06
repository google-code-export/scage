package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.support.TimeUpdater

abstract class Decision(val living:Living) {
  val action_period = 0
  
  protected var was_executed = false
  def wasExecuted = was_executed
  
  protected def doAction
  def execute = {
    doAction
    if(wasExecuted) living.lastActionTime = TimeUpdater.time
  }
}
