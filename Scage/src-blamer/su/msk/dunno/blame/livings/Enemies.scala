package su.msk.dunno.blame.livings

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.decisions.{Shoot, Move}
import su.msk.dunno.blame.prototypes.{Decision, Npc}
import su.msk.dunno.scage.support.messages.ScageMessage

class SiliconCreature(point:Vec)
extends Npc(name = ScageMessage.xml("enemy.siliconcreature.name"),
            description = ScageMessage.xml("enemy.siliconcreature.description"),
            point, SILICON_CREATURE, CYAN) {
  def livingAI:Decision = {
    def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
    val dov = intStat("dov")
    FieldTracer.neighboursOfPoint(trace, point, dov).foreach(neighbour => {
      if(neighbour.getState.getBool("is_player")) {
        if((point dist neighbour.getPoint) > 3) {       
    	  val step = FieldTracer.direction(point, neighbour.getPoint)
          if(FieldTracer.isPointPassable(point+step)) 
            return new Move(step, this)
          else return new Move(randomDir, living = this)
        }
        else return new Shoot(neighbour.getPoint, this)
      }
    })    
    return new Move(randomDir, living = this)
  }

  override def changeState(s:State) = {
    if(s.contains("damage")) {
      changeStat("health", -s.getInt("damage"))
      BottomMessages.addPropMessageSameString("changestatus.damage", stat("name"), s.getString("damage"))
    }
  }
}
