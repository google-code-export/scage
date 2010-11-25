package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.prototypes.ActionHandler
import su.msk.dunno.blame.screens.FieldScreen
import su.msk.dunno.scage.support.{Color, Vec}

abstract class Npc(point:Vec, symbol:Int, color:Color) extends Living(point, symbol, color) {
  protected var last_decision:Decision = null
  FieldScreen.addHandler(new ActionHandler {
    override def action = {
      if(last_decision.wasExecuted || last_decision == null) last_decision = livingAI
    }
  })

  def livingAI:Decision
}
