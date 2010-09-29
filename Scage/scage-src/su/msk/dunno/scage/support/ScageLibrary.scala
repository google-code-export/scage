package su.msk.dunno.scage.support

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import su.msk.dunno.scage.support.tracer.{StandardTracer}

trait ScageLibrary extends Colors {
  lazy val width = Renderer.width
  lazy val height = Renderer.height
  def scale = Renderer.scale
  def scale_= (value:Float):Unit = Renderer.scale = value
  def fps = Idler.fps

  lazy val game_width = StandardTracer.game_width
  lazy val game_height = StandardTracer.game_height

  lazy val framerate = Idler.framerate

  def on_pause = Scage.on_pause
  def switchPause = Scage.switchPause
  def start = Scage.start
  def stop = Scage.stop

  implicit def vec2tracervec(old_coord:Vec) = new Vec(old_coord) {
    def ->(new_coord:Vec) = StandardTracer.updateLocation(old_coord, new_coord)
  }
}