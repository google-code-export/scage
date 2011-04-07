package su.msk.dunno.scage.screens.physics

import net.phys2d.raw.Body
import net.phys2d.raw.BodyList
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.single.support.Vec

trait Physical {
  val body:Body

  def prepare {
    is_active = true
    is_touching = false
    touching_bodies = new BodyList
  }

  private var is_active = true
  def isActive = is_active
  def isActive_=(activity:Boolean) {is_active = activity}

  def addForce(force:Vec) = {
    body.setIsResting(false)
    body.addForce(new Vector2f(force.x, force.y))
  }

  def coord = {
    val pos = body.getPosition
    Vec(pos.getX, pos.getY)
  }
  def coord_=(new_coord:Vec) = body.move(new_coord.x, new_coord.y)
  def move(delta:Vec) = {
    val new_coord = coord + delta
    body.move(new_coord.x, new_coord.y)
  }

  def velocity = {
    val vel = body.getVelocity
    Vec(vel.getX, vel.getY)
  }
  def velocity_=(new_velocity:Vec) = {
    val delta = new_velocity - velocity
    body.adjustVelocity(new Vector2f(delta.x, delta.y))
  }

  private var is_touching = false
  private var touching_bodies:BodyList = new BodyList
  def isTouching = is_touching
  def isTouching(p:Physical) = touching_bodies.contains(p.body)
  private[physics] def isTouching_=(new_is_touching:Boolean) {
    is_touching = new_is_touching
    if(is_touching) {
      val new_touching_bodies =  body.getTouching
      for {
        i <- 0 until new_touching_bodies.size
        body = new_touching_bodies.get(i)
        if !touching_bodies.contains(body)
      } touching_bodies.add(body)
    }
    else touching_bodies.clear
  }

  def render
}