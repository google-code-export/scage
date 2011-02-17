package su.msk.dunno.blame.livings

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.decisions.{Shoot, Move}
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.blame.prototypes.{Decision, Npc}

class SiliconCreature(point:Vec)
extends Npc(name        = xml("enemy.siliconcreature.name"),
            description = xml("enemy.siliconcreature.description"),
            point, SILICON_CREATURE, CYAN) {
  setStat("enemy")
  setStat("health", 20)
  //setStat("blood", CYAN)
  setStat("speed", 2)

  def livingAI:Decision = {
    val dov = intStat("dov")
    FieldTracer.visibleObjectsNear(trace, point, dov, obj => {
      obj.getState.contains("player") && obj.getState.getInt("health") > 0
    }).foreach(neighbour => {
      if((point dist neighbour.getPoint) > 3) {
    	  val step = FieldTracer.direction(point, neighbour.getPoint)
        if(FieldTracer.isPointPassable(point+step))
          return new Move(this, step)
        else return new Move(living = this, randomDir)
      }
      else return new Shoot(this, neighbour.getPoint)
    })    
    return new Move(living = this, randomDir)
  }

  override def onDeath = {
    super.onDeath
    setStat("name", xml("enemy.siliconcreature.dead.name"))
  }
}
