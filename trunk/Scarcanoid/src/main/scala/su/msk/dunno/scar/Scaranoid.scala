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
  init {
    count = 0
  }

  new StaticLine(Vec(30,  10),   Vec(30,  470)) -> this
  new StaticLine(Vec(30,  470),  Vec(630, 470)) -> this
  new StaticLine(Vec(630, 470),  Vec(630, 10)) -> this
  new StaticLine(Vec(630, 10),   Vec(30,  10)) {
    action {
      if(isTouching) pause
    }
  } -> this

  class TargetBox(leftup_coord:Vec) extends StaticBox(leftup_coord, 40, 40) {
    action {
      if(isTouching) {
        count += 1
        if(count >= 39) pause
        isActive = false
      }
    }
  }

  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 460)) -> this
  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 415)) -> this
  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 370)) -> this

  val player_platform = new StaticBox(Vec(width/2,25), 50, 10) {
    init {
      coord = Vec(width/2,25)
    }
  } -> this

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && player_platform.coord.x > 60) player_platform.move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && player_platform.coord.x < 600) player_platform.move(Vec(3, 0)))

  val ball_radius = property("ball.radius", 5)
  val ball_speed = property("ball.speed", 25)
  new DynaBall(Vec(width/2, height/2), ball_radius) {
    action {
      if(velocity.norma < ball_speed-1)
        velocity = velocity.n * ball_speed
      else if(math.abs(velocity.y) < 1)
        velocity = Vec(velocity.x, 10*math.signum(velocity.y))
    }

    init {
      coord = Vec(width/2, height/2)
      velocity = new Vec(-ball_speed, -ball_speed)
    }
  } -> this

  interface {
    if(onPause) {
      if(count < 39) print(xml("game.lose"), width/2, height/2, WHITE)
      else print(xml("game.win"), width/2, height/2, WHITE)
      print(xml("game.playagain"), width/2, height/2-20, WHITE)
    }
    print(count, 5, height-20, WHITE)
    print(fps, 5, height-40, WHITE)
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