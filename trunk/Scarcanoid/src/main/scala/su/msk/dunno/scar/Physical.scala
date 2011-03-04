package su.msk.dunno.scar

import net.phys2d.raw.Body
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.prototypes.ScageRender

abstract class Physical {
  val body:Body

  private var is_active = true
  def isActive = is_active
  def isActive_=(new_is_active:Boolean) = {
    is_active = new_is_active
    if(!is_active) Physics.removeBody(body)
  }


  def addForce(force:Vec) = {
    body.setIsResting(false)
    body.addForce(new Vector2f(force.x, force.y))
  }

  def coord = {
    val pos = body.getPosition
    Vec(pos.getX, pos.getY)
  }

  def velocity = {
    val vel = body.getVelocity
    Vec(vel.getX, vel.getY)
  }

  def move(delta:Vec) = {
    val new_coord = coord + delta
    body.move(new_coord.x, new_coord.y)
  }

  private var is_touching = false
  def isTouching = is_touching
  def isTouching_=(new_is_touching:Boolean) = is_touching = new_is_touching

  def renderFunc
  Scaranoid.addRender(new ScageRender {
    override def render = if(is_active) renderFunc
  })
}