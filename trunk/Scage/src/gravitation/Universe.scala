package gravitational

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.handlers.{Idler, AI, Renderer}
import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard

object Universe {
  val G = Scage.getFloatProperty("G")
  val dt = Scage.getFloatProperty("dt")
  val mass = Scage.getFloatProperty("mass")
  val num_bodies = Scage.getIntProperty("num_bodies")
  val radius = Scage.getIntProperty("radius")

  Renderer.setBackground(Color.BLACK)

  var bodies = {
    def getRandomPos = Vec((Math.random*Renderer.width).toFloat, (Math.random*Renderer.height).toFloat)
    def getRandomVel = Vec((Math.random*2-1).toFloat, (Math.random*2-1).toFloat).n * (Math.random*50).toFloat

    def addBody(acc:List[MaterialPoint], num:Int):List[MaterialPoint] = {
      if(num > 0) {
        val new_acc = new MaterialPoint(getRandomPos, Vec(0,0), mass, radius) :: acc
        addBody(new_acc, num-1)
      }
      else acc
    }
    addBody(List[MaterialPoint](), num_bodies)
  }

  def calculateAcceleration(point:MaterialPoint) = {
    bodies.filter(body => !body.consumed && body != point && body.coord.dist(point.coord) > 20).foldLeft(Vec(0,0))((acceleration, body) => {
        val vec = body.coord - point.coord
        val norma = vec.norma
        acceleration + vec*G*body.mass/(norma*norma*norma)
    })
  }

  // observation
  var observation_coord = Vec(Renderer.width/2, Renderer.height/2)
  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => observation_coord += Vec(0, 1))
  Controller.addKeyListener(Keyboard.KEY_DOWN, 10, () => observation_coord += Vec(0, -1))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => observation_coord += Vec(1, 0))
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => observation_coord += Vec(-1, 0))
  Controller.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => Renderer.scale -= (if(Renderer.scale <= 1.0f)0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_ADD, 10, () => Renderer.scale += (if(Renderer.scale >= 5.0f)0 else 0.1f))
  Renderer.setCentral(() => if(Renderer.scale == 1)Vec(Renderer.width/2, Renderer.height/2) else observation_coord)

  class MaterialPoint(init_coord:Vec, init_vel:Vec, val mass:Float, val radius:Int) {
    var coord = init_coord
    var velocity = init_vel
    val color = Color.getRandomColor
    var consumed = false

    StandardTracer.addTrace(new Trace[State]() {
      def getCoord = coord
      def getState = new State("velocity", velocity).put("mass", mass).put("consumed", consumed).put("velocity", velocity).put("radius", radius)
      def changeState(s:State) = {if(s.contains("consumed")) consumed = true}
    })

    AI.registerAI(() => {
      if(!consumed) {
        velocity += calculateAcceleration(this)*dt
        coord = StandardTracer.getNewCoord(coord + velocity*dt)
      }
    })

    Renderer.addRender(() => {
      if(!consumed) {
        Renderer.setColor(color)
        Renderer.drawCircle(coord, 3)
        Renderer.drawLine(coord, coord+velocity)
      }
    })
  }

  AI.registerAI(() => {
    val new_bodies = bodies.foldLeft(List[MaterialPoint]())((new_bodies, body) => {
      if(body.consumed)new_bodies
      else {
        val new_body = StandardTracer.getNeighbours(body.coord, -1 to 1).foldLeft((Vec(0,0), Vec(0,0), 0.0f, 0))((new_body, neighbour) => {
          if(!neighbour.getState.getBool("consumed") && body.coord.dist(neighbour.getCoord) < body.radius + neighbour.getState.getInt("radius")) {
            neighbour.changeState(new State("consumed"))
            val new_coord = body.coord
            val new_velocity = new_body._2 + (if(!body.consumed) body.velocity*body.mass else Vec(0,0)) + neighbour.getState.getVec("velocity")*neighbour.getState.getFloat("mass")
            val new_mass = new_body._3 + (if(!body.consumed) body.mass else 0) + neighbour.getState.getFloat("mass")
            val new_radius = radius
            body.consumed = true
            (new_coord, new_velocity/new_mass, new_mass, new_radius)
          }
          else new_body
        })
        if(new_body._3 > 0) new MaterialPoint(new_body._1, new_body._2, new_body._3, new_body._4) :: new_bodies
        else new_bodies
      }
    })
    bodies = new_bodies ::: bodies
  })

  def main(args:Array[String]):Unit = Scage.start
}