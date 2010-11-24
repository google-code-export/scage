package su.msk.dunno.blame.support

import su.msk.dunno.screens.prototypes.Handler
import su.msk.dunno.blame.prototypes.Decision
import su.msk.dunno.blame.screens.FieldScreen

object TimeUpdater {
  private var _time = 0;
  def time = _time

  private var playerDecisions:List[Decision] = Nil
  def addPlayerDecision(d:Decision) = playerDecisions = playerDecisions ::: List(d)
  
  private var otherDecisions:List[Decision] = Nil
  def addDecision(d:Decision) = otherDecisions = otherDecisions ::: List(d)
  
  FieldScreen.addHandler(new Handler {
    override def action = {
      if(playerDecisions.length > 0) {
        playerDecisions.foreach(playerAction => {
          if(playerAction.execute) _time += playerAction.actionPeriod        
        })
      }
      playerDecisions = Nil
      
      val enemyActions = otherDecisions.filter(decision => 
        decision.living.lastActionTime + decision.actionPeriod <= _time)
      enemyActions.foreach(_.execute)
      otherDecisions = otherDecisions.filterNot(enemyActions.contains(_))
    }
  })
}
