package gravitation.objects

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import gravitation.{Universe, Gravitation}
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.messages.Message
import org.lwjgl.opengl.GL11

class SpaceShip(init_coord:Vec, init_velocity:Vec, override val mass:Float, override val radius:Int)
extends Planet(init_coord, init_velocity, mass, radius) with Gravitation {
  color = RED

  var direction = Vec(0, 1)
  Controller.addKeyListener(Keyboard.KEY_LEFT, 100, () => direction = direction.rotate(-0.1f))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 100, () => direction = direction.rotate(0.1f))

  var acceleration = 0.0f
  Controller.addKeyListener(Keyboard.KEY_UP, () => if(acceleration < 5) acceleration += 0.1f)
  Controller.addKeyListener(Keyboard.KEY_DOWN, () => if(acceleration > 0) acceleration -= 0.1f)
  Renderer.addInterfaceElement(() => Message.print("acceleration: "+acceleration, 10, height-20, YELLOW))

  AI.registerAI(() => {
    if(!consumed) {
      velocity += direction*acceleration*Universe.dt
      coord = StandardTracer.getNewCoord(coord + velocity*Universe.dt)      
    }
  })

  override def render() = {
    if(!consumed) {
      Renderer.setColor(color)
      GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(coord.x-3, coord.y-3)
        GL11.glVertex2f(coord.x-3, coord.y+3)
        GL11.glVertex2f(coord.x+3, coord.y+3)
        GL11.glVertex2f(coord.x+3, coord.y-3)
      GL11.glEnd();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      Renderer.setColor(CYAN)
      Renderer.drawLine(coord, coord+direction*5)
      Renderer.setColor(GREEN)
      Renderer.drawLine(coord, coord+velocity)
      if(Universe.show_mass) {
        Message.print(mass, coord, color)
        Message.print(coord, coord.x, coord.y-15, color)
        Message.print(velocity, coord.x, coord.y-30, color)
      }
    }
  }
}