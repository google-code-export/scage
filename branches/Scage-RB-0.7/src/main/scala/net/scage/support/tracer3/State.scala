package net.scage.support.tracer3

import collection.mutable.HashMap

class State(args:Any*) extends HashMap[String, Any] {
  //def this(args:String*) {this(args.map((_, true)):_*)}
  
  args.foreach(arg => {
    arg match {
      case elem:(String, Any) => this += elem
      case elem:Any => this += (elem.toString -> true)
    }
  })

  //this ++= args
  def neededKeys(needed_keys:String*)(foreach_func:((String, Any)) => Any) {
    foreach(elem => if(needed_keys.contains(elem._1)) foreach_func(elem))
  }

  override def toString() = mkString("State(", ", ", ")")
}

object State {
  def apply(args:(String, Any)*) = new State(args:_*)
  def unapplySeq(data:Any) = {
    data match {
      case state:State => Some(state.toList.sortWith((e1, e2) => e1._1 < e2._1))
      case _ => None
    }
  }
}