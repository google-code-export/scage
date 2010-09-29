package tracertest

import su.msk.dunno.scage.support.{Vec, ScageLibrary}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.handlers.{AI, Renderer}

object GloriousTracer extends Application with ScageLibrary {
  var coord = Vec(150,150)
  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 5)
  })
  Renderer.addInterfaceElement(() => Message.print(point(coord), 20, height-30))
  Renderer.addInterfaceElement(() => Message.print("fps: "+fps, 20, height-45))

  val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "player")
    def changeState(s:State) = {}
  })

  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => (coord in trace) --> (coord + Vec(0, 1), -1 to 1, 11))
  Controller.addKeyListener(Keyboard.KEY_DOWN, 10, () => (coord in trace) --> (coord - Vec(0, 1), -1 to 1, 11))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => (coord in trace) --> (coord + Vec(1, 0), -1 to 1, 11))
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => (coord in trace) --> (coord - Vec(1, 0), -1 to 1, 11))

  for(i <- 1 to 20) new Stranger

  start
}

class Stranger extends ScageLibrary {
  private var coord = Vec((100 + math.random*200).toFloat, (100 + math.random*200).toFloat)

  val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "stranger")
    def changeState(s:State) = {}
  })

  AI.registerAI(() => {
    if((coord in trace) ? (-1 to 1, 10)) {
      coord = Vec((100 + math.random*200).toFloat, (100 + math.random*200).toFloat)
    }
  })

  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 5)
  })
}