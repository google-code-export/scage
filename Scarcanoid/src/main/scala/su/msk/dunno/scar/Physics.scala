package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageProperties._
import _root_.net.phys2d.raw.{Body, World}
import net.phys2d.math.Vector2f
import _root_.net.phys2d.raw.strategies.QuadSpaceStrategy
import su.msk.dunno.scage.screens.prototypes.{ScageAction}
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.ScageScreen

object Physics {
  val dt = property("dt", 5)
  val world = new World(new Vector2f(0.0f, 0), 10, new QuadSpaceStrategy(20,10));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)

  private var physicals:List[Physical] = Nil
  def addBody(physical:Physical) = {
    if(!world.getBodies.contains(physical.body)) {
      world.add(physical.body)
      physicals = physical :: physicals
    }
  }

  def removeBody(body:Body) = world.remove(body)

  Scaranoid./*addAction(new ScageAction {
    override def */init/* =*/ {
      physicals.foreach(_.isTouching = false)
      physicals.foreach(_.isActive = true)
    }

    /*override def */Scaranoid.action/* =*/ {
      if(!Scaranoid.onPause) {
        physicals.foreach(_.isTouching = false)
        for(i <- 1 to dt) {
          world.step()
          physicals.foreach(p => p.isTouching = p.isTouching || p.body.getTouching.size > 0)
        }
      }
    }
  /*})*/
}