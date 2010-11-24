package su.msk.dunno.blame.decisions

import su.msk.dunno.blame.prototypes.{Living, Decision}
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State

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
    FieldTracer.neighboursOfPoint(living.trace, living.point, -1 to 1).foreach(_.changeState(new State("door_open")))
    true
  }
}

class CloseDoor(living:Living) extends Decision(living) {
  action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.point, -1 to 1).foreach(_.changeState(new State("door_close")))
    true
  }
}
