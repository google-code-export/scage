package su.msk.dunno.scage.handlers

import _root_.net.phys2d.math.Vector2f
import _root_.net.phys2d.raw.{Body, World}
import _root_.net.phys2d.raw.strategies.QuadSpaceStrategy
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.prototypes.Handler
import su.msk.dunno.scage.support.ScageProperties._

object Physics extends Handler {
  val dt = intProperty("dt")
  val gravity = intProperty("gravity")
  val friction = floatProperty("friction")
  val world = new World(new Vector2f(0.0f, gravity), 10, new QuadSpaceStrategy(20,5));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)

  def addBody(b:Body) = if(!world.getBodies.contains(b)) {
    //b.setFriction(friction)
    world.add(b)
  }

  override def actionSequence() = if(!Scage.on_pause)for(i <- 1 to dt)world.step()
}