package su.msk.dunno.blame.support

import su.msk.dunno.screens.prototypes.Handler
import su.msk.dunno.blame.prototypes.Decision
import su.msk.dunno.blame.screens.FieldScreen

object TimeUpdater {
  private var _time = 0;
  def time = _time

  private var decisions:List[Decision] = Nil
  def addDecision(decision:Decision) = decisions = decisions ::: List(decision)

  FieldScreen.addHandler(new Handler {
    override def action = {
      val current_actions = decisions.filter(decision =>
        decision.living.lastActionTime + decision.actionPeriod <= _time || decision.living.isPlayer)
      current_actions.foreach(action => {
        action.execute
        if(action.living.isPlayer) _time += action.actionPeriod
      })
      decisions = decisions.filterNot(current_actions.contains(_))
    }
  })
}
