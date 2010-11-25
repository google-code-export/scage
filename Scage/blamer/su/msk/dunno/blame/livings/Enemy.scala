package su.msk.dunno.blame.livings

import su.msk.dunno.blame.decisions.Move
import su.msk.dunno.blame.prototypes.{Living, Decision}
import su.msk.dunno.blame.screens.FieldScreen
import su.msk.dunno.screens.prototypes.Handler
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.scage.support.Vec

abstract class Enemy(point:Vec) extends Living(point) {
  protected var last_decision:Decision = null
  FieldScreen.addHandler(new Handler {
    override def action = {
      if(last_decision.wasExecuted || last_decision == null) last_decision = livingAI
    }
  })
  
  def livingAI:Decision
}

class SiliconCreature(point:Vec) extends Enemy(point) {
  override val trace = FieldTracer.addTrace(new FieldObject {
    def getCoord = FieldTracer.pointCenter(point)
    def getSymbol = SILICON_CREATURE
    def getColor = CYAN
    def isTransparent = true
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })

  def name = "Sillicon Creature"
  
  def livingAI = {
    def randomDir:Vec = Vec((math.random*3).toInt - 1, (math.random*3).toInt - 1)
    new Move(this, randomDir)
  }
}
