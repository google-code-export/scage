package planeflight.objects

import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.support.{Vec}
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.handlers.{AI, Renderer}
class OurPlane(init_coord:Vec) extends EnemyPlane(init_coord:Vec) {
  def this(x:Float, y:Float) = this(Vec(x, y))

  // ai
  override protected def ai() = {
    AI.registerAI(() => {
      if(health > 0) coord = StandardTracer.getNewCoord(coord + step)
      if(delta > 5) delta -= 0.1f
    })
  }

  // controls
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => rotation -= 0.2f*delta)
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => rotation += 0.2f*delta)
  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => if(delta < 15)delta += 0.5f)
  Controller.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => Renderer.scale -= (if(Renderer.scale <= 1.0f)0 else 0.1f))
  Controller.addKeyListener(Keyboard.KEY_ADD, 10, () => Renderer.scale += (if(Renderer.scale >= 5.0f)0 else 0.1f))

  // shooting
  private var dir = 1
  Controller.addKeyListener(Keyboard.KEY_SPACE, 1500, () => {
    new Rocket("player", coord + step.n.rotate(Math.Pi/2 * dir)*10, step, rotation);
    dir *= -1
  })

  // interactions
  override protected def tracer() {
    StandardTracer.addTrace(new Trace[State] {
      def getCoord = coord
      def getState() = new State("name", "player")
      def changeState(s:State) = if(s.contains("damage")) health -= s.getInt("damage")
    })
  }

  // render
  override protected val PLANE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
}