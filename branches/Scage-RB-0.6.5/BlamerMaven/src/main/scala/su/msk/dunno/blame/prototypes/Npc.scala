package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.screens.prototypes.ScageAction
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.scage.single.support.{ScageColor, Vec}
import su.msk.dunno.blame.support.TimeUpdater
import su.msk.dunno.blame.decisions.DoNothing

abstract class Npc(name:String, description:String, point:Vec, symbol:Int, color:ScageColor)
extends Living(name, description, point, symbol, color) {
  protected var last_decision:Decision = new DoNothing(this)
  Blamer.addAction(new ScageAction {
    override def action = {
      if(isAlive && TimeUpdater.time - lastActionTime >= last_decision.actionPeriod) {
        last_decision = livingAI
        TimeUpdater.addDecision(last_decision)
      }
    }
  })

  def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
  def livingAI:Decision
}
