package multiglobula

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.{ScageColors, ScageProperties, Vec}
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.prototypes.{ScageAction, ScageRender}
import su.msk.dunno.screens.support.tracer.{State, Trace, Tracer}

trait NodeTrace extends Trace {
  def getVelocity:Vec
  def getRadius:Int
  def getMass:Float
}

object ProteinTracer extends Tracer[NodeTrace]

class Node(screen:ScageScreen, val coord:Vec, val velocity:Vec, val radius:Int = 5, val mass:Float = 1) {
  private var external_force:Vec = Vec(0,0)

  val new_velocity = velocity.copy

  val trace = ProteinTracer.addTrace(new NodeTrace {
    def getCoord = coord
    def getVelocity = velocity
    def getRadius = radius
    def getMass = mass
    def getState = new State
    def changeState(state:State) = {}
  })
}

object Multiglobula extends ScageScreen (
  screen_name = "Multiglobula",
  is_main_screen = true,
  properties = "multiglobula-properties.txt") {

  val dt = ScageProperties.property("dt", 0.05f)

  Renderer.backgroundColor = ScageColors.WHITE

  /*val nodes = (1 to 50).foldLeft(List[Node]())((nodes, i) => {
    new Node(
      screen = this,
      coord = Vec((math.random*Renderer.width).toFloat, (math.random*Renderer.height).toFloat),
      velocity = Vec(-5 + math.random.toFloat*10, -5 + math.random.toFloat*10)) :: nodes
  })*/

  val nodes = List(
    new Node(this, coord = Vec(100,100), velocity = Vec(5, 0)),
    new Node(this, coord = Vec(200,105), velocity = Vec(0, 0))
  )

  addAction(new ScageAction {
    override def action = {
      // update velocities
      nodes.foreach(node => {
        ProteinTracer.neighbours(node.trace, node.coord, -1 to 1, (n) => true).foreach(neighbour => {
          if((node.coord dist neighbour.getCoord) < node.radius + neighbour.getRadius) {  // collision
            val m1 = node.mass
            val m2 = neighbour.getMass

            val v1 = node.velocity
            val v2 = neighbour.getVelocity

            val n = (node.coord - neighbour.getCoord).n

            val v1_x = v1*n
            val v1_y = (v1 - n*v1_x).norma

            val n_ortho = (v1 - n*v1_x).n

            val v2_x = v2*n
            //val v2_y = (v2 - n*v2_x).norma

            val u1_x = (v1_x - v2_x)*(m1 - m2)/(m1 + m2) + v2_x
            //val u2_x = (v1_x - v2_x)*2*m1/(m1 + m2) + v2_x

            node.new_velocity is n*u1_x + n_ortho*v1_y
          }
        })
      })

      nodes.foreach(node => node.velocity is node.new_velocity)

      // update coords
      nodes.foreach(node => ProteinTracer.updateLocation(node.trace, node.coord, (node.coord + node.velocity * dt)))
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