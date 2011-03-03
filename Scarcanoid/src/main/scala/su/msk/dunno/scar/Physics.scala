package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageProperties._
import _root_.net.phys2d.raw.{Body, World}
import net.phys2d.math.Vector2f
import _root_.net.phys2d.raw.strategies.QuadSpaceStrategy
import su.msk.dunno.scage.screens.prototypes.{ScageAction}
import su.msk.dunno.scage.single.support.Vec

object Physics {
  val dt = property("dt", 5)
  val gravity = property("gravity", -5)
  val friction = property("friction", 0.0f)
  val world = new World(new Vector2f(0.0f, gravity), 10, new QuadSpaceStrategy(20,5));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)

  def addBody(body:Body) = {
    if(!world.getBodies.contains(body)) {
      body.setRestitution(1.0f)
      body.setFriction(friction)
      world.add(body)
    }
  }

  Scaranoid.addAction(new ScageAction {
    override def action = {
      for(i <- 1 to dt) {
        print("*")
        world.step()
      }
    }
  })
}