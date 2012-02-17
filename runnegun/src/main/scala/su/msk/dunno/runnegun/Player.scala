package su.msk.dunno.runnegun

import su.msk.dunno.runnegun.Runnegun._
import net.scage.support.{State, Vec}

object Player {
  val trace = new GameObject {
    var _state = new State("player")
    def state = _state
    def changeState(changer:GameObject, s:State) {
      s.neededKeys {
        case ("hit", true) => is_alive = false
      }
    }
  }

  def move(delta:Vec) {
    tracer.moveTrace(trace, delta)
  }
  key(KEY_W, 10, onKeyDown = {if(!onPause) move(Vec(0,1))})
  key(KEY_A, 10, onKeyDown = {if(!onPause) move(Vec(-1,0))})
  key(KEY_S, 10, onKeyDown = {if(!onPause) move(Vec(0,-1))})
  key(KEY_D, 10, onKeyDown = {if(!onPause) move(Vec(1,0))})

  def shootPoint = (mouseCoord - trace.location).n*(player_radius+5) + trace.location
  leftMouse(30, onBtnDown = {
    mouse_coord => new Bullet(shootPoint, mouse_coord - shootPoint, shooter_type = "player")
  })

  private var is_alive = true
  def isAlive = is_alive

  init {
    is_alive = true
    tracer.addTrace(Vec(window_width/2, window_height/2), trace)
    val render_id = render {
      currentColor = BLACK
      drawLine(trace.location, shootPoint)
      drawCircle(trace.location, player_radius)
    }

    clear {
      delOperations(render_id)
      tracer.removeTraces(trace)
      deleteSelf()
    }
  }
}