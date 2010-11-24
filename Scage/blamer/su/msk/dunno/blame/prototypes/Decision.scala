package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.support.TimeUpdater

abstract class Decision(val living:Living) {
  protected var action_period = 0
  def actionPeriod = action_period
  
  protected var was_executed = false
  def wasExecuted = was_executed
  
  protected def doAction:Boolean
  def execute:Boolean = {
    if(doAction) {living.lastActionTime = TimeUpdater.time; true}
    else false
  }
}
