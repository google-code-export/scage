package planeflight.objects

import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.support.Vec
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.handlers.{AI, Renderer}
import planeflight.PlaneFlight

class OurPlane(init_coord:Vec) extends EnemyPlane(init_coord:Vec) {
  def this(x:Float, y:Float) = this(Vec(x, y))

  // ai
  override protected def ai() = {
    AI.registerAI(() => {
      if(alive_condition) {
        coord = StandardTracer.checkEdges(coord + step)
        if(delta > 5) delta -= 0.1f
      }
    })
  }

  // controls
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => if(alive_condition) {rotation -= 0.2f*delta})
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => if(alive_condition) {rotation += 0.2f*delta})
  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => if(alive_condition) {if(delta < 15)delta += 0.5f})

  // scaling
  Controller.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => if(alive_condition) {
    Renderer.scale -= (if(Renderer.scale <= 1.0f)0 else 0.1f)
  })
  Controller.addKeyListener(Keyboard.KEY_ADD, 10, () => if(alive_condition) {
    Renderer.scale += (if(Renderer.scale >= 5.0f)0 else 0.1f)
  })

  // shooting
  Controller.addKeyListener(Keyboard.KEY_SPACE, 1500, () => {
    if(alive_condition) {
      new Rocket("player", coord + step.n.rotate(Math.Pi/2 * plane_side)*10, step, rotation);
      plane_side *= -1
    }
  })

  // interactions
  override protected def tracer() {
    StandardTracer.addTrace(new Trace[State] {
      def getCoord = coord
      def getState() = new State("name", "player").put("health", health)
      def changeState(s:State) = if(s.contains("damage")) health -= s.getInt("damage")
    })
  }

  // render
  override protected val PLANE = PlaneFlight.PLAYER_PLANE
}