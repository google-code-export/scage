package planeflight.objects

import su.msk.dunno.scage.handlers.tracer.{State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import org.lwjgl.opengl.GL11
import planeflight.PlaneFlight
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.support.{Colors, Vec}

class Rocket(private val shooter:String, init_coord:Vec, dir:Vec, private val rotation:Float) extends Colors {
  private var fuel = 60
  private val velocity = 10
  private val direction = dir.n

  private var coord = init_coord

  AI.registerAI(() => {
    if(fuel > 5) {
      coord = StandardTracer.checkEdges(coord + direction*velocity)
      StandardTracer.getNeighbours(coord, -1 to 1).foreach(plane => {
        if(coord.dist2(plane.getCoord) < 60*60 &&
           !shooter.equals(plane.getState.getString("name")) &&
           plane.getState.getInt("health") > 0) {
              plane.changeState(new State("damage", 10))
              fuel = 5
        }
      })
    }
    if(fuel > 0) fuel -= 1
  })

  private var next_frame:Float = 0
  Renderer.addRender(() => {
    if(fuel > 0) {
      GL11.glPushMatrix();
      Renderer.setColor(WHITE)

      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(0, Renderer.height, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));
      GL11.glRotatef(-rotation, 0.0f, 0.0f, 1.0f)

      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
      if(fuel > 5)GL11.glCallList(PlaneFlight.ROCKET_ANIMATION(next_frame.toInt));
      else GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(0));

      if(!Scage.on_pause) next_frame += 0.1f
      if(next_frame >= 3)next_frame = 0
      GL11.glPopMatrix()
    }
  })
}