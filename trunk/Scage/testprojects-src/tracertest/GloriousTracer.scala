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

  val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "player")
    def changeState(s:State) = {}
  })

  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => coord -> (coord + Vec(0, 1)) in trace)
  Controller.addKeyListener(Keyboard.KEY_DOWN, 10, () => coord -> (coord - Vec(0, 1)) in trace)
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => coord -> (coord + Vec(1, 0)) in trace)
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => coord -> (coord - Vec(1, 0)) in trace)

  for(i <- 1 to 5) new Stranger

  start
}

class Stranger extends ScageLibrary {
  private var coord = Vec((100 + scala.math.random*200).toFloat, (100 + scala.math.random*200).toFloat)
  println(coord)
  private var dir = Vec(scala.math.random.toFloat, scala.math.random.toFloat).n
  println(dir)
  private var steps = (scala.math.random*50).toInt
  println(steps)
  AI.registerAI(() => {
    if(steps > 0) {
      if(!StandardTracer.hasCollisions(coord + dir, -1 to 1, 5)) coord -> (coord + dir) in trace
      steps -= 1
    }
    else {
      dir = Vec(scala.math.random.toFloat, scala.math.random.toFloat).n
      steps = (scala.math.random*50).toInt
    }
  })

  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 5)
  })

  val trace = StandardTracer.addTrace(new Trace[State] {
    def getCoord = coord
    def getState() = new State("name", "stranger")
    def changeState(s:State) = {}
  })
}