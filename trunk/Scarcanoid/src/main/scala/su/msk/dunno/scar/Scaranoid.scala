package su.msk.dunno.scar

import levels.Level1
import su.msk.dunno.scage.screens.physics._
import su.msk.dunno.scage.screens.physics.objects._

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.Vec

object Scaranoid extends PhysicsScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) {
  val max_level = property("level.max", 1)

  var count = 0
  var bonus = 0
  private var current_level = 1
  init {
    bonus = 0
  }

  this --> new StaticLine(Vec(30,  10),   Vec(30,  height-10))
  this --> new StaticLine(Vec(30,  height-10),  Vec(width-10, height-10))
  this --> new StaticLine(Vec(width-10, height-10),  Vec(width-10, 10))
  val down_line = this --> new StaticLine(Vec(width-10, 10),   Vec(30,  10)) {
    init {
      prepare()
    }

    action {
      if(isTouching(PlayerBall)) pause
    }
  }

  private var boxes:IndexedSeq[Physical] = null
  init {
    current_level match {
      case 1 => boxes = Level1.load
      case _ => boxes = Level1.load
    }

  }
  def winCondition = boxes.forall(!_.isActive)

  val player_platform = this --> new StaticBox(Vec(width/2,25), 50, 10) {
    init {
      coord = Vec(width/2,25)
    }
  }

  val additional_platform = this --> new StaticBox(Vec(width/4, 200), 150, 10) {
    init {
      coord = Vec(width/4, 200)
    }

    private var dir = 1
    action {
      if(isTouching(PlayerBall)) PlayerBall.ball_color = WHITE
      move(Vec(dir,0))
      if(coord.x > width-90) dir = -1
      else if(coord.x < 110) dir = 1
    }
  }

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && player_platform.coord.x > 60) player_platform.move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && player_platform.coord.x < width-40) player_platform.move(Vec(3, 0)))

  interface {
    print(count, 5, height-20, WHITE)
    print("+"+bonus, 5, height-40, WHITE)

    if(onPause) {
      if(winCondition) print(xml("game.win"), width/2, height/2, WHITE)
      else print(xml("game.lose"), width/2, height/2, WHITE)
      print(xml("game.playagain"), width/2, height/2-20, WHITE)
    }
  }

  key(KEY_Y, onKeyDown = if(onPause) {
    if(winCondition) {
      if(current_level == max_level) current_level = 1
      else current_level += 1
    }
    else {
      count = 0
      current_level = 1
    }

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

  def main(args:Array[String]) {
    run
  }
}