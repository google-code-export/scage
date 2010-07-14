package planeflight.objects

import su.msk.dunno.scage.handlers.tracer.{State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.{Color, Vec}
import org.lwjgl.opengl.GL11
import planeflight.PlaneFlight
import su.msk.dunno.scage.main.Scage

class Bullet(private val shooter:String, init_coord:Vec, dir:Vec, private val rotation:Float) {
  private var fuel = 60
  private val velocity = 10
  private val direction = dir.n

  private var coord = init_coord
  
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

  private var next_frame:Float = 0
  Renderer.addRender(() => {
    if(fuel > 0) {
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      Renderer.setColor(Color.WHITE)
      GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      if(!Scage.onPause) next_frame += 0.1f
      if(next_frame >= 3)next_frame = 0      
      GL11.glPopMatrix()
    }
  })
}