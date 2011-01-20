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
  setStat("health", 20)

  def livingAI:Decision = {
    def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
    val dov = intStat("dov")
    FieldTracer.neighboursOfPoint(trace, point, dov).foreach(neighbour => {
      if(neighbour.getState.contains("player") && neighbour.getState.getInt("health") > 0) {
        if((point dist neighbour.getPoint) > 3) {       
    	  val step = FieldTracer.direction(point, neighbour.getPoint)
          if(FieldTracer.isPointPassable(point+step)) 
            return new Move(step, this)
          else return new Move(randomDir, living = this)
        }
        else return new Shoot(this, neighbour.getPoint)
      }
    })    
    return new Move(randomDir, living = this)
  }
}
