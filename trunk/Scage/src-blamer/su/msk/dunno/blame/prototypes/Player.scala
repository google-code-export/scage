package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.support.{Vec, ScageColor}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.screens.{Blamer, CommandScreen, SelectTarget}
import su.msk.dunno.blame.support.TimeUpdater
import su.msk.dunno.blame.decisions.{Shoot, Move}

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

  private var is_attack_enemies = false
  def isAttackEnemies = is_attack_enemies
  private var is_follow_player = false
  def isFollowPlayer = is_follow_player
  def livingAI:Decision = {
    if(!isCurrentPlayer) {
      if(is_attack_enemies) {
        FieldTracer.findVisibleObject(trace, point, intStat("dov"), obj => {
          obj.getState.contains("enemy") && obj.getState.getInt("health") > 0
        }) match {
          case Some(live_enemy) => return new Shoot(this, live_enemy.getPoint)
          case None =>
        }
      }
      if(is_follow_player) {
        FieldTracer.findPath(point, Blamer.currentPlayer.getPoint) match {
          case head :: tail => {
            val step = FieldTracer.direction(point, head)
            return new Move(this, step)
          }
          case _ =>
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

  protected def isCurrentPlayer = Blamer.currentPlayer.getPoint == point
}