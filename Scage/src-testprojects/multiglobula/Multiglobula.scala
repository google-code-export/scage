package multiglobula

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.{ScageColors, Vec}
import su.msk.dunno.scage.support.ScageProperties._
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.prototypes.{ScageAction, ScageRender}
import su.msk.dunno.screens.support.tracer.{State, Trace, Tracer}

trait NodeTrace extends Trace {
  def getVelocity:Vec
  def getRadius:Int
  def getMass:Float
}

object ProteinTracer extends Tracer[NodeTrace] {
  def randomNonCollisionCoord(dist:Int):Option[Vec] = {
    var coord = Vec(field_from_x + math.random.toFloat*field_width,
                    field_from_y + math.random.toFloat*field_height)
    var count = 10
    while(hasCollisions(-1, coord, -1 to 1, dist, (n) => true) && count > 0) {
      coord = Vec(field_from_x + math.random.toFloat*field_width,
                  field_from_y + math.random.toFloat*field_height)
      count -= 1
    }
    if(count <= 0) {
      log.warn("warning: cannot locate random noncollision coord within 10 tries")
      None
    }
    Some(coord)
  }

  def reflectFromEdge(coord:Int) = {

  }
}

class Node(screen:ScageScreen, val coord:Vec, val velocity:Vec, val radius:Int = 5, val mass:Float = 1) {
  private var external_force:Vec = Vec(0,0)
  private val new_velocity = Vec(0,0)

  val trace = ProteinTracer.addTrace(new NodeTrace {
    def getCoord = coord
    def getVelocity = velocity
    def getRadius = radius
    def getMass = mass
    def getState = new State
    def changeState(state:State) = {}
  })

  def calculateVelocity = {
    // elastic collisions
    ProteinTracer.neighbours(trace, coord, -1 to 1, (n) => true).foreach(neighbour => {
     if((coord dist neighbour.getCoord) < radius + neighbour.getRadius) {
        val m1 = mass
        val m2 = neighbour.getMass

        val v1 = velocity
        val v2 = neighbour.getVelocity

        val n = (coord - neighbour.getCoord).n

        val v1_x = v1*n
        val v1_y = (v1 - n*v1_x).norma

        val n_ortho = (v1 - n*v1_x).n

        val v2_x = v2*n
        //val v2_y = (v2 - n*v2_x).norma

        val u1_x = (v1_x - v2_x)*(m1 - m2)/(m1 + m2) + v2_x
        //val u2_x = (v1_x - v2_x)*2*m1/(m1 + m2) + v2_x

        new_velocity is n*u1_x + n_ortho*v1_y
      }
    })
  }

  def updateVelocity = {
    if(new_velocity.notZero) {
      velocity is new_velocity
      new_velocity is (0,0)
    }
  }

  def updateCoordinate = {
    ProteinTracer.updateLocation(trace, coord, (coord + velocity * Multiglobula.dt))
  }
}

object Multiglobula extends ScageScreen (
  screen_name = "Multiglobula",
  is_main_screen = true,
  properties = "multiglobula-properties.txt") {

  val dt = property("dt", 0.05f)

  Renderer.backgroundColor = ScageColors.WHITE

  val bodies_radius = property("bodies.radius", 5)
  val nodes = (1 to property("bodies.num", 50)).foldLeft(List[Node]())((nodes, i) => {
    ProteinTracer.randomNonCollisionCoord(bodies_radius*2) match {
      case Some(random_coord) => new Node(
                                      screen = this,
                                      coord = random_coord,
                                      velocity = Vec(-5 + math.random.toFloat*10, -5 + math.random.toFloat*10),
                                      radius = bodies_radius) :: nodes
      case None => nodes
    }
  })

  /*val nodes = List(
    new Node(this, coord = Vec(100,100), velocity = Vec(5, 0)),
    new Node(this, coord = Vec(200,105), velocity = Vec(0, 0))
  )*/

  addAction(new ScageAction {
    override def action = {
      nodes.foreach(_.calculateVelocity)
      nodes.foreach(_.updateVelocity)
      nodes.foreach(_.updateCoordinate)
    }
  })

  addRender(new ScageRender{
    override def interface = {
      val total_impulse = nodes.foldLeft(Vec(0,0))((total, node) => total + node.velocity*node.mass)
      ScageMessage.print("Total Impulse: "+total_impulse, 20, Renderer.height-20, ScageColors.BLACK)

      val total_energy = nodes.foldLeft(0.0f)((total, node) => total + node.velocity*node.velocity*node.mass/2)
      ScageMessage.print("Total Energy: "+total_energy, 20, Renderer.height-40, ScageColors.BLACK)
    }

    override def render = {
      nodes.foreach(node => {
        Renderer.color = ScageColors.BLACK
        Renderer.drawCircle(node.coord, node.radius)
      })
    }
  })

  def main(args:Array[String]):Unit = run
}