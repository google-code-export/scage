package gravitation

import objects.{Planet, MaterialPoint}
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.tracer.{State, StandardTracer}

trait Gravitation {
  // gravitation force
  def gravitationAcceleration(m_points:List[MaterialPoint] = Universe.bodies, point:MaterialPoint) = {
    m_points.filter(body => !body.consumed && body != point && body.coord.dist(point.coord) > body.radius+point.radius).foldLeft(Vec(0,0))((acceleration, body) => {
        val vec = body.coord - point.coord
        val norma = vec.norma
        acceleration + vec*Universe.G*body.mass/(norma*norma*norma)
    })
  }
  def calculateStep(m_points:List[MaterialPoint] = Universe.bodies, point:MaterialPoint) = {
    val new_velocity = point.velocity + gravitationAcceleration(m_points, point)*Universe.dt
    val new_coord = StandardTracer.getNewCoord(point.coord + new_velocity*Universe.dt)
    (new_velocity, new_coord)
  }

  // non-elastic collision
  def calculateCollisions(m_points:List[MaterialPoint]):List[MaterialPoint] = {
    m_points.foldLeft(List[MaterialPoint]())((new_bodies, body) => {
      if(body.consumed)new_bodies
      else {
        val new_body = StandardTracer.getNeighbours(body.coord, -1 to 1).foldLeft((Vec(0,0), Vec(0,0), 0.0f, 0))((new_body, neighbour) => {
          if(!neighbour.getState.getBool("consumed") && body.coord.dist(neighbour.getCoord) < body.radius + neighbour.getState.getInt("radius")) {
            neighbour.changeState(new State("consumed"))
            val new_coord = body.coord
            val new_velocity = new_body._2 + (if(!body.consumed) body.velocity*body.mass else Vec(0,0)) + neighbour.getState.getVec("velocity")*neighbour.getState.getFloat("mass")
            val new_mass = new_body._3 + (if(!body.consumed) body.mass else 0) + neighbour.getState.getFloat("mass")
            body.consumed = true
            (new_coord, new_velocity/new_mass, new_mass, 1)
          }
          else new_body
        })
        if(new_body._3 > 0) new Planet(new_body._1, new_body._2, new_body._3, new_body._4) :: new_bodies
        else body :: new_bodies
      }
    })
  }
}