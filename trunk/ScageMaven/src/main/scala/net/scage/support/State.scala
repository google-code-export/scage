package net.scage.support

import collection.mutable.HashMap
import parsers.JSONParser

/**
 * Represents JSON Object
 */
// TODO: add example usages and tell about alphabetical order for keys in patterns!
class State(args:Any*) extends HashMap[String, Any] {
  add(args:_*)

  def neededKeys(needed_keys:String*)(foreach_func:((String, Any)) => Any) {
    foreach(elem => if(needed_keys.contains(elem._1)) foreach_func(elem))
  }
  
  def add(args:Any*):this.type = {
    args.foreach(arg => {
      arg match {
        case elem:(String, Any) => this += (elem)
        case elem:Any => this += ((elem.toString -> true))
      }
    })
    this
  }  
  def addJson(json:String) {this ++= State.json_parser.evaluate(json)}

  /*import scalaz._
  Identity(("string": Any)).matchOrZero { case s: String => s.toSeq }*/

  override def toString() = mkString("State(", ", ", ")")

  private def vec2Json(v:Vec) = "{\"type\":\"vec\", \"x\":"+v.x+", \"y\":"+v.y+"}"
  private def color2Json(c:ScageColor) = "{\"type\":\"color\", \"name\":\""+c.name+"\", \"red\":"+c.red+", \"green\":"+c.green+", \"blue\":"+c.blue+"}"
  private def list2JsonArrayString(l:List[Any]):String = {  // maybe make it public and move to State object. I'll do it on real purpose appeared
    val sb = new StringBuilder("[")
    for {
      (value, index) <- l.zipWithIndex
      opt_comma = if(index != l.size-1) ", " else ""
    } {
      value match {
        case s:State => sb.append(s.toJsonString+opt_comma)
        case l:List[Any] => sb.append(list2JsonArrayString(l)+opt_comma)
        case str:String => sb.append("\""+str+"\""+opt_comma)
        case any_other_val => sb.append(any_other_val.toString+opt_comma)
      }
    }
    sb.append("]")
    sb.toString()
  }
  def toJsonString:String = {
    val sb = new StringBuilder("{")
    for {
      (key, index) <- keys.zipWithIndex
      value = apply(key)
      opt_comma = if(index != keys.size-1) ", " else ""
    } {
      value match {
        case s:State => sb.append("\""+key+"\":"+s.toJsonString+opt_comma)
        case l:List[Any] => sb.append("\""+key+"\":"+list2JsonArrayString(l)+opt_comma)
        case str:String => sb.append("\""+key+"\":\""+str+"\""+opt_comma)
        case v:Vec => sb.append("\""+key+"\":"+vec2Json(v)+opt_comma)
        case c:ScageColor => sb.append("\""+key+"\":"+color2Json(c)+opt_comma)
        case any_other_val => sb.append("\""+key+"\":"+any_other_val.toString+opt_comma)
      }
    }
    sb.append("}")
    sb.toString()
  }
}

object State {
  def apply(args:Any*) = new State(args:_*)
  def unapplySeq(data:Any) = {
    data match {
      case state:State => Some(state.toList.sortWith((e1, e2) => e1._1 < e2._1))
      case _ => None
    }
  }

  private lazy val json_parser = new JSONParser
  def fromJson(json:String):State = new State() ++= json_parser.evaluate(json)  // we catch exceptions in JSONParser. Maybe we should do it here.
}