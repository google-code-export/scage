package su.msk.dunno.q

import su.msk.dunno.scage.single.support.Vec
import QueersIsland._
import su.msk.dunno.scage.screens.support.tracer.{Trace, State, CoordTrace}
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.screens.handlers.Renderer._

abstract class Human(protected var gender:String) extends CoordTrace {
  protected var status = ""
  alive_count += 1

  protected var already_met:List[CoordTrace] = Nil
  protected var is_child = true

  protected var is_alive = true

  private var steps = 50
  private var dir = randomDir
  def randomDir = Vec((-1 + math.random*2).toFloat, (-1 + math.random*2).toFloat).n

  def getState = new State("gender", gender).put("alive", is_alive).put("child", is_child)
  def changeState(changer: Trace, state: State) {
    if(state.contains("kill")) {
      status = "DIE..."
      is_alive = false
      alive_count -= 1
      delActionOperation(action_id)
    }
  }

  val action_id = action {
    if(is_alive) {
      if(steps > 0) {
        val old_point = point.copy
          tracer.move(this, dir)
          if(old_point != point) already_met = Nil
          steps -= 1
      }
      else {
        steps = (math.random*100).toInt
        dir = randomDir
        is_child = false
      }
    }
  }

  private val max_count = property("message.time", 50)
  private var count = max_count // TODO: make a property
  render {
    if(status != "") {
      print(status, coord.x, coord.y+30, BLUE)
      count -= 1
      if(count == 0) {
        count = max_count
        status = ""
      }
    }
  }

  val baby_image = image("baby.png", 30, 36, 0, 0, 187, 218)
}