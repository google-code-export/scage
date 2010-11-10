package su.msk.dunno.screens.support.tracer

import collection.mutable.HashMap
import su.msk.dunno.scage.support.Vec

class State() {
  private val args:HashMap[String, StateData] = new HashMap[String, StateData]()

  def this(key:String) = {this(); args += key -> new StateData()}
  def put(key:String) = {args += key -> new StateData()}

  def this(key:String, int_num:Int) = {this(); args += key -> new StateData(int_num)}
  def put(key:String, int_num:Int):State = {
    if(args.contains(key)) args(key).int = int_num
    else args += key -> new StateData(int_num);
    this
  }
  def getInt(key:String) = {
    if(!args.contains(key)) 0
    else if(args(key).int != 0) args(key).int
    else if(args(key).float != 0) args(key).float.toInt
    else 0
  }

  def this(key:String, float_num:Float) = {this(); args += key -> new StateData(float_num)}
  def put(key:String, float_num:Float):State = {
    if(args.contains(key)) args(key).float = float_num
    else args += key -> new StateData(float_num);
    this
  }
  def getFloat(key:String) = {
    if(!args.contains(key)) 0
    else if(args(key).float != 0) args(key).float
    else if(args(key).int != 0) args(key).int
    else 0
  }

  def this(key:String, message:String) = {this(); args += key -> new StateData(message)}
  def put(key:String, message:String):State = {
    if(args.contains(key)) args(key).string = message
    else args += key -> new StateData(message);
    this
  }
  def getString(key:String) = {
    if(!args.contains(key)) ""
    else if(args(key).string != null) args(key).string
    else if(args(key).int != 0) args(key).int.toString
    else if(args(key).float != 0) args(key).float.toString
    else ""
  }

  def this(key:String, vec:Vec) = {this(); args += key -> new StateData(vec)}
  def put(key:String, new_vec:Vec):State = {
    if(args.contains(key)) args(key).vec = new_vec
    else args += key -> new StateData(new_vec);
    this
  }
  def getVec(key:String) = {
    if(!args.contains(key)) Vec(0,0)
    else args(key).vec
  }

  def this(key:String, b:Boolean) = {this(); args += key -> new StateData(b)}
  def put(key:String, b:Boolean):State = {
    if(args.contains(key)) args(key).bool = b
    else args += key -> new StateData(b);
    this
  }
  def getBool(key:String) = {
    if(!args.contains(key)) false
    else args(key).bool
  }
  
  def contains(key:String):Boolean = args.contains(key)
  
  private[State] class StateData() {
	  private var i = 0
	  def this(i:Int) = {this(); this.i = i;}
	  def int = i
    def int_= (new_i:Int) = i = new_i
	
	  private var f = 0.0f
	  def this(f:Float) = {this(); this.f = f;}
	  def float = f
    def float_= (new_f:Float) = f = new_f
	
	  private var s = ""
	  def this(s:String) = {this(); this.s = s;}
	  def string = s
    def string_= (new_s:String) = s = new_s
	
	  private var v = Vec(0,0)
	  def this(v:Vec) = {this(); this.v = v;}
	  def vec = v
    def vec_= (new_v:Vec) = v = new_v

    private var b = false
    def this(b:Boolean) = {this(); this.b = b;}
    def bool = b
    def bool_= (new_b:Boolean) = b = new_b
  }
}