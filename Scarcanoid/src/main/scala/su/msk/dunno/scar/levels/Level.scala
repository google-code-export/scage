package su.msk.dunno.scar.levels

import su.msk.dunno.scar.{TargetBox, Scaranoid}
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._

trait Level {
  val rows:Int
  val columns:Int
  val level:List[Int]

  def load = {
    for {
      i <- 0 until rows
      j <- 0 until columns
      if level(i*columns + j) == 1
      box = Scaranoid --> new TargetBox(Vec(35 + j*45, height-20-45*i))
    } yield box
  }
}