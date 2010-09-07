package scagetest.objects

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import org.newdawn.slick.opengl.Texture
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.support.{Vec}
import su.msk.dunno.scage.objects.DynaBall
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.handlers.tracer.{StandardTracer, State, Trace}

class Tr0yka(init_coord:Vec) extends DynaBall(init_coord:Vec, 30) {
	
	// loading main image
  private val TR0YKA = Renderer.createList("img/stay2.png", 48, 60, 0, 0, 160, 200)

  // loading images for animation
  private val ANIMATION:Array[Int] = {
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val next_key = Renderer.createList(texture, 48, 60, 160*(arr.length), 0, 160, 200)
      val new_arr = next_key :: arr
      if(new_arr.length == 6)new_arr
      else nextFrame(next_key :: arr, texture)
    }
    nextFrame(List[Int](), Renderer.getTexture("img/loli.png")).toArray
  }

  // controls
  private var last_key:Int = 0
  //EventManager.addKeyListener(Keyboard.KEY_S,() => {last_key = Keyboard.KEY_S; if(isTouching)addForce(Vec(body.getForce))})
  Controller.addKeyListener(Keyboard.KEY_SPACE,() => {last_key = Keyboard.KEY_SPACE; if(Math.abs(velocity.y) < 0.5f)addForce(Vec(0,3500))})
  Controller.addKeyListener(Keyboard.KEY_UP,() => {last_key = Keyboard.KEY_UP; addForce(Vec(0,3500))})	// for test purposes only!!!!
  Controller.addKeyListener(Keyboard.KEY_LEFT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(-2000,0))
    else if(last_key != Keyboard.KEY_LEFT) addForce(Vec(-1500,0))
    last_key = Keyboard.KEY_LEFT
  })
  Controller.addKeyListener(Keyboard.KEY_RIGHT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(2000,0))
    else if(last_key != Keyboard.KEY_RIGHT) addForce(Vec(1500,0))
    last_key = Keyboard.KEY_RIGHT
  })

  Controller.addKeyListener(Keyboard.KEY_Z, () => {
  StandardTracer.getNeighbours(coord, -1 to 1).foreach(trace => {
      //if("Box".equals(trace.getState.getString("name"))) {
        val state = new State("pull", coord)
        trace.changeState(state)
      //}
    })
  })

  val trace = new Trace[State] {
	  def getCoord = coord()
	  def getState = {
		  val state = new State("name", "Tr0yka")
		  state
	  }
	  def changeState(s:State) = {}
  }
  StandardTracer.addTrace(trace)

  // render function
  private var next_frame:Float = 0
  override protected def render() = {
    GL11.glPushMatrix();
	GL11.glTranslatef(coord.x, coord.y, 0.0f);

    Renderer.setColor(WHITE)
    last_key match {
      case Keyboard.KEY_LEFT => GL11.glScalef(1,1,1)
      case Keyboard.KEY_RIGHT => GL11.glScalef(-1,1,1)
      case Keyboard.KEY_SPACE => {
        val x_dir = Math.signum(velocity * Vec(-1,0))
        GL11.glScalef(if(x_dir == 0)1 else x_dir,1,1)
      }
      case _ => GL11.glScalef(1,1,1)
    }

    val vel = velocity.norma2
    if(isTouching && vel > 10){
      GL11.glCallList(ANIMATION(next_frame.toInt));
      if(!Scage.onPause) {
    	  next_frame += vel/2000
    	  if(next_frame >= 6)next_frame = 0
      }
    }
    else GL11.glCallList(TR0YKA)

    GL11.glPopMatrix()

    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 30)
    Renderer.drawLine(coord, coord+velocity)
  }
}