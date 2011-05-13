package su.msk.dunno.q

import su.msk.dunno.scage.screens.support.tracer.{State, Trace}
import QueersIsland._
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.ScageProperties._

class Male extends Human("male") {
  private val is_queer_tolerate = math.random < property("male.tolerate.queer", 0.7)

  override def changeState(changer: Trace, state: State) {
    if(state.contains("fuck")) {
      status = "FUCK"
      if(changer.getState.getString("gender") == "male" || changer.getState.getString("gender") == "queer") {
        // TODO: add refusing probability
        gender = "queer"
      }
      else if(changer.getState.getString("gender") == "female")
        changer.changeState(this, new State("fuck"))
    }
    if(state.contains("fight")) { // TODO: add male vs queer fighting
      status = "FIGHT"
      changer.getState.getString("gender") match {
        case "male" => {
          if(math.random < property("male.fight.male.kill", 0.1)) is_alive = false
          else if(math.random < property("male.fight.male.kill", 0.1)) changer.changeState(this, new State("kill"))
        }
        case "female" => {
          if(math.random < property("female.fight.male.kill", 0.1)) is_alive = false
          else if(math.random < property("male.fight.female.kill", 0.3)) changer.changeState(this, new State("kill"))
        }
        case _ =>
      }
    }
    super.changeState(changer, state)
  }

  val male_action_id:Int = action {
    if(is_alive) {
      if(gender == "male" && !is_child) { // TODO: add queers activity
        tracer.tracesInPoint(point, trace => {trace.id != id &&
          trace.point == point && trace.getState.getBool("alive") && !trace.getState.getBool("child") &&
          coord.dist(trace.coord) < 20 &&
          !already_met.contains(trace)
        }).foreach(trace => {
          already_met = trace ::already_met
          trace.getState.getString("gender") match {
            case "male" => if(math.random < property("male.fight.male", 0.9)) {
                             status = "FIGHT"
                             trace.changeState(this, new State("fight"))
                           }
                           else {
                             status = "FUCK"
                             trace.changeState(this, new State("fuck"))
                             gender = "queer"
                           }
            case "female" => {
              status = "FUCK"
              trace.changeState(this, new State("fuck"))
            }
            case "queer" => if(!is_queer_tolerate) {
              status = "FIGHT"
              trace.changeState(this, new State("fight"))
            }
          }
        })
      }
    }
    else delActionOperation(male_action_id)
  }

  val male_image  = image("man.png", 40, 40, 0, 0, 80, 80)
  val dead_male_image  = image("dead_man.png", 41, 52, 0, 0, 41, 52)

  val queer_image = image("boy.png", 28, 50, 0, 0, 57, 100)
  val dead_queer_image = image("dead_boy.png", 31, 42, 0, 0, 31, 42)

  render {
    if(is_alive) {
      if(is_child) drawDisplayList(baby_image, coord, WHITE)
      else {
        gender match {
          case "male" => drawDisplayList(male_image, coord, WHITE)
          case "queer" => drawDisplayList(queer_image, coord, WHITE)
        }
      }
    }
    else {
      drawDisplayList(skull, coord, WHITE)
      /*gender match {
        case "male" => drawDisplayList(dead_male_image, coord, WHITE)
        case "queer" => drawDisplayList(dead_queer_image, coord, WHITE)
      }*/
    }
  }
}