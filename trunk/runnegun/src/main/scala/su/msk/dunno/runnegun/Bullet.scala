package su.msk.dunno.runnegun

import su.msk.dunno.runnegun.Runnegun._
import net.scage.support.Vec
import net.scage.support.State

class Bullet(start:Vec, val direction:Vec, val speed:Int = 2, shooter_type:String) {
  val trace = tracer.addTrace(start, new GameObject {
    private var _state = new State("bullet" -> shooter_type)
    def state = _state
    def changeState(changer:GameObject, s:State) {}
  })

  val step = direction.n*speed
  var steps = 200
  action {
    if(steps > 0) {
      tracer.moveTrace(trace, step)
      val targets = tracer.tracesNearCoord(trace.location, -1 to 1, other_trace => {
        shooter_type match {
          case "enemy" =>
            other_trace.state.contains("player") &&
            other_trace.location.dist(trace.location) < bullet_radius + player_radius
          case "player" =>
            other_trace.state.contains("enemy") &&
              other_trace.location.dist(trace.location) < bullet_radius + enemy_radius
          case _ => false
        }
      })
      if(targets.size > 0) {
        targets.foreach(_.changeState(trace, new State("hit")))
        steps = 0
      }
      steps -= 1
    } else {
      tracer.removeTraces(trace)
      deleteSelf()
    }
  }

  render {
    if(steps > 0) {
      shooter_type match {
        case "player" => drawFilledCircle(trace.location, bullet_radius, BLUE)
        case "enemy" => drawFilledCircle(trace.location, bullet_radius, RED)
        case _ => drawCircle(trace.location, bullet_radius, BLACK)
      }
    } else deleteSelf()
  }
}