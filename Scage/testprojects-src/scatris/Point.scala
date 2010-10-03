package scatris

import su.msk.dunno.scage.support.tracer.{StandardTracer, Trace, State}
import su.msk.dunno.scage.handlers.{Renderer}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

class Point(init_coord:Vec, private val figure:Figure) extends ScageLibrary {
  val coord = init_coord
  private var is_active = true
  def isActive = is_active

  val trace = StandardTracer.addTrace(new Trace[State] {
    override def isActive = is_active
    def getCoord = coord
    def getState() = new State("figure", figure.name).put("isActive", is_active)
    def changeState(s:State) = if(s.contains("disable")) is_active = false
  })

  def move(excluded_traces:List[Int], dir:Vec) = (trace in coord) --> (coord + dir, -1 to 1, dir.norma, excluded_traces)
  def canMove(excluded_traces:List[Int], dir:Vec) = !((trace in (coord + dir)) ? (-1 to 1, dir.norma, excluded_traces))

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