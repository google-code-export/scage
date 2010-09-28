package tracertest

import su.msk.dunno.scage.support.{Vec, ScageLibrary}
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}

object GloriousTracer extends Application with ScageLibrary {
  var coord = Vec(150,150)
  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 3)
  })

  Controller.addKeyListener(Keyboard.KEY_UP, 100, () => {
    coord = coord -> (coord + Vec(0, 10))
  })
  Controller.addKeyListener(Keyboard.KEY_DOWN, 100, () => {
    coord = coord -> (coord - Vec(0, 10))
  })
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 100, () => {
    coord -> (coord + Vec(10, 0))
    coord = coord + Vec(0, 10)
  })
  Controller.addKeyListener(Keyboard.KEY_LEFT, 100, () => {
    coord -> (coord - Vec(10, 0))
  })  

  val trace_id = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "ball")
    def changeState(s:State) = {}
  })
  println(trace_id)

  start
}

