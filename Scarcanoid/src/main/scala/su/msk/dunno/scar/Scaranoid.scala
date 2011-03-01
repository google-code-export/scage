package su.msk.dunno.scar

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer

object Scaranoid extends ScageScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) {
  new DynaBall(Vec(320, 240), 5)
  new StaticBox(Vec(200,200), 200, 10)
  new StaticLine(Vec(10, 100), Vec(630, 100))

  //Renderer.backgroundColor = WHITE

  def main(args:Array[String]):Unit = run
}