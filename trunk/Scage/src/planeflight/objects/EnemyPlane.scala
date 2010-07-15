package planeflight.objects

import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.support.{Color, Vec}
import planeflight.PlaneFlight
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.handlers.{AI, Renderer}

class EnemyPlane(init_coord:Vec) {
  def this(x:Float, y:Float) = this(Vec(x, y))

  // parameters
  protected var delta = 5.0f
  protected var rotation = 0.0f
  var coord = init_coord
  protected def step = Vec(-0.4f*delta*Math.sin(Math.toRadians(rotation)).toFloat,
                           0.4f*delta*Math.cos(Math.toRadians(rotation)).toFloat)
  var health = 100

  // ai
  protected def ai() = {
    AI.registerAI(() => {
      if(health > 0) {
        rotation += 0.2f*delta
        coord = StandardTracer.getNewCoord(coord + step)
      }
    })
  }; ai

  // interactions
  protected def tracer() {
    StandardTracer.addTrace(new Trace[State] {
      def getCoord = coord
      def getState() = new State("name", "enemy")
      def changeState(s:State) = if(s.contains("damage")) health -= s.getInt("damage")
    })
  }; tracer

  // render
  protected val PLANE = Renderer.createList("img/plane2.png", 60, 60, 0, 0, 122, 121)
  private var next_frame:Float = 0
  Renderer.addRender(() => {
     GL11.glPushMatrix();
     GL11.glTranslatef(coord.x, coord.y, 0.0f);
     GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
     Renderer.setColor(Color.WHITE)
     if(health > 0) GL11.glCallList(PLANE)
     else {
        GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(next_frame.toInt));
        if(!Scage.onPause) next_frame += 0.1f
        if(next_frame >= 3)next_frame = 0
     }
     GL11.glPopMatrix()
  })
}