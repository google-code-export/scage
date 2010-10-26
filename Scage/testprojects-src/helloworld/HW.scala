package helloworld

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}

object HW {
  var coord = Vec(width/2, height/2)
  val trace = StandardTracer.addTrace(new Trace[State](){
    def getCoord = coord
    def getState = new State
    def changeState(s:State) = {}
  })

  var coord2 = Vec(width/2+width/4, height/2)
  val trace2 = StandardTracer.addTrace(new Trace[State](){
    def getCoord = coord2
    def getState = new State
    def changeState(s:State) = {}
  })

  center = coord

  keyListener(Keyboard.KEY_UP,    100, onKeyDown = (trace in coord) --> (coord + Vec(0, 10),  -1 to 1, 20))
  keyListener(Keyboard.KEY_DOWN,  100, onKeyDown = (trace in coord) --> (coord + Vec(0, -10), -1 to 1, 20))
  keyListener(Keyboard.KEY_RIGHT, 100, onKeyDown = (trace in coord) --> (coord + Vec(10, 0),  -1 to 1, 20))
  keyListener(Keyboard.KEY_LEFT,  100, onKeyDown = (trace in coord) --> (coord + Vec(-10, 0), -1 to 1, 20))

  keyListener(Keyboard.KEY_ADD,      100, onKeyDown = if(scale < 10) scale += 1)
  keyListener(Keyboard.KEY_SUBTRACT, 100, onKeyDown = if(scale > 1) scale -= 1)

  action {
    if(Controller.isKeyPressed) scale = 2
    else scale = 1
  }

  render {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 10)
    Renderer.drawCircle(coord2, 10)
  }

  def main(args:Array[String]):Unit = start
}