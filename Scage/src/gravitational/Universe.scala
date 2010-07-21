package gravitational

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{AI, Renderer}
import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.{ScageLibrary, Colors, Vec}
import org.lwjgl.opengl.GL11

object Universe extends ScageLibrary {
  val G = Scage.getFloatProperty("G")
  val dt = Scage.getFloatProperty("dt")
  val mass = Scage.getFloatProperty("mass")
  val num_bodies = Scage.getIntProperty("num_bodies")
  val radius = Scage.getIntProperty("radius")  

  Renderer.setBackground(BLACK)

  var bodies = {
    def getRandomPos(center:Vec) = Vec((Math.random*width + (center.x - width/2)).toFloat, (Math.random*height + (center.y - height/2)).toFloat)
    def getRandomVel = Vec((Math.random*2-1).toFloat, (Math.random*2-1).toFloat).n * (Math.random*50).toFloat

    def addBody(acc:List[MaterialPoint], center:Vec, num:Int):List[MaterialPoint] = {
      if(num > 0) {
        val new_acc = new Body(getRandomPos(center), Vec(0,0), mass, radius) :: acc
        addBody(new_acc, center, num-1)
      }
      else acc
    }
    addBody(List[MaterialPoint](), Vec(game_width/2, game_height/2), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(game_width/4, game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(3*game_width/4, game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(game_width/4, 3*game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(3*game_width/4, 3*game_height/4), num_bodies)
  }

  // gravitational force
  def calculateAcceleration(m_points:List[MaterialPoint], point:MaterialPoint) = {
    m_points.filter(body => !body.consumed && body != point && body.coord.dist(point.coord) > body.radius+point.radius).foldLeft(Vec(0,0))((acceleration, body) => {
        val vec = body.coord - point.coord
        val norma = vec.norma
        acceleration + vec*G*body.mass/(norma*norma*norma)
    })
  }
  def calculateAcceleration(point:MaterialPoint):Vec = calculateAcceleration(bodies, point)
  def calculateStep(m_points:List[MaterialPoint], point:MaterialPoint) = {
    val new_velocity = point.velocity+calculateAcceleration(m_points, point)*dt
    val new_coord = StandardTracer.getNewCoord(point.coord + new_velocity*dt)
    (new_velocity, new_coord)
  }
  def calculateStep(point:MaterialPoint):(Vec, Vec) = calculateStep(bodies, point)

  // controls
  var observation_coord = () => Vec(game_width/2, game_height/2)
  Renderer.setCentral(observation_coord)
  Controller.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => scale -= (if(scale > 0.0f) 0.01f else 0))
  Controller.addKeyListener(Keyboard.KEY_ADD, 10, () => scale += (if(scale >= 5.0f) 0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_T, 10, () => {
    val max_mass = bodies.foldLeft(0.0f)((max_mass, body) => if(!body.consumed && body.mass > max_mass){
      Renderer.setCentral(() => body.coord)
      body.mass
    } else max_mass)
  })
  var show_mass = false
  Controller.addKeyListener(Keyboard.KEY_I, () => show_mass = !show_mass)
  var next_body = 0
  Controller.addKeyListener(Keyboard.KEY_RIGHT, () => {
    val body = bodies(next_body)
    Renderer.setCentral(() => body.coord)
    next_body = if(next_body == bodies.length - 1) 0 else next_body + 1
  })
  Controller.addKeyListener(Keyboard.KEY_LEFT, () => {
    val body = bodies(next_body)
    Renderer.setCentral(() => body.coord)
    next_body = if(next_body == 0) bodies.length - 1 else next_body - 1
  })

  // pause
  val t = new Trajectories()
  Controller.addKeyListener(Keyboard.KEY_SPACE, () => {
    Scage.switchPause
    t.init
  })
  Renderer.addInterfaceElement(() => if(Scage.onPause)Message.print("PAUSE", Renderer.width/2-20, Renderer.height/2+60, WHITE))
  class Trajectories {
    var material_points = initPoints
    var points = List[Vec]()

    def initPoints = bodies.foldLeft(List[MaterialPoint]())((m_points, body) => new MaterialPoint(body.coord, body.velocity, body.mass, body.radius) :: m_points)
    def init = {
      material_points = initPoints
      points = List[Vec]()
    }

    def calculateTrajectories() = {
      material_points.foreach(point => {
        val next_step = calculateStep(material_points, point)
        point.velocity = next_step._1
        point.coord = next_step._2
      })
      points = points ::: material_points.foldLeft(List[Vec]())((new_points, m_point) => m_point.coord :: new_points)
    }

    Renderer.addRender(() => {
      if(onPause) {
        calculateTrajectories()
        Renderer.setColor(GREEN)
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    	    GL11.glBegin(GL11.GL_POINTS);
            points.foreach(point => GL11.glVertex2f(point.x, point.y))
          GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
      }
    })
  }

  // the name is self-explainable
  class MaterialPoint(init_coord:Vec, init_velocity:Vec, val mass:Float, val radius:Int) {
    var coord = init_coord
    var velocity = init_velocity
    var color = randomColor
    var consumed = false
  }
  class Body(init_coord:Vec, init_velocity:Vec, override val mass:Float, override val radius:Int) extends MaterialPoint(init_coord, init_velocity, mass, radius) {
    StandardTracer.addTrace(new Trace[State]() {
      def getCoord = coord
      def getState = new State("velocity", velocity).put("mass", mass).put("consumed", consumed).put("radius", radius)
      def changeState(s:State) = {if(s.contains("consumed")) consumed = true}
    })

    def ai() = {
      if(!consumed) {
        val next_step = calculateStep(this)
        velocity = next_step._1
        coord = next_step._2
      }
    }
    AI.registerAI(ai)

    Renderer.addRender(() => {
      if(!consumed) {
        Renderer.setColor(color)
        Renderer.drawCircle(coord, 3)
        Renderer.drawLine(coord, coord+velocity)
        if(show_mass) {
          Message.print(mass, coord, color)
          Message.print(coord, coord.x, coord.y-15, color)
          Message.print(velocity, coord.x, coord.y-30, color)
        }
      }
    })
  }

  // consumption (nonelastic collision)
  AI.registerAI(() => {
    bodies = bodies.foldLeft(List[MaterialPoint]())((new_bodies, body) => {
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
        if(new_body._3 > 0) new Body(new_body._1, new_body._2, new_body._3, new_body._4) :: new_bodies
        else body :: new_bodies
      }
    })
  })

  def main(args:Array[String]):Unit = Scage.start
}