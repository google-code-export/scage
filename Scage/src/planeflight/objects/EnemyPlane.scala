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
  protected def alive_condition = health > 0

  // ai
  protected var plane_side = 1
  protected def ai() = {
    AI.registerAI(() => {
      if(alive_condition) {
        coord = StandardTracer.getNewCoord(coord + step)
        StandardTracer.getNeighbours(coord, -8 to 8).foreach(plane => {
          if(plane.getState.getInt("health") > 0) {
            val planes_angle = (plane.getCoord - coord) rad step
            val planes_side = Math.signum((plane.getCoord - coord) * step)
            println(planes_angle)
            if(planes_angle < Math.Pi/12) {
              new Rocket("enemy", coord + step.n.rotate(Math.Pi/2 * plane_side)*10, step, rotation);
              plane_side *= -1
            }
            else rotation += 0.2f*delta*planes_side
          }
        })
      }
    })
  }; ai

  // interactions
  protected def tracer() {
    StandardTracer.addTrace(new Trace[State] {
      def getCoord = coord
      def getState() = new State("name", "enemy").put("health", health)
      def changeState(s:State) = if(s.contains("damage")) health -= s.getInt("damage")
    })
  }; tracer

  // render
  protected val PLANE = PlaneFlight.ENEMY_PLANE
  private var next_frame:Float = 0
  Renderer.addRender(() => {
     if(alive_condition) {
        GL11.glPushMatrix();
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
        Renderer.setColor(Color.WHITE)
        GL11.glCallList(PLANE)
        GL11.glPopMatrix()
     }
     else if(next_frame < 3) {
        GL11.glPushMatrix();
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
        Renderer.setColor(Color.WHITE)
        GL11.glCallList(PlaneFlight.EXPLOSION_ANIMATION(next_frame.toInt));
        if(!Scage.onPause) next_frame += 0.1f
        GL11.glPopMatrix()
     }
  })
}