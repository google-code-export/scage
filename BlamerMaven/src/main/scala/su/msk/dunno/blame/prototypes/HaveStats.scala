package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.scage.single.support.ScageColor

trait HaveStats {
  // stats
  protected val stats = new State
  def haveStat(key:String):Boolean = stats.contains(key)
  def boolStat(key:String):Boolean = stats.getBool(key)
  def intStat(key:String):Int = stats.getInt(key)
  def floatStat(key:String):Float = stats.getFloat(key)
  def colorStat(key:String):ScageColor = stats.getColor(key)
  def stateStat(key:String):State = stats.getState(key)
  def stat(key:String):String = stats.getString(key)

  def setStat(key:String) = stats.put(key)
  def removeStat(key:String) = stats.remove(key)
  
  def setStat[A](key:String, value:A)(implicit m:Manifest[A]) = {
    m.toString match {
      case "Int" => stats.put(key, value.asInstanceOf[Int])
      case "Float" => stats.put(key, value.asInstanceOf[Float])
      case "Boolean" => stats.put(key, value.asInstanceOf[Boolean])
      case "su.msk.dunno.scage.single.support.ScageColor" => stats.put(key, value.asInstanceOf[ScageColor])
      case "su.msk.dunno.scage.screens.support.tracer.State" => stats.put(key, value.asInstanceOf[State])
      case _ => stats.put(key, value.asInstanceOf[String])
    }
  }

  def changeStat(key:String, delta:Float) = {
    val old_value = stats.getFloat(key)
    stats.put(key, old_value + delta)
  }
}
