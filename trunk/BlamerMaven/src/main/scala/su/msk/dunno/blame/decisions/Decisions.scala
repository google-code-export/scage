package su.msk.dunno.blame.decisions

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.support.{BottomMessages, TimeUpdater}
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.blame.animations.BulletFlight
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.scage.single.support.messages.ScageMessage
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.prototypes.{Player, Living, Decision}

class Move(living:Living, val step:Vec) extends Decision(living) {
  def doAction = {
    val new_point = living.getPoint + step
    was_executed = FieldTracer.move2PointIfPassable(living.trace, living.getPoint, new_point)
  }
}

class OpenDoor(living:Living) extends Decision(living) {
  def doAction = {
    FieldTracer.findVisibleObject(living.trace, living.getPoint, 1, obj => {
      obj.getState.contains("door") && obj.getState.contains("close")
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
  def doAction = {
    FieldTracer.findVisibleObject(living.trace, living.getPoint, 1, obj => {
      obj.getState.contains("door") && obj.getState.contains("open") &&
      !FieldTracer.objectsAtPoint(obj.getPoint).exists(_.getState.contains("living"))
    }) match {
      case Some(door) => {
        door.changeState(new State("door_close", living.stat("name")))
        was_executed = true
      }
      case None =>
    }
  }
}

class Shoot(living:Living, private val target_point:Vec) extends Decision(living) {
  def this(living:Living) = this(living, Vec(-1,-1))

  def doAction = {
    val target = if(target_point != Vec(-1, -1)) target_point else living.selectTarget(Keyboard.KEY_F)
    if(target != living.getPoint) {
      BottomMessages.addPropMessage("decision.shoot", living.stat("name"))
      if(FieldTracer.isNearPlayer(living.getPoint)) new BulletFlight(living.getPoint, target, YELLOW)
      val kickback = (living.getPoint - target).n
      val kickback_delta = Vec(if(math.abs(kickback.x) > 0.3) math.signum(kickback.x) else 0,
                               if(math.abs(kickback.y) > 0.3) math.signum(kickback.y) else 0)
      TimeUpdater.addDecision(new Move(living, kickback_delta))
      FieldTracer.objectsAtPoint(target).foreach(_.changeState(new State("damage", 10)))
      was_executed = true
    }
  }
}

class DropItem(living:Living) extends Decision(living) {
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
  def doAction = {
    FieldTracer.findObjectAtPoint(living.getPoint, "item") match {
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

class IssueCommand(player:Player) extends Decision(player) {
  private def findPlayer = FieldTracer.findVisibleObject(player.trace, player.getPoint, player.getState.getInt("dov"), obj => {
    obj.getState.contains("player") && obj.getState.getInt("health") > 0
  })

  def doAction:Unit = {
    player.selectCommand match {
      case 1 => findPlayer match {
        case Some(other_player) => {
          other_player.changeState(new State("follow"))
          BottomMessages.addPropMessage("decision.followme", player.getState.getString("name"), other_player.getState.getString("name"))
        }
        case None =>
      }
      case 2 => findPlayer match {
        case Some(other_player) => {
          other_player.changeState(new State("stay"))
          BottomMessages.addPropMessage("decision.stay", player.getState.getString("name"), other_player.getState.getString("name"))
        }
        case None =>
      }
      case 3 => findPlayer match {
        case Some(other_player) => {
          other_player.changeState(new State("attack"))
          BottomMessages.addPropMessage("decision.attack", player.getState.getString("name"), other_player.getState.getString("name"))
        }
        case None =>
      }
      case 4 => findPlayer match {
        case Some(other_player) => {
          other_player.changeState(new State("noattack"))
          BottomMessages.addPropMessage("decision.noattack", player.getState.getString("name"), other_player.getState.getString("name"))
        }
        case None =>
      }
      case _ => return
    }
    was_executed = true
  }
}

class DoNothing(living:Living) extends Decision(living) {
  def doAction = {
    was_executed = true
  }
}
