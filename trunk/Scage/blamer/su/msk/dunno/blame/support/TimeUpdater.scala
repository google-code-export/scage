package su.msk.dunno.blame.support

import su.msk.dunno.screens.prototypes.ActionHandler
import su.msk.dunno.blame.prototypes.Decision
import su.msk.dunno.blame.screens.FieldScreen

object TimeUpdater {
  private var _time = 0;
  def time = _time

  private var decisions:List[Decision] = Nil
  def addDecision(decision:Decision) = decisions = decisions ::: List(decision)

  FieldScreen.addHandler(new ActionHandler {
    override def action = {
      var current_actions = decisions.filter(decision =>
        decision.living.lastActionTime + decision.actionPeriod <= _time || decision.living.boolStat("is_player"))
      while(!current_actions.isEmpty) {
        current_actions.foreach(action => {
          action.execute
          if(action.living.boolStat("is_player")) _time += action.actionPeriod
        })
        decisions = decisions.filterNot(current_actions.contains(_))
        current_actions = decisions.filter(decision =>
          decision.living.lastActionTime + decision.actionPeriod <= _time || decision.living.boolStat("is_player"))
      }
    }
  })
} 
