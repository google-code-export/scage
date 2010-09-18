package su.msk.dunno.scage.support

import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}

trait ScageLibrary extends Colors {
  lazy val width = Renderer.width
  lazy val height = Renderer.height
  def scale = Renderer.scale
  def scale_= (value:Float):Unit = Renderer.scale = value
  def fps = Renderer.fps

  lazy val game_width = StandardTracer.game_width
  lazy val game_height = StandardTracer.game_height

  lazy val framerate = Idler.framerate

  def on_pause = Scage.on_pause
  def start = Scage.start
  def stop = Scage.stop
}