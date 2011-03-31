package su.msk.dunno.scar

import su.msk.dunno.scage.screens.physics._
import su.msk.dunno.scage.screens.physics.objects._

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import net.phys2d.math.Vector2f
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._

object Scaranoid extends PhysicsScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) {
  private var count = 0
  private var bonus = 1
  init {
    count = 0
    bonus = 1
  }

  this --> new StaticLine(Vec(30,  10),   Vec(30,  470))
  this --> new StaticLine(Vec(30,  470),  Vec(630, 470))
  this --> new StaticLine(Vec(630, 470),  Vec(630, 10))
  val down_line = this --> new StaticLine(Vec(630, 10),   Vec(30,  10)) {
    init {
      this.prepare
    }

    action {
      if(isTouching(player_ball)) pause
    }
  }

  val physics_screen = this
  class TargetBox(leftup_coord:Vec) extends StaticBox(leftup_coord, 40, 40) {
    init {
      physics_screen --> this
    }

    action {
      if(isActive && isTouching) {
        count += bonus
        isActive = false

        if(winCondition) pause
        else bonus += 1
      }
    }
  }

  private var boxes:List[TargetBox] = Nil
  for(i <- 0 to 12) boxes = new TargetBox(Vec(35 + i*45, 460)) :: boxes
  for(i <- 0 to 12) boxes = new TargetBox(Vec(35 + i*45, 415)) :: boxes
  for(i <- 0 to 12) boxes = new TargetBox(Vec(35 + i*45, 370)) :: boxes
  def winCondition = boxes.forall(!_.isActive)

  val player_platform = this --> new StaticBox(Vec(width/2,25), 50, 10) {
    init {
      coord = Vec(width/2,25)
    }

    action {
      if(isTouching) bonus = 1
    }
  }

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && player_platform.coord.x > 60) player_platform.move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && player_platform.coord.x < 600) player_platform.move(Vec(3, 0)))

  val ball_radius = property("ball.radius", 5)
  val ball_speed = property("ball.speed", 25)
  class PlayerBall(coord:Vec) extends DynaBall(coord, ball_radius) {
    velocity = new Vec(-ball_speed, -ball_speed)

    action {
      if(velocity.norma < ball_speed-1)
        velocity = velocity.n * ball_speed
      else if(math.abs(velocity.y) < 1)
        velocity = Vec(velocity.x, 10*math.signum(velocity.y))
    }
  }

  val player_ball = this --> new PlayerBall(Vec(width/2, height/2)) {
    init {
      coord = Vec(width/2, height/2)
      velocity = new Vec(-ball_speed, -ball_speed)
    }
  }

  interface {
    if(onPause) {
      if(winCondition) print(xml("game.win"), width/2, height/2, WHITE)
      else print(xml("game.lose"), width/2, height/2, WHITE)
      print(xml("game.playagain"), width/2, height/2-20, WHITE)
    }
    print(count, 5, height-20, WHITE)
    print("x"+bonus, 5, height-40, WHITE)
    print(fps, 5, height-60, WHITE)
  }
  key(KEY_Y, onKeyDown = if(onPause) {
    init
    pauseOff
  })
  key(KEY_N, onKeyDown = if(onPause) stop)

  new ScageScreen("Help Screen") {
    key(KEY_SPACE, onKeyDown = stop)

    interface {
      print(xml("helpscreen.helpmessage"), 10, height-20, WHITE)
    }
  }.run

  def main(args:Array[String]):Unit = run
}