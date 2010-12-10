package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.scage.support.ScageColor
import su.msk.dunno.blame.screens.SelectTarget

abstract class Living(val point:Vec, symbol:Int, color:ScageColor) extends HaveStats {
  val trace = FieldTracer.addTrace(new FieldObject {
    def getCoord = FieldTracer.pointCenter(point)
    def getSymbol = symbol
    def getColor = color
    def isTransparent = true
    def isPassable = false

    def getState = stats
    def changeState(s:State) = changeStatus(s)
  })
  
  def changeStatus(s:State) = {}
  
  protected var last_action_time = 0
  def lastActionTime = last_action_time
  def lastActionTime_=(action_time:Int) = last_action_time = action_time
  
  val inventory = new Inventory(this)

  private lazy val target_selector = new SelectTarget(this)
  def selectTarget(stop_key:Int):Vec = {
    target_selector(stop_key)
  }

  setStat("health", 100)
}
