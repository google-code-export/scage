package su.msk.dunno.blame.decisions

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.{BottomMessages, TimeUpdater}
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.blame.animations.BulletFlight
import su.msk.dunno.blame.prototypes.{Item, Living, Decision}
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}

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
      if(FieldTracer.isNearPlayer(living.point)) new BulletFlight(living.point, target_point, YELLOW)
      val kickback = (living.point - target_point).n
      val kickback_delta = Vec(if(math.abs(kickback.x) > 0.3) math.signum(kickback.x) else 0,
                               if(math.abs(kickback.y) > 0.3) math.signum(kickback.y) else 0)
      TimeUpdater.addDecision(new Move(kickback_delta, living))
      val objects = FieldTracer.objectsAtPoint(target_point)
      BottomMessages.addPropMessage("decision.shoot", living.stat("name"))
      objects.foreach(_.changeState(new State("damage", 10)))
      was_executed = true
    }
  }
}

class DropItem(item:Option[FieldObject], living:Living) extends Decision(living) {
  override val action_period = 2
  
  def doAction = {
    item match {
      case Some(item_to_drop) => {
        living.inventory.removeItem(item_to_drop)
        item_to_drop.changeState(new State("location", living.point))
        FieldTracer.addTrace(item_to_drop)
      }
      case None =>
    }
  }
}

class PickUpItem(living:Living) extends Decision(living) {
  override val action_period = 2
  
  def doAction = {
    FieldTracer.objectsAtPoint(living.point).find(_.getState.contains("item")) match {
      case Some(item) => {
        living.inventory.addItem(item)
        FieldTracer.removeTraceFromPoint(item.id, item.getPoint)
      }
      case None =>
    }
  }
}
