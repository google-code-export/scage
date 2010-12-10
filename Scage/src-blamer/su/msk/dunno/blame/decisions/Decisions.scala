package su.msk.dunno.blame.decisions

import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.{BottomMessages, TimeUpdater}
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.blame.animations.BulletFlight
import su.msk.dunno.blame.prototypes.{Item, Living, Decision}

class Move(val step:Vec, living:Living) extends Decision(living) {
  override val action_period = 2

  def doAction = {
    val new_point = living.point + step
    was_executed = FieldTracer.move2PointIfPassable(living.trace, living.point, new_point)
  }
}

class OpenDoor(living:Living) extends Decision(living) {
  override val action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.point, 1)
               .foreach(_.changeState(new State("door_open", living.stat("name"))))
    was_executed = true
  }
}

class CloseDoor(living:Living) extends Decision(living) {
  override val action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.point, 1)
               .foreach(_.changeState(new State("door_close", living.stat("name"))))
    was_executed = true
  }
}

class Shoot(target_point:Vec, living:Living) extends Decision(living) {
  override val action_period = 2

  def doAction = {
    if(target_point != living.point) {
      new BulletFlight(living.point, target_point, YELLOW)
      val kickback = (living.point - target_point).n
      val kickback_delta = Vec(if(math.abs(kickback.x) > 0.3) math.signum(kickback.x) else 0, if(math.abs(kickback.y) > 0.3) math.signum(kickback.y) else 0)
      TimeUpdater.addDecision(new Move(kickback_delta, living))
      val objects = FieldTracer.objectsAtPoint(target_point)
      BottomMessages.addBottomPropMessage("decision.shoot", living.stat("name"))
      objects.foreach(_.changeState(new State("damage", 10)))
      was_executed = true
    }
  }
}

class DropItem(living:Living, item:Item) extends Decision(living) {
  override val action_period = 2
  
  def doAction = {
    living.inventory.removeItem(item)
  }
}
