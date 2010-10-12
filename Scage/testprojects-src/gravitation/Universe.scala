package gravitation

import objects.{SpaceShip, Trajectories, Planet, MaterialPoint}
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{AI, Renderer}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.messages.Message
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageProperties, ScageLibrary, Vec}
import su.msk.dunno.scage.support.ScageLibrary._

object Universe extends Gravitation {
  val G = ScageProperties.floatProperty("G")
  val dt = ScageProperties.floatProperty("dt")
  private val mass = ScageProperties.floatProperty("mass")
  private val num_bodies = ScageProperties.intProperty("num_bodies")

  // background
  private val SPACE = Renderer.createList("img/deep-space.png", 800, 600, 0, 0, 1024, 768)
  Renderer.addRender(() => {
    GL11.glPushMatrix();

    GL11.glLoadIdentity
    Renderer.setColor(WHITE)
    GL11.glTranslatef(width/2, height/2, 0.0f);
    GL11.glCallList(SPACE)

     GL11.glPopMatrix()
  })

  def getRandomPos(center:Vec) = Vec((Math.random*width + (center.x - width/2)).toFloat, (Math.random*height + (center.y - height/2)).toFloat)
  def getRandomVel = Vec((Math.random*2-1).toFloat, (Math.random*2-1).toFloat).n * (Math.random*10).toFloat

  val space_ship = new SpaceShip(Vec(game_width/2-300, game_height/2-200), Vec(0,0))
  var bodies = {
    def addBody(acc:List[MaterialPoint], center:Vec, num:Int):List[MaterialPoint] = {
      if(num > 0) {
        val new_acc = new Planet(getRandomPos(center), Vec(0,0), mass, 1) :: acc
        addBody(new_acc, center, num-1)
      }
      else acc
    }

    /*addBody(List[MaterialPoint](), Vec(game_width/2, game_height/2), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(game_width/4, game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(3*game_width/4, game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(game_width/4, 3*game_height/4), num_bodies) :::
    addBody(List[MaterialPoint](), Vec(3*game_width/4, 3*game_height/4), num_bodies)*/
    List[MaterialPoint](new Planet(Vec(game_width/2, game_height/2), Vec(0,0), 100000, 1), space_ship)
  }

  // controls
  private var observation_coord = () => Vec(game_width/2, game_height/2)
  Renderer.setCentral(observation_coord)
  Controller.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => scale -= (if(scale > 0.0f) 0.1f else 0))
  Controller.addKeyListener(Keyboard.KEY_ADD, 10, () => scale += (if(scale >= 30.0f) 0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_T, 10, () => {
    val max_mass = bodies.foldLeft(0.0f)((max_mass, body) => if(!body.consumed && body.mass > max_mass){
      Renderer.setCentral(() => body.coord)
      body.mass
    } else max_mass)
  })
  Controller.addKeyListener(Keyboard.KEY_S, 10, () => {
    Renderer.setCentral(() => space_ship.coord)
  })
  var show_mass = false
  Controller.addKeyListener(Keyboard.KEY_I, () => show_mass = !show_mass)
 /* private var next_body = 0
  Controller.addKeyListener(Keyboard.KEY_RIGHT, () => {
    val body = bodies(next_body)
    Renderer.setCentral(() => body.coord)
    next_body = if(next_body == bodies.length - 1) 0 else next_body + 1
  })
  Controller.addKeyListener(Keyboard.KEY_LEFT, () => {
    val body = bodies(next_body)
    Renderer.setCentral(() => body.coord)
    next_body = if(next_body == 0) bodies.length - 1 else next_body - 1
  })*/
  Controller.addKeyListener(Keyboard.KEY_P, () => {
    bodies = new Planet(getRandomPos(Vec(game_width/2, game_height/2)), getRandomVel, mass, 1) :: bodies
  })

  // pause
  private val t = new Trajectories()
  Controller.addKeyListener(Keyboard.KEY_SPACE, () => {
    Scage.switchPause
    t.init
  })
  Renderer.addInterfaceElement(() => if(onPause)Message.print("PAUSE", width/2-20, height/2+60, WHITE))

  // consumption (nonelastic collision)
  AI.registerAI(() => bodies = calculateCollisions(bodies))

  def main(args:Array[String]):Unit = Scage.start
}