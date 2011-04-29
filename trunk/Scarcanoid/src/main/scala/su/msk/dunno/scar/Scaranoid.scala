package su.msk.dunno.scar

import levels.LevelMap1
import su.msk.dunno.scage.screens.physics._
import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._

object Scaranoid extends ScageScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) with ScagePhysics {
  var count = 0
  var bonus = 0
  private var current_level = 1
  val max_level = property("level.max", 1)
  init {
    bonus = 0
    current_level match {
      case 1 => Level.load(LevelMap1)
      case _ => Level.load(LevelMap1)
    }
  }
  Scaranoid --> PlayerBall
  Scaranoid --> PlayerPlatform

  interface {
    print(count, 5, height-20, WHITE)
    print("+"+bonus, 5, height-40, WHITE)
    print(world.getBodies().size(), 5, height-60, WHITE)

    if(onPause) {
      if(Level.winCondition) print(xml("game.win"), width/2, height/2, WHITE)
      else print(xml("game.lose"), width/2, height/2, WHITE)
      print(xml("game.playagain"), width/2, height/2-20, WHITE)
    }
  }

  key(KEY_Y, onKeyDown = if(onPause) {
    if(Level.winCondition) {
      if(current_level == max_level) current_level = 1
      else current_level += 1
    }
    else {
      count = 0
      current_level = 1
    }

    init()
    pauseOff()
  })
  key(KEY_N, onKeyDown = if(onPause) stop())

  action {
    step()
  }

  new ScageScreen("Help Screen") {
    key(KEY_SPACE, onKeyDown = stop())

    interface {
      print(xml("helpscreen.helpmessage"), 10, height-20, WHITE)
    }
  }.run()

  def main(args:Array[String]) {
    run()
  }
}