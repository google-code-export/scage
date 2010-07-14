package su.msk.dunno.scage.handlers.tracer

import collection.mutable.HashMap
import su.msk.dunno.scage.support.Vec

class State() {
  private val args:HashMap[String, StateData] = new HashMap[String, StateData]()

  def this(key:String) = {this(); args += key -> null}
  def put(key:String) = {args += key -> null}

  def this(key:String, int_num:Int) = {this(); args += key -> new StateData(int_num)}
  def put(key:String, int_num:Int) = {args += key -> new StateData(int_num)}
  def getInt(key:String) = {
    if(!args.contains(key))0
    else if(args(key).int != 0)args(key).int
    else if(args(key).float != 0)args(key).float.toInt
    else 0
  }

  def this(key:String, float_num:Float) = {this(); args += key -> new StateData(float_num)}
  def put(key:String, float_num:Float) = {args += key -> new StateData(float_num)}
  def getFloat(key:String) = {
    if(!args.contains(key))0
    else if(args(key).float != 0)args(key).float
    else if(args(key).int != 0)args(key).int
    else 0
  }

  def this(key:String, message:String) = {this(); args += key -> new StateData(message)}
  def put(key:String, message:String) = {args += key -> new StateData(message)}
  def getString(key:String) = {
    if(!args.contains(key))""
    else if(args(key).string != null)args(key).string
    else if(args(key).int != 0)args(key).int.toString
    else if(args(key).float != 0)args(key).float.toString
    else ""
  }

  def this(key:String, vec:Vec) = {this(); args += key -> new StateData(vec)}
  def put(key:String, vec:Vec) = {args += key -> new StateData(vec)}
  def getVec(key:String) = {
    if(!args.contains(key))Vec(0,0)
    else if(args(key).vec != null)args(key).vec
    else Vec(0,0)
  }
  
  def contains(key:String):Boolean = args.contains(key)
  
  private[State] class StateData {
	  private var i = 0
	  def this(i:Int) = {this(); this.i = i;}
	  def int() = i
	  def int_(i:Int) = {this.i = i}
	
	  private var f = 0.0f
	  def this(f:Float) = {this(); this.f = f;}
	  def float() = f
	  def float_(f:Float) = {this.f = f}
	
	  private var s = ""
	  def this(s:String) = {this(); this.s = s;}
	  def string() = s
	  def string_(s:String) = {this.s = s}
	
	  private var v = Vec(0,0)
	  def this(v:Vec) = {this(); this.v = v;}
	  def vec() = v
	  def vec_(v:Vec) = {this.v = v}
  }
}