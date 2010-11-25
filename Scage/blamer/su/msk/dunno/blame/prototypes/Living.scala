package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State

abstract class Living(val point:Vec) {
  val trace = -1
  
  protected var last_action_time = 0
  def lastActionTime = last_action_time
  def lastActionTime_=(action_time:Int) = last_action_time = action_time
  
  // stats
  private val stats = new State
  def boolStat(key:String):Boolean = stats.getBool(key)
  def floatStat(key:String):Float = stats.getFloat(key)
  def stat(key:String):String = stats.getString(key)
  
  def setStat[A](key:String, value:A)(implicit m:Manifest[A]) = {
    m.toString match {
      case "Float" => stats.put(key, value.asInstanceOf[Float])
      case "Boolean" => stats.put(key, value.asInstanceOf[Boolean])
      case "String" => stats.put(key, value.asInstanceOf[String])
      case _ =>
    }
  }
  
  def changeStat(key:String, delta:Float) = {
    val old_value = stats.getFloat(key)
    stats.put(key, old_value + delta)
  }
}
