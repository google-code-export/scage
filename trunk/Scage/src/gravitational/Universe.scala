package gravitational

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.support.{Color, Vec}
import su.msk.dunno.scage.handlers.{Idler, AI, Renderer}
import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}

object Universe {
  val G = Scage.getFloatProperty("G")
  val dt = Scage.getFloatProperty("dt")
  val mass = Scage.getFloatProperty("mass")
  val num_bodies = Scage.getIntProperty("num_bodies")

  def getRandomPos = Vec((Math.random*Renderer.width).toFloat, (Math.random*Renderer.height).toFloat)
  def getRandomVel = Vec(Math.random.toFloat, Math.random.toFloat).n * (Math.random*10).toFloat

  val bodies = {
    def addBody(acc:List[MaterialPoint], num:Int):List[MaterialPoint] = {
      if(num > 0) {
        val new_acc = new MaterialPoint(getRandomPos, getRandomVel, mass, 3) :: acc
        addBody(new_acc, num-1)
      }
      else acc
    }
    addBody(List[MaterialPoint](), num_bodies)
  }

  def calculateAcceleration(point:MaterialPoint) = {
    bodies.filter(body => body != point && body.coord.dist(point.coord) > 20).foldLeft(Vec(0,0))((acc, body) => {
      val vec = body.coord - point.coord
      val norma = vec.norma
      acc + vec*G*mass/(norma*norma*norma)
    })/point.mass
  }

  class MaterialPoint(init_coord:Vec, init_vel:Vec, val mass:Float, val radius:Int) {
    var coord = init_coord
    var velocity = init_vel
    val color = Color.getRandomColor

    AI.registerAI(() => {
      velocity = velocity + calculateAcceleration(this)*dt
      StandardTracer.getNeighbours(coord, -1 to 1).foreach(body => if(coord.dist(body.getCoord) < 10) {
        val new_velocity = velocity + body.getState.getVec("velocity")
        velocity = new_velocity * -Math.signum(velocity*new_velocity)/2
      })
      coord = StandardTracer.getNewCoord(coord + velocity*dt)
    })

    StandardTracer.addTrace(new Trace[State]() {
      def getCoord = coord
      def getState = new State("velocity", velocity).put("mass", mass)
      def changeState(s:State) = {}
    })

    Renderer.addRender(() => {
      Renderer.setColor(color)
      Renderer.drawCircle(coord, radius)
      Renderer.drawLine(coord, coord+velocity)
    })
  }

  def main(args:Array[String]):Unit = Scage.start
}