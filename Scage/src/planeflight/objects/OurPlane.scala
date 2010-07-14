package planeflight.objects

import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.support.{Vec, Color}
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.handlers.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.{AI, Renderer}

class OurPlane(init_coord:Vec) {
  def this(x:Float, y:Float) = this(Vec(x, y))

  var delta = 5.0f
  var rotation = 0.0f
  var coord = init_coord
  def step = Vec(-0.4f*delta*Math.sin(Math.toRadians(rotation)).toFloat,
                 0.4f*delta*Math.cos(Math.toRadians(rotation)).toFloat)

  var bullet_coord:() => Vec = () => Vec(0,0)
  Renderer.addInterfaceElement(() => Message.print(bullet_coord(), 20, Renderer.height-40, Color.YELLOW))

  var health = 100
  Renderer.addInterfaceElement(() => Message.print("HP: "+health, 20, Renderer.height-60, Color.YELLOW))

  Renderer.addInterfaceElement(() => Message.print(StandardTracer.point(coord), 20, Renderer.height-80, Color.YELLOW))

  AI.registerAI(() => {
    if(health > 0) coord = StandardTracer.getNewCoord(coord + step)
    if(delta > 5) delta -= 0.1f
  })

  // controls
  Controller.addKeyListener(Keyboard.KEY_A, 10, () => rotation -= 0.2f*delta)
  Controller.addKeyListener(Keyboard.KEY_D, 10, () => rotation += 0.2f*delta)
  Controller.addKeyListener(Keyboard.KEY_W, 10, () => if(delta < 15)delta += 0.5f)
  Controller.addKeyListener(Keyboard.KEY_1, 10, () => Renderer.scale -= (if(Renderer.scale <= 1.0f)0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_2, 10, () => Renderer.scale += (if(Renderer.scale >= 5.0f)0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_SPACE, 100, () => {
    val b = new Bullet(coord + step, step);
    bullet_coord = () => b.coord
  })

  // interactions
  StandardTracer.addTrace(new Trace[State]{
    def getCoord = coord
    def getState() = new State("name", "player")
    def changeState(s:State) = if(s.contains("damage")) health -= s.getInt("damage")
  })

  // render
  val PLANE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
  Renderer.addRender(() => {
     GL11.glPushMatrix();
     GL11.glTranslatef(coord.x, coord.y, 0.0f);
     GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
     Renderer.setColor(Color.WHITE)
     GL11.glCallList(PLANE)
     GL11.glPopMatrix()
  })
  Renderer.setCentral(() => if(Renderer.scale == 1)Vec(Renderer.width/2, Renderer.height/2) else coord)
}