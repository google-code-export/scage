package scagetest.handlers

import su.msk.dunno.scage.handlers.tracer.{Tracer, State}
import net.phys2d.raw.Body
import net.phys2d.raw.shapes.Box
import collection.mutable.HashMap

object BodyTracer extends Tracer[BodyState]

class BodyState extends State {
  private val body_args:HashMap[String, BodyStateData] = new HashMap[String, BodyStateData]()
  
  def this(key:String, body:Body) = {this(); body_args += key -> new BodyStateData(body)}
  def put(key:String, body:Body) = {body_args += key -> new BodyStateData(body)}
  def getBody(key:String) = {
    if(!body_args.contains(key))null
    else body_args(key).body
  }
  
  private[BodyState] class BodyStateData {
	private var b:Body = new Body(new Box(1,1), 1)
	def this(b:Body) = {this(); this.b = b;}
	def body() = b
	def body_(b:Body) = {this.b = b}
  }
}