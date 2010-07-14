package su.msk.dunno.scage.handlers

import net.phys2d.raw.strategies.QuadSpaceStrategy
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.main.Scage
import net.phys2d.raw.{Body, World}
import su.msk.dunno.scage.prototypes.{THandler}

object Physics extends THandler {
  val dt = Scage.getIntProperty("dt")
  val gravity = Scage.getIntProperty("gravity")
  val friction = Scage.getFloatProperty("friction")
  val world = new World(new Vector2f(0.0f, gravity), 10, new QuadSpaceStrategy(20,5));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)

  def addBody(b:Body) = if(!world.getBodies.contains(b)) {
    //b.setFriction(friction)
    world.add(b)
  }

  override def actionSequence() = if(!Scage.onPause)for(i <- 1 to dt)world.step()
}