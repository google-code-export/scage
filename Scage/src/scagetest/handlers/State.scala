package scagetest.handlers

import collection.mutable.HashMap
import su.msk.dunno.scage.support.Vec

class State() {
  val args:HashMap[String, StateData] = new HashMap[String, StateData]()

  def this(key:String) = {this(); args += key -> null}
  def this(key:String, int_num:Int) = {this(); args += key -> new StateData(int_num)}
  def this(key:String, float_num:Float) = {this(); args += key -> new StateData(float_num)}
  def this(key:String, message:String) = {this(); args += key -> new StateData(message)}
  def this(key:String, vec:Vec) = {this(); args += key -> new StateData(vec)}

  def put(key:String) = {args += key -> null}

  def put(key:String, int_num:Int) = {args += key -> new StateData(int_num)}
  def getInt(key:String) = {
    if(!args.contains(key))0
    else if(args(key).getInt != 0)args(key).getInt
    else if(args(key).getFloat != 0)args(key).getFloat.toInt
    else 0
  }

  def put(key:String, float_num:Float) = {args += key -> new StateData(float_num)}
  def getFloat(key:String) = {
    if(!args.contains(key))0
    else if(args(key).getFloat != 0)args(key).getFloat
    else if(args(key).getInt != 0)args(key).getInt
    else 0
  }

  def put(key:String, message:String) = {args += key -> new StateData(message)}
  def getString(key:String) = {
    if(!args.contains(key))""
    else if(args(key).getString != null)args(key).getString
    else if(args(key).getInt != 0)args(key).getInt.toString
    else if(args(key).getFloat != 0)args(key).getFloat.toString
    else ""
  }

  def put(key:String, vec:Vec) = {args += key -> new StateData(vec)}
  def getVec(key:String) = {
    if(!args.contains(key))Vec(0,0)
    else if(args(key).getVec != null)args(key).getVec
    else Vec(0,0)
  }
  
  def contains(key:String):Boolean = args.contains(key)
}