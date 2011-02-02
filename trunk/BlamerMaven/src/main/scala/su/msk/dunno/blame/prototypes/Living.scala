package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.scage.single.support.{ScageProperties, Vec, ScageColor}
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.blame.screens.Blamer

abstract class Living(val name:String,
                      val description:String,
                      point:Vec,
                      private val symbol:Int,
                      private val color:ScageColor)
extends FieldObject(point) with HaveStats {
  def getSymbol = if(isAlive) symbol else CORPSE
  def getColor = if(isAlive) color else WHITE
  def isTransparent = true
  def isPassable = if(isAlive) false else true

  def getState = stats
  def changeState(s:State) = {
    if(isAlive) {
      if(s.contains("damage")) {
        changeStat("health", -s.getInt("damage"))
        BottomMessages.addPropMessageSameString("changestatus.damage", stat("name"), s.getNumAsString("damage"))
        FieldTracer.pourBlood(trace, point, colorStat("blood"))
      }
      if(!isAlive) onDeath
    }
  }

  def onDeath = {
    BottomMessages.addPropMessage("changestatus.dead", stat("name"))
    if(FieldTracer.isLightSource(trace)) FieldTracer.removeLightSource(trace)
  }

  val trace = FieldTracer.addTrace(this)
  
  protected var last_action_time = 0
  def lastActionTime = last_action_time
  def lastActionTime_=(action_time:Int) = last_action_time = action_time

  val inventory = new Inventory(this)
  val weapon = new Weapon(this)

  setStat("living")
  setStat("name", name)
  setStat("description", description)
  setStat("dov", ScageProperties.property("dov.default", 5))
  setStat("health", 100)
  setStat("blood", RED)

  def isAlive = intStat("health") > 0
  def isCurrentPlayer = haveStat("player") && point == Blamer.currentPlayer.getPoint
}
