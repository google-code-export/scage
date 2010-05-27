package su.msk.dunno.scage.objects

import su.msk.dunno.scage.handlers.Renderer
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.handlers.eventmanager.EventManager
import org.lwjgl.input.Keyboard
import org.newdawn.slick.opengl.Texture
import su.msk.dunno.scage.support.{Color, Vec}

class Tr0yka(init_coord:Vec) extends DynaBall(init_coord:Vec, 30) {
  private val TR0YKA = Renderer.nextDisplayListKey
  Renderer.createList(TR0YKA, "img/stay2.png", 24, 30, 0, 0, 160, 200)

  private val ANIMATION:Array[Int] = {
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val next_key = Renderer.nextDisplayListKey
      Renderer.createList(next_key, texture, 24, 30, 160*(arr.length), 0, 160, 200)
      val new_arr = next_key :: arr
      if(new_arr.length == 6)new_arr
      else nextFrame(next_key :: arr, texture)
    }
    nextFrame(List[Int](), Renderer.getTexture("img/loli.png")).toArray
  }

  private var last_key:Int = 0
  EventManager.addKeyListener(Keyboard.KEY_S,() => {last_key = Keyboard.KEY_S; if(isTouching)body.setForce(0, 0)})
  EventManager.addKeyListener(Keyboard.KEY_SPACE,() => {last_key = Keyboard.KEY_SPACE; if(isTouching)addForce(Vec(0,3500))})
  EventManager.addKeyListener(Keyboard.KEY_UP,() => {last_key = Keyboard.KEY_UP; if(isTouching)addForce(Vec(0,3500))})
  EventManager.addKeyListener(Keyboard.KEY_LEFT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(-2000,0))
    else if(last_key != Keyboard.KEY_LEFT) addForce(Vec(-1500,0))
    last_key = Keyboard.KEY_LEFT
  })
  EventManager.addKeyListener(Keyboard.KEY_RIGHT,100,() => {
    if(isTouching && velocity.norma2 < 500)addForce(Vec(2000,0))
    else if(last_key != Keyboard.KEY_RIGHT) addForce(Vec(1500,0))
    last_key = Keyboard.KEY_RIGHT
  })

  private var next_frame:Float = 0
  override def render() = {
   /* Renderer.setColor(Color.BLACK)
    Renderer.drawCircle(coord, 15)
    Renderer.drawLine(coord, coord+velocity)*/

    GL11.glPushMatrix();
		GL11.glTranslatef(coord.x, coord.y, 0.0f);

    Renderer.setColor(Color.WHITE)
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
      next_frame += vel/2000
      if(next_frame >= 6)next_frame = 0
    }
    else GL11.glCallList(TR0YKA)

    GL11.glPopMatrix()
  }
}