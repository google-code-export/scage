package scatris

import su.msk.dunno.scage.support.tracer.{StandardTracer, Trace, State}
import su.msk.dunno.scage.handlers.Renderer
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.ScageLibrary._

class Point(init_coord:Vec, private val figure:Figure) {
  var coord = init_coord
  private var is_active = true
  def isActive = is_active

  val trace_id = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("figure", figure.name).put("isActive", is_active)
                                                     .put("isMoving", !figure.was_landed)
    def changeState(s:State) = {
      if(s.contains("disable")) is_active = false
      else if(s.contains("update")) {
        figure.was_landed = false
      }
    }
  })

  def move(condition:(StateTrace) => Boolean, dir:Vec) = (trace_id in coord) --> (coord + dir, -1 to 1, dir.norma, condition)

  def canMove(condition:(StateTrace) => Boolean, dir:Vec) =
    is_active && !((trace_id in (coord + dir)) ? (-1 to 1, dir.norma, condition))

  private val BOX = Renderer.createList("img/Crate.png", StandardTracer.h_x, StandardTracer.h_y, 0, 0, 256, 256)
  render {
    if(is_active) {
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      Renderer.setColor(WHITE)
      GL11.glCallList(BOX)
      GL11.glPopMatrix()
    }
  }
}
