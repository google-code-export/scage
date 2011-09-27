package su.msk.dunno.scar

import levels.LevelMap1
import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.support.physics.ScagePhysics

object Scaranoid extends ScageScreen("Scaranoid", is_main_screen = true, "scaranoid.properties") {
  val physics = ScagePhysics(PlayerBall, PlayerPlatform)
  action {
    physics.step()
  }

  var count = 0
  var bonus = 1
  private var current_level = 1
  val max_level = property("level.max", 1)
  init {
    bonus = 0
    current_level match {
      case 1 => Level.loadMap(LevelMap1)
      case _ => Level.loadMap(LevelMap1)
    }
  }

  interface {
    print(count, 5, screen_height-20, WHITE)
    print("+"+bonus, 5, screen_height-40, WHITE)
    print(physics.world.getBodies.size(), 5, screen_height-60, WHITE)

    if(onPause) {
      if(Level.winCondition) print(xml("game.win"), screen_width/2, screen_height/2, WHITE)
      else print(xml("game.lose"), screen_width/2, screen_height/2, WHITE)
      print(xml("game.playagain"), screen_width/2, screen_height/2-20, WHITE)
    }
  }

  key(KEY_Y, onKeyDown = if(onPause) {
    if(Level.winCondition) {
      if(current_level == max_level) current_level = 1
      else current_level += 1
    } else {
      count = 0
      current_level = 1
    }

    init()
    pauseOff()
  })
  key(KEY_N, onKeyDown = if(onPause) stop())

  new ScageScreen("Help Screen") {
    key(KEY_SPACE, onKeyDown = stop())

    interface {
      print(xml("helpscreen.helpmessage"), 10, screen_height-20, WHITE)
    }
  }.run()

  def main(args:Array[String]) {
    run()
  }
}