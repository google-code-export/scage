package scatris

import su.msk.dunno.scage.support.tracer.{StandardTracer, Trace, State}
import su.msk.dunno.scage.handlers.{Renderer}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}
import su.msk.dunno.scage.support.ScageLibrary._

class Point(init_coord:Vec, private val figure:Figure) {
  val coord = init_coord
  private var is_active = true
  def isActive = is_active

  val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("figure", figure.name).put("isActive", is_active).put("isMoving", figure.canMoveDown)
    def changeState(s:State) = if(s.contains("disable")) is_active = false
  })

  def move(condition:(Trace[State]) => Boolean, dir:Vec) = (trace in coord) --> (coord + dir, -1 to 1, dir.norma, condition)
  def canMove(condition:(Trace[State]) => Boolean, dir:Vec) = is_active && !((trace in (coord + dir)) ? (-1 to 1, dir.norma, condition))

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