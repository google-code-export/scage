package planeflight.objects

import su.msk.dunno.scage.handlers.tracer.{State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.{Color, Vec}
import org.lwjgl.opengl.GL11

class Bullet(init_coord:Vec, dir:Vec) {
  val max_distance2 = 400*400
  val velocity = 10
  val direction = dir.n

  var coord = init_coord
  
  AI.registerAI(() => {
    if(coord.dist2(init_coord) < max_distance2) {
      coord += direction*velocity
      StandardTracer.getNeighbours(coord, -1 to 1).foreach(plane => {
        if(coord.dist2(plane.getCoord) < 25) plane.changeState(new State("damage", 10))
      })
    }
  })

  Renderer.addRender(() => {
    if(coord.dist2(init_coord) < max_distance2) {
      Renderer.setColor(Color.WHITE)
      Renderer.drawCircle(coord, 2)
    }
  })
}