package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.support.TimeUpdater

abstract class Decision(val living:Living) {
  def actionPeriod = 5 - living.intStat("speed")
  
  protected var was_executed = false
  def wasExecuted = was_executed
  
  protected def doAction
  def execute = {
    doAction
    living.processTemporaryEffects
    if(wasExecuted) living.lastActionTime += actionPeriod
  }
}
