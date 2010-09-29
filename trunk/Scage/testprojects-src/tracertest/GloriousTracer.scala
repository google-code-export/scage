package tracertest

import su.msk.dunno.scage.support.{Vec, ScageLibrary}
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.support.messages.Message

object GloriousTracer extends Application with ScageLibrary {
  var coord = Vec(150,150)
  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 3)
  })
  Renderer.addInterfaceElement(() => Message.print(StandardTracer.point(coord), 20, height-30))

  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => {
    coord = coord -> (coord + Vec(0, 1))
  })
  Controller.addKeyListener(Keyboard.KEY_DOWN, 10, () => {
    coord = coord -> (coord - Vec(0, 1))
  })
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => {
    coord = coord -> (coord + Vec(1, 0))
  })
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => {
    coord = coord -> (coord - Vec(1, 0))
  })  

  StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "ball")
    def changeState(s:State) = {}
  })

  start
}

