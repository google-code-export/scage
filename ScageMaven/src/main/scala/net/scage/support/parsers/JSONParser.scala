package net.scage.support.parsers

import scala.util.parsing.combinator._
import com.weiglewilczek.slf4s.Logger
import net.scage.support.State

/**
 * Json parser based on example from "Programming in Scala, 2nd edition"
 */
// TODO: add deserialization for Vec and ScageColor
class JSONParser extends JavaTokenParsers {
  private val log = Logger(this.getClass.getName)

  // TODO: def => private lazy val
  private lazy val obj: Parser[State] =
    "{"~> repsep(member, ",") <~"}" ^^ (State() ++= _)  // State instead of Map[String, Any] because of issues with deserialization from json of structures like array of objects

  private lazy val arr: Parser[List[Any]] =
    "["~> repsep(value, ",") <~"]"

  private lazy val anyString = ("""([^"\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*""").r

  private lazy val member: Parser[(String, Any)] =
    "\""~anyString~"\""~":"~value ^^ { case "\""~member_name~"\""~":"~member_value => (member_name, member_value) }

  private lazy val value: Parser[Any] = (
    obj
    | arr
    | "\""~anyString~"\"" ^^ {case "\""~name~"\"" => name}
    | floatingPointNumber ^^ (_.toFloat)
    | "null" ^^ (x => null)
    | "true" ^^ (x => true)
    | "false" ^^ (x => false)
  )

  def evaluate(json:String) =
    parseAll(obj, json) match {
      case Success(result, _) => {
        log.debug("successfully parsed json:\n"+json)
        result
      }
      case x @ Failure(msg, _) => { // maybe throw exceptions instead
        log.error("failed to parse json: "+msg+"\njson string corrupted:\n"+json)
        Map[String, Any]()
      }
      case x @ Error(msg, _) => {
        log.error("failed to parse json: "+msg+"\njson string corrupted:\n"+json)
        Map[String, Any]()
      }
    }
}
