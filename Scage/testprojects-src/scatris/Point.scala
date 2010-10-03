package scatris

import su.msk.dunno.scage.support.tracer.{StandardTracer, Trace, State}
import su.msk.dunno.scage.handlers.{Renderer}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

class Point(init_coord:Vec, private val figure:Figure) extends ScageLibrary {
  val coord = init_coord
  private var is_active = true

  val trace = StandardTracer.addTrace(new Trace[State] {
    override def isActive = is_active
    def getCoord = coord
    def getState() = new State("figure", figure.name).put("isActive", isActive).put("isMoving", figure.canMoveDown)
    def changeState(s:State) = if(s.contains("disable")) is_active = false
  })

  private val down = Vec(0, -StandardTracer.h_y)
  def canMoveDown(excluded_traces:List[Int]) = !is_active || !((trace in (coord + down)) ? (-1 to 1, StandardTracer.h_y, excluded_traces))
  def moveDown = (trace in coord) --> (coord + down, -1 to 1, StandardTracer.h_y)

  private val left = Vec(-StandardTracer.h_x, 0)
  def moveLeft = (trace in coord) --> (coord + left, -1 to 1, StandardTracer.h_x)

  private val right = Vec(StandardTracer.h_x, 0)
  def moveRight = (trace in coord) --> (coord + right, -1 to 1, StandardTracer.h_x)

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