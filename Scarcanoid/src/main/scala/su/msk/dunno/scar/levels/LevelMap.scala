package su.msk.dunno.scar.levels

import su.msk.dunno.scar.{TargetBox, Scaranoid}
import su.msk.dunno.scar.Scaranoid._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._

trait LevelMap {
  val rows:Int
  val columns:Int
  val level:List[Int]

  def load = {
    for {
      i <- 0 until rows
      j <- 0 until columns
      if level(i*columns + j) == 1
      box = physics.addPhysical(new TargetBox(Vec(55 + j*45, screen_height-40-45*i)))
    } yield box
  }
}