package su.msk.dunno.scar

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import net.phys2d.math.Vector2f
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.screens.prototypes.{ScageAction, ScageRender}
import su.msk.dunno.scage.single.support.messages.ScageMessage._

object Scaranoid extends ScageScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) {
  private var is_game_over = false
  def isGameOver = is_game_over
  private var count = 0

  new StaticLine(Vec(30, 10),   Vec(30, 470))
  new StaticLine(Vec(30, 470),  Vec(630, 470))
  new StaticLine(Vec(630, 470), Vec(630, 10))
  new StaticLine(Vec(630, 10),  Vec(30, 10)) {
    addAction(new ScageAction {
      override def action = {
        if(isTouching) is_game_over = true
      }
    })
  }

  class TargetBox(leftup_coord:Vec) extends StaticBox(leftup_coord, 40, 40) {
    addAction(new ScageAction {
      override def action = {
        if(isActive && isTouching) {
          count += 1
          if(count >= 39) is_game_over = true
          isActive = false
        }
      }
    })
  }
  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 460))
  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 415))
  for(i <- 0 to 12) new TargetBox(Vec(35 + i*45, 370))

  val player_platform = new StaticBox(Vec(width/2,25), 50, 10)
  keyListener(Keyboard.KEY_LEFT,  10, onKeyDown = if(!is_game_over && player_platform.coord.x > 60) player_platform.move(Vec(-3, 0)))
  keyListener(Keyboard.KEY_RIGHT, 10, onKeyDown = if(!is_game_over && player_platform.coord.x < 600) player_platform.move(Vec(3, 0)))

  val ball_radius = property("ball.radius", 5)
  val ball_speed = property("ball.speed", 25)
  val ball = new DynaBall(Vec(width/2, height/2), ball_radius) {
    addAction(new ScageAction {
      override def action = {
        val old_v = velocity
        if(old_v.norma < ball_speed-1) {
          val new_v = old_v.n * ball_speed
          val delta = new_v - old_v
          body.adjustVelocity(new Vector2f(delta.x, delta.y))
        }
        else if(math.abs(old_v.y) < 1) {
          val new_v = Vec(old_v.x, 5)
          val delta = new_v - old_v
          body.adjustVelocity(new Vector2f(delta.x, delta.y))
        }
      }
    })
  }
  ball.body.adjustVelocity(new Vector2f(0, -ball_speed))

  Scaranoid.addRender(new ScageRender {
    override def interface = {
      if(is_game_over) {
        if(count < 39) print(xml("game.lose"), width/2, height/2, WHITE)
        else print(xml("game.win"), width/2, height/2, WHITE)
      }
      print(count, 5, height-20, WHITE)
      //print(ball.velocity, 10, height-40, WHITE)
    }
  })

  new ScageScreen("Help Screen") {
    keyListener(Keyboard.KEY_SPACE, onKeyDown = stop)

    addRender(new ScageRender {
      override def interface = {
        print(xml("helpscreen.helpmessage"), 10, height-20, WHITE)
      }
    })
  }.run

  def main(args:Array[String]):Unit = run
}