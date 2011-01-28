package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.prototypes.ScageAction
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.scage.support.{ScageColor, Vec}
import su.msk.dunno.blame.support.TimeUpdater

abstract class Npc(name:String, description:String, point:Vec, symbol:Int, color:ScageColor)
extends Living(name, description, point, symbol, color) {
  protected var last_decision:Decision = null
  Blamer.addAction(new ScageAction {
    override def action = {
      if(isAlive &&
         (last_decision == null || TimeUpdater.time - lastActionTime >= last_decision.action_period)) {
        last_decision = livingAI
        if(last_decision != null) TimeUpdater.addDecision(last_decision)
      }
    }
  })

  def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
  def livingAI:Decision
}
