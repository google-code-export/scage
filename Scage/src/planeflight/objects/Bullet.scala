package planeflight.objects

import su.msk.dunno.scage.handlers.tracer.{State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.{Color, Vec}
import org.lwjgl.opengl.GL11

class Bullet(val shooter:String, init_coord:Vec, dir:Vec, val rotation:Float) {
  var fuel = 60
  val velocity = 10
  val direction = dir.n

  var coord = init_coord
  
  AI.registerAI(() => {
    if(fuel > 0) {
      coord = StandardTracer.getNewCoord(coord + direction*velocity)
      fuel -= 1
      StandardTracer.getNeighbours(coord, -1 to 1).foreach(plane => {
        if(coord.dist2(plane.getCoord) < 60*60 && !shooter.equals(plane.getState.getString("name")))
          plane.changeState(new State("damage", 10))
      })
    }
  })

  val ROCKET = Renderer.createList("img/rocket.png", 10, 29, 0, 0, 15, 44)
  Renderer.addRender(() => {
    if(fuel > 0) {
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      Renderer.setColor(Color.WHITE)
      GL11.glCallList(ROCKET)
      GL11.glPopMatrix()
    }
  })
}