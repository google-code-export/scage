package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.support.tracer.State

trait HaveStats {
  // stats
  protected val stats = new State
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
