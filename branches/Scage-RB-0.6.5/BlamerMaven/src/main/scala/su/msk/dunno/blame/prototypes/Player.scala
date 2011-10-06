package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.single.support.{Vec, ScageColor}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.screens.{Blamer, CommandScreen}
import su.msk.dunno.blame.decisions.{DoNothing, OpenDoor, Shoot, Move}

abstract class Player(name:String, description:String, point:Vec, color:ScageColor)
extends Npc(name, description, point, PLAYER, color) {
  override def getSymbol = PLAYER
  setStat("player")
  FieldTracer.addLightSource(point, intStat("dov"), trace)

  private lazy val command_screen = new CommandScreen(this)
  def selectCommand = command_screen.selectCommand

  def livingAI:Decision = {
    if(!isCurrentPlayer) {
      if(boolStat("attack")) {
        FieldTracer.findVisibleObject(trace, point, intStat("dov"), obj => {
          obj.getState.contains("enemy") && obj.getState.getInt("health") > 0
        }) match {
          case Some(live_enemy) => return new Shoot(this, live_enemy.getPoint)
          case None =>
        }
      }
      if(boolStat("follow")) {
        FieldTracer.findPath(point, Blamer.currentPlayer.getPoint) match {
          case step1 :: step2 :: tail => {
            FieldTracer.findObjectAtPoint(step1, "door") match {
              case Some(door) => if(door.getState.contains("close")) return new OpenDoor(this)
              case None =>
            }
            return new Move(this, FieldTracer.direction(point, step1))
          }
          case _ => return new Move(this, randomDir)
        }
      }
      return new DoNothing(this)
    }
    return null
  }

  override def changeState(s:State) = {
    super.changeState(s)
    if(s.contains("follow")) setStat("follow", true)
    if(s.contains("stay")) setStat("follow", false)
    if(s.contains("attack")) setStat("attack", true)
    if(s.contains("noattack")) setStat("attack", false)
  }
}