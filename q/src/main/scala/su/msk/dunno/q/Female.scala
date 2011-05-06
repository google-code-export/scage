package su.msk.dunno.q

import su.msk.dunno.scage.screens.support.tracer.{State, Trace}
import QueersIsland._
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.ScageProperties._

class Female extends Human("female") {
  override def changeState(changer: Trace, state: State) {
    if(state.contains("fuck")) {
      if(math.random < property("male.fuck.female.refuse", 0.6)) {
        status = "FIGHT"
        changer.changeState(this, new State("fight"))
      }
      else {
        status = "FUCK"
        if(math.random < property("male.fuck.female.success", 0.3)) {
          if(math.random < property("male.fuck.female.success.maleborn", 0.5)) {
            status = "BABYBORN"
            tracer.addTrace(coord, new Male)
          }
          else tracer.addTrace(coord, new Female)
        }
      }
    }
    super.changeState(changer, state)
  }

  // TODO: add females activity

  val female_image = image("girl.png", 25, 45, 0, 0, 50, 90)
  val dead_female_image = image("dead_girl.png", 26, 42, 0, 0, 26, 42)

  render {
    if(is_alive) {
      if(is_child) drawDisplayList(baby_image, coord, WHITE)
      else drawDisplayList(female_image, coord, WHITE)
    }
    else drawDisplayList(dead_female_image, coord, WHITE)
  }
}