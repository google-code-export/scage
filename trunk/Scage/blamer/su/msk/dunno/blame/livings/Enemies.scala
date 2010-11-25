package su.msk.dunno.blame.livings

import su.msk.dunno.blame.decisions.Move
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.blame.prototypes.Npc

class SiliconCreature(point:Vec) extends Npc(point, SILICON_CREATURE, CYAN) {
  setStat("name", "Sillicon Creature")
  
  def livingAI = {
    def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
    new Move(randomDir, living = this)
  }
}
