package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.blame.screens.SelectTarget
import su.msk.dunno.scage.support.{ScageProperties, Vec, ScageColor}
import su.msk.dunno.blame.support.BottomMessages

abstract class Living(val name:String,
                      val description:String,
                      point:Vec,
                      private val symbol:Int,
                      private val color:ScageColor)
extends FieldObject(point) with HaveStats {
  def getSymbol = symbol
  def getColor = color
  def isTransparent = true
  def isPassable = false

  def getState = stats
  def changeState(s:State) = {
    if(s.contains("damage")) {
      changeStat("health", -s.getInt("damage"))
      BottomMessages.addPropMessageSameString("changestatus.damage", stat("name"), s.getString("damage"))
    }
  }

  val trace = FieldTracer.addTrace(this)
  
  protected var last_action_time = 0
  def lastActionTime = last_action_time
  def lastActionTime_=(action_time:Int) = last_action_time = action_time
  
  val inventory = new Inventory(this)
  val weapon = new Weapon(this)

  private lazy val target_selector = new SelectTarget(this)
  def selectTarget(stop_key:Int):Vec = {
    target_selector(stop_key)
  }

  setStat("living")
  setStat("name", name)
  setStat("description", description)
  setStat("dov", ScageProperties.property("dov.default", 5))
  setStat("health", 100)
}
