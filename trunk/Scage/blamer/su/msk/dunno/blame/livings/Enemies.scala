package su.msk.dunno.blame.livings

import su.msk.dunno.blame.decisions.Move
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.blame.prototypes.Npc
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.IngameMessages

class SiliconCreature(point:Vec) extends Npc(point, SILICON_CREATURE, CYAN) {
  setStat("name", "Sillicon Creature")
  setStat("health", 100)
  
  def livingAI = {
    def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
    new Move(randomDir, living = this)
  }

  override def changeStatus(s:State) = {
    if(s.contains("damage")) {
      changeStat("health", -s.getInt("damage"))
      IngameMessages.addBottomPropMessageSameString("changestatus.damage", stat("name"), s.getString("damage"))
    }
  }
}
