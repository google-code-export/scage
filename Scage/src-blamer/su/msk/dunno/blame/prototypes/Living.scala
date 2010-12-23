package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.blame.screens.SelectTarget
import su.msk.dunno.scage.support.{ScageProperties, Vec, ScageColor}

abstract class Living(val name:String,
                      val description:String,
                      private val point:Vec,
                      private val symbol:Int,
                      private val color:ScageColor)
extends FieldObject with HaveStats {
  def getCoord = FieldTracer.pointCenter(point)
  override def getPoint = point
  def getSymbol = symbol
  def getColor = color
  def isTransparent = true
  def isPassable = false

  def getState = stats
  def changeState(s:State) = {}

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
