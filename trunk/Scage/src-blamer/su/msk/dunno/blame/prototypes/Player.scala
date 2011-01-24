package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.{Vec, ScageColor}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.screens.{Blamer, CommandScreen, SelectTarget}
import su.msk.dunno.blame.decisions.{Shoot, Move}
import su.msk.dunno.blame.support.BottomMessages

abstract class Player(name:String, description:String, point:Vec, color:ScageColor)
extends Npc(name, description, point, PLAYER, color) {
  override def getSymbol = PLAYER
  setStat("player")
  FieldTracer.addLightSource(point, intStat("dov"), trace)

  private lazy val target_selector = new SelectTarget(this)
  def selectTarget(stop_key:Int):Vec = {
    target_selector(stop_key)
  }

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
            val step = FieldTracer.direction(point, step1)
            return new Move(this, step)
          }
          case _ => return new Move(this, randomDir)
        }
      }
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