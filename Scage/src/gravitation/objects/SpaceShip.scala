package gravitation.objects

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import gravitation.{Universe, Gravitation}
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.messages.Message
import org.lwjgl.opengl.GL11

class SpaceShip(init_coord:Vec, init_velocity:Vec)
extends Planet(init_coord, init_velocity, 1, 1) with Gravitation {
  color = RED
  private var rotation = 0.0f

  private  var direction = Vec(0, 1)
  Controller.addKeyListener(Keyboard.KEY_LEFT, 100, () => {
    rotation += 5
    direction = direction.rotateDeg(5)
  })
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 100, () => {
    rotation -= 5
    direction = direction.rotateDeg(-5)
  })

  private var acceleration = 0
  Controller.addKeyListener(Keyboard.KEY_UP, () => if(acceleration < 5) acceleration += 1)
  Controller.addKeyListener(Keyboard.KEY_DOWN, () => if(acceleration > 0) acceleration -= 1)
  Renderer.addInterfaceElement(() => Message.print("acceleration: "+acceleration, 10, height-20, YELLOW))

  override def ai() = {
    if(!consumed) {
      velocity += (gravitationAcceleration(point = this) + direction*acceleration)*Universe.dt
      coord = StandardTracer.getNewCoord(coord + velocity*Universe.dt)
    }
  }

  private val SHIP = Renderer.createList("img/plane2.png", 3, 3, 0, 0, 122, 121)
  override def render() = {
    if(!consumed) {
      Renderer.setColor(WHITE)
      GL11.glPushMatrix();
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)        
        GL11.glCallList(SHIP)
      GL11.glPopMatrix()
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