package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}
import su.msk.dunno.scage.support.Color

abstract class Living(val point:Vec, symbol:Int, color:Color) {
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
  
  // stats
  private val stats = new State
  def boolStat(key:String):Boolean = stats.getBool(key)
  def intStat(key:String):Int = stats.getInt(key)
  def floatStat(key:String):Float = stats.getFloat(key)
  def stat(key:String):String = stats.getString(key)
  
  def setStat[A](key:String, value:A)(implicit m:Manifest[A]) = {
    m.toString match {
      case "Int" => stats.put(key, value.asInstanceOf[Int])
      case "Float" => stats.put(key, value.asInstanceOf[Float])
      case "Boolean" => stats.put(key, value.asInstanceOf[Boolean])
      case _ => stats.put(key, value.asInstanceOf[String])
    }
  }
  
  def changeStat(key:String, delta:Float) = {
    val old_value = stats.getFloat(key)
    stats.put(key, old_value + delta)
  }
}
