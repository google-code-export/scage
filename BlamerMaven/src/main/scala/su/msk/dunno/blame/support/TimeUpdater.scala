package su.msk.dunno.blame.support

import su.msk.dunno.scage.screens.prototypes.ScageAction
import su.msk.dunno.blame.prototypes.Decision
import su.msk.dunno.blame.screens.Blamer

object TimeUpdater {
  private var _time = 0;
  def time = _time

  private var decisions:List[Decision] = Nil
  def addDecision(decision:Decision) = decisions = decisions ::: List(decision)

  Blamer.addAction(new ScageAction {
    override def action = {
      var current_actions = decisions.filter(decision =>
        decision.living.isAlive &&
        (decision.living.lastActionTime + decision.actionPeriod <= _time || decision.living.isCurrentPlayer))
      while(!current_actions.isEmpty) {
        current_actions.foreach(action => {
          action.execute
          if(action.wasExecuted && action.living.isCurrentPlayer) _time += action.actionPeriod
        })
        decisions = decisions.filterNot(decision => current_actions.contains(decision) || !decision.living.isAlive)
        current_actions = decisions.filter(decision =>
          decision.living.isAlive &&
          (decision.living.lastActionTime + decision.actionPeriod <= _time || decision.living.isCurrentPlayer))
      }
    }
  })
} 
