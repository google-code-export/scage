package su.msk.dunno.scage.prototypes

import net.phys2d.raw.Body
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.support.Vec
trait Physical extends Drawable {
  val body:Body

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

  def touching():String = {
    def touching_(i:Int):String = {
      if(i < body.getTouching.size-1)body.getTouching.get(i).getVelocity.toString+"\n"+touching_(i+1)
      else body.getTouching.get(i).getVelocity.toString+" "+body.getTouching.get(i).getVelocity.lengthSquared
    }
    if(body.getTouching.size > 0)touching_(0)
    else ""
  }

  def ::(o:Physical) = o :: List[Physical](this)
}