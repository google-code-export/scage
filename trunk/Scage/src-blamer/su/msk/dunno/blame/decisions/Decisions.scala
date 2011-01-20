package su.msk.dunno.blame.decisions

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.{BottomMessages, TimeUpdater}
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.blame.animations.BulletFlight
import su.msk.dunno.blame.prototypes.{Living, Decision}
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.blame.screens.Blamer
import org.lwjgl.input.Keyboard

class Move(living:Living, val step:Vec) extends Decision(living) {
  override val action_period = 2

  def doAction = {
    val new_point = living.getPoint + step
    was_executed = FieldTracer.move2PointIfPassable(living.trace, living.getPoint, new_point)
  }
}

class OpenDoor(living:Living) extends Decision(living) {
  override val action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.getPoint, 1).find(fo => {
      fo.getState.contains("door") &&
      "close" == fo.getState.getString("door")
    }) match {
      case Some(door) => {
        door.changeState(new State("door_open", living.stat("name")))
        was_executed = true
      }
      case None =>
    }
  }
}

class CloseDoor(living:Living) extends Decision(living) {
  override val action_period = 1

  def doAction = {
    FieldTracer.neighboursOfPoint(living.trace, living.getPoint, 1).find(possible_door => {
      possible_door.getState.contains("door") &&
      "open" == possible_door.getState.getString("door") &&
      !FieldTracer.objectsAtPoint(possible_door.getPoint).exists(_.getState.contains("living"))
    }) match {
      case Some(door) => {
        door.changeState(new State("door_close", living.stat("name")))
        was_executed = true
      }
      case None =>
    }
  }
}

class Shoot(living:Living, private val defined_target:Vec = Vec(-1,-1)) extends Decision(living) {
  //def this(living:Living) = this(Vec(-1,-1), living)
  override val action_period = 2

  def doAction = {
    val target_point = if(defined_target == Vec(-1, -1)) living.selectTarget(Keyboard.KEY_F) else defined_target
    if(target_point != living.getPoint) {
      BottomMessages.addPropMessage("decision.shoot", living.stat("name"))
      if(FieldTracer.isNearPlayer(living.getPoint)) new BulletFlight(living.getPoint, target_point, YELLOW)
      val kickback = (living.getPoint - target_point).n
      val kickback_delta = Vec(if(math.abs(kickback.x) > 0.3) math.signum(kickback.x) else 0,
                               if(math.abs(kickback.y) > 0.3) math.signum(kickback.y) else 0)
      TimeUpdater.addDecision(new Move(kickback_delta, living))
      FieldTracer.objectsAtPoint(target_point).foreach(_.changeState(new State("damage", 10)))
      was_executed = true
    }
  }
}

class DropItem(living:Living) extends Decision(living) {
  override val action_period = 2
  
  def doAction = {
    living.inventory.selectItem(ScageMessage.xml("decision.drop.selection")) match {
      case Some(item_to_drop) => {
        living.inventory.removeItem(item_to_drop)
        item_to_drop.changeState(new State("point", living.getPoint))
        FieldTracer.addTraceSecondToLast(item_to_drop)
        BottomMessages.addPropMessage("decision.drop", living.stat("name"), item_to_drop.getState.getString("name"))
        was_executed = true
      }
      case None =>
    }
  }
}

class PickUpItem(living:Living) extends Decision(living) {
  override val action_period = 2
  
  def doAction = {
    FieldTracer.objectsAtPoint(living.getPoint).find(_.getState.contains("item")) match {
      case Some(item) => {
        living.inventory.addItem(item)
        FieldTracer.removeTraceFromPoint(item.id, item.getPoint)
        BottomMessages.addPropMessage("decision.pickup", living.stat("name"), item.getState.getString("name"))
      }
      case None => BottomMessages.addPropMessage("decision.pickup.failed", living.stat("name"))
    }
    was_executed = true
  }
}

class OpenWeapon(living:Living) extends Decision(living) {
  def doAction = {
    living.weapon.showWeapon
  }
}

class OpenInventory(living:Living) extends Decision(living) {
  def doAction = {
    living.inventory.showInventory
  }
}
