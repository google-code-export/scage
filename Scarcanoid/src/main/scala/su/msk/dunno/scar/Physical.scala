package su.msk.dunno.scar

import net.phys2d.raw.Body
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.single.support.Vec

trait Physical {
  def initBody:Body
  def setupBody = {}
  val body:Body = initBody
  Physics.addBody(body)

  def addForce(force:Vec) = {
    body.setIsResting(false)
    body.addForce(new Vector2f(force.x, force.y))
  }

  def coord() = {
    val pos = body.getPosition
    Vec(pos.getX, pos.getY)
  }

  def velocity = {
    val vel = body.getVelocity
    Vec(vel.getX, vel.getY)
  }

  def isTouching():Boolean = {
    //body.getTouching.size > 0
    for(i <- 0 to body.getTouching.size-1; val b:Body = body.getTouching.get(i))if(b.isStatic)return true
    false
  }
}