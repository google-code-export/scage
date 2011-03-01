package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageProperties._
import _root_.net.phys2d.raw.{Body, World}
import net.phys2d.math.Vector2f
import _root_.net.phys2d.raw.strategies.QuadSpaceStrategy
import su.msk.dunno.scage.screens.prototypes.{ScageAction}

object Physics {
  val dt = property("dt", 5)
  val gravity = property("gravity", -5)
  val friction = property("friction", 0.0f)
  val world = new World(new Vector2f(0.0f, gravity), 10, new QuadSpaceStrategy(20,5));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)
  world.setDamping(10)

  def addBody(b:Body) = if(!world.getBodies.contains(b)) {
    //b.setFriction(friction)
    world.add(b)
  }

  Scaranoid.addAction(new ScageAction {
    override def action = {
      for(i <- 1 to dt) world.step()
    }
  })
}