package scatris

import figures.Figure
import su.msk.dunno.scage.support.tracer.{StandardTracer, Trace, State}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.{Renderer, AI}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

class Point(init_coord:Vec) extends ScageLibrary {
  private var coord = init_coord
  private var is_active = true

  private val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "point")
    def changeState(s:State) = if(s.contains("disable")) is_active = false
  })

  private val down = Vec(0, -StandardTracer.h_y)
  private var is_moving = true
  def isMoving = is_moving
  AI.registerAI(() => {
    if(is_active) {
      if(!((coord in trace) --> (coord + down, -1 to 1, StandardTracer.h_y*0.9f)))
        is_moving = false
    }
  })

  private val left = Vec(-StandardTracer.h_x, 0)
  Controller.addKeyListener(Keyboard.KEY_LEFT, 1000, () => {
    if(is_active) (coord in trace) --> (coord + left, -1 to 1, StandardTracer.h_x)
  })

  private val right = Vec(StandardTracer.h_x, 0)
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 1000, () => {
    if(is_active) (coord in trace) --> (coord + right, -1 to 1, StandardTracer.h_x)
  })

  private val BOX = Renderer.createList("img/Crate.png", StandardTracer.h_x, StandardTracer.h_y, 0, 0, 256, 256)
  Renderer.addRender(() => {
    if(is_active) {
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      Renderer.setColor(WHITE)
      GL11.glCallList(BOX)
      GL11.glPopMatrix()
    }
  })
}