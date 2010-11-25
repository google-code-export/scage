package su.msk.dunno.blame.decisions

import su.msk.dunno.blame.prototypes.{Living, Decision}
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.{IngameMessages, TimeUpdater}

class Move(val step:Vec, living:Living) extends Decision(living) {
  action_period = 2

  def doAction = {
    val new_point = living.point + step
    FieldTracer.move2PointIfPassable(living.trace, living.point, new_point)
  }
}

class OpenDoor(living:Living) extends Decision(living) {
  action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.point, -1 to 1).foreach(neighbour => {
      val neighbour_state = neighbour.getState
      if(neighbour_state.contains("door") && "close".equals(neighbour_state.getString("door"))) {
        neighbour.changeState(new State("door_open"))
        IngameMessages.addBottomPropMessage("door.open", living.stat("name"))
      }
    })
    TimeUpdater.addDecision(new Move(Vec(1,0), living))
    true
  }
}

class CloseDoor(living:Living) extends Decision(living) {
  action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.point, -1 to 1).foreach(neighbour => {
      val neighbour_state = neighbour.getState
      if(neighbour_state.contains("door") && "open".equals(neighbour_state.getString("door"))) {
        neighbour.changeState(new State("door_close"))
        IngameMessages.addBottomPropMessage("door.close", living.stat("name"))
      }
    })
    true
  }
}
