package su.msk.dunno.scage.handlers.tracer

import collection.mutable.HashMap

class State() {
  val args:HashMap[String, StateData] = new HashMap[String, StateData]()

  def this(key:String) = {this(); args += key -> null}
  def this(key:String, int_num:Int) = {this(); args += key -> new StateData(int_num)}
  def this(key:String, float_num:Float) = {this(); args += key -> new StateData(float_num)}
  def this(key:String, message:String) = {this(); args += key -> new StateData(message)}

  def put(key:String) = {args += key -> null}

  def putInt(key:String, int_num:Int) = {args += key -> new StateData(int_num)}
  def getInt(key:String) = {
    if(args(key).getInt != 0)args(key).getInt
    else if(args(key).getFloat != 0)args(key).getFloat.toInt
    else 0
  }

  def putFloat(key:String, float_num:Float) = {args += key -> new StateData(float_num)}
  def getFloat(key:String) = {
    if(args(key).getFloat != 0)args(key).getFloat
    else if(args(key).getInt != 0)args(key).getInt
    else 0
  }

  def putString(key:String, message:String) = {args += key -> new StateData(message)}
  def getString(key:String) = {
    if(args(key).getString != null)args(key).getString
    else if(args(key).getInt != 0)args(key).getInt.toString
    else if(args(key).getFloat != 0)args(key).getFloat.toString
    else ""
  }
}