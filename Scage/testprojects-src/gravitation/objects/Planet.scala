package gravitation.objects

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.{AI, Renderer}
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.support.messages.Message
import gravitation.{Universe, Gravitation}
import org.lwjgl.opengl.GL11

class Planet(init_coord:Vec, init_velocity:Vec, override val mass:Float, override val radius:Int)
extends MaterialPoint(init_coord, init_velocity, mass, radius) with Gravitation {
    StandardTracer.addTrace(new Trace[State]() {
      def getCoord = coord
      def getState = new State("velocity", velocity).put("mass", mass).put("consumed", consumed).put("radius", radius)
      def changeState(s:State) = {if(s.contains("consumed")) consumed = true}
    })

    def ai() = {
      if(!consumed) {
        val next_step = calculateStep(point = this)
        velocity = next_step._1
        coord = next_step._2
      }
    }
    AI.registerAI(ai)

  private val PLANET = Renderer.createList("img/planet1.png", 10, 10, 0, 0, 350, 350)
  def render() = {
    if(!consumed) {
      GL11.glPushMatrix();
        Renderer.setColor(WHITE)
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        GL11.glCallList(PLANET)
      GL11.glPopMatrix()
      Renderer.drawLine(coord, coord+velocity)
      if(Universe.show_mass) {
        Message.print(mass, coord, color)
        Message.print(coord, coord.x, coord.y-15, color)
        Message.print(velocity, coord.x, coord.y-30, color)
      }
    }
  }
  Renderer.addRender(render)
}