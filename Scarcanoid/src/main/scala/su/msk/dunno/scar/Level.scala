package su.msk.dunno.scar

import levels.LevelMap
import su.msk.dunno.scage.screens.physics.Physical
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._
import Scaranoid._
import su.msk.dunno.scage.screens.physics.objects.{StaticBox, StaticLine}
import su.msk.dunno.scage.single.support.ScageColors._

object Level {
  private var boxes:IndexedSeq[Physical] = null
  def winCondition = boxes.forall(!_.isActive)
  def load(level_map:LevelMap) {
    boxes = level_map.load
  }

  Scaranoid --> new StaticLine(Vec(30,  10),   Vec(30,  height-10))
  Scaranoid --> new StaticLine(Vec(30,  height-10),  Vec(width-10, height-10))
  Scaranoid --> new StaticLine(Vec(width-10, height-10),  Vec(width-10, 10))
  val down_line = Scaranoid --> new StaticLine(Vec(width-10, 10),   Vec(30,  10)) {
    init {
      prepare()
    }

    action {
      if(isTouching(PlayerBall)) pause()
    }
  }

  val additional_platform = Scaranoid --> new StaticBox(Vec(width/4, 200), 150, 10) {
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
}