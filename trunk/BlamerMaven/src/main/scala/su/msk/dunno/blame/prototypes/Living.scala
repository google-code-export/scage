package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.scage.single.support.{ScageProperties, Vec, ScageColor}
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.blame.screens.{SelectTarget, Blamer}

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
        val damage = s.getFloat("damage")
        val shield = floatStat("shield")
        if(shield > damage) {
          changeStat("shield", -damage)
          BottomMessages.addPropMessageSameString("changestatus.damage.shield", stat("name"), s.getNumAsString("damage"))
        }
        else {
          setStat("shield", 0)
          changeStat("health", -(damage - shield))
          BottomMessages.addPropMessageSameString("changestatus.damage.noshield", stat("name"), s.getNumAsString("damage"))
          FieldTracer.pourBlood(trace, point, colorStat("blood"))
        }
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

  def selectTarget(stop_key:Int):Vec = SelectTarget(this, stop_key)

  setStat("living")
  setStat("name", name)
  setStat("description", description)
  setStat("dov", ScageProperties.property("dov.default", 5))
  setStat("health", 100); setStat("max_health", 100);
  setStat("blood", RED)

  def isAlive = intStat("health") > 0
  def isCurrentPlayer = haveStat("player") && point == Blamer.currentPlayer.getPoint

  def checkMax(effect_name:String, max_effect_name:String) = {
    if(floatStat(effect_name) > floatStat(max_effect_name))
      setStat(effect_name, floatStat(max_effect_name))
  }
  def checkMax:Unit = {  // TODO: rename this one!!!
    checkMax("energy", "max_energy")
    checkMax("shield", "max_shield")
    checkMax("health", "max_health")
  }

  def processTemporaryEffects = { // TODO: rename this one!!!
    def _process(effect_name:String, max_effect_name:String, effect_increase_rate_name:String) = {
      if(floatStat(effect_name) < floatStat(max_effect_name)) {
        changeStat(effect_name, floatStat(effect_increase_rate_name))
        checkMax(effect_name, max_effect_name)
      }
    }
    _process("energy", "max_energy", "energy_increase_rate")
    _process("shield", "max_shield", "shield_increase_rate")
  }
}
