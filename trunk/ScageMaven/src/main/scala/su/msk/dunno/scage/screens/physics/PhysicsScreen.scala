package su.msk.dunno.scage.screens.physics

import su.msk.dunno.scage.screens.ScageScreen
import net.phys2d.raw.World
import su.msk.dunno.scage.single.support.ScageProperties._
import net.phys2d.math.Vector2f
import net.phys2d.raw.strategies.QuadSpaceStrategy

class PhysicsScreen(screen_name:String, is_main_screen:Boolean = false, properties:String = "")
extends ScageScreen(screen_name, is_main_screen, properties) {
  val dt = property("dt", 5)
  val world = new World(new Vector2f(0.0f, 0), 10, new QuadSpaceStrategy(20,10));
  world.enableRestingBodyDetection(0.01f, 0.000001f, 0.01f)
  
  private var physicals:List[Physical] = Nil
  def --> (physical:Physical) = {
    if(!world.getBodies.contains(physical.body)) world.add(physical.body)
    if(!physicals.contains(physical)) physicals = physical :: physicals
    physical.prepare()

    physical
  }

  render {
    physicals.foreach(p => p.render())
  }

  action {
    if(!onPause) {
      for(p <- physicals) {
        if(!p.isActive) {
          world.remove(p.body)
          physicals = physicals.filterNot(_ == p)
        }
        else p.isTouching = false
      }

      for(i <- 1 to dt) {
        world.step()
        for(p <- physicals) {
          p.isTouching = p.isTouching || p.body.getTouching.size > 0
        }
      }
    }
  }

  /*exit {
    physicals.foreach(p => {
      p.isActive = false
      world.remove(p.body)
    })
    physicals = Nil
  }*/
}