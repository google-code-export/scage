package su.msk.dunno.scage.support

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import tracer.{Point, Tracer, StandardTracer}

trait ScageLibrary extends Colors {
  lazy val width = Renderer.width
  lazy val height = Renderer.height
  def scale = Renderer.scale
  def scale_= (value:Float):Unit = Renderer.scale = value

  lazy val game_width = StandardTracer.game_width
  lazy val game_height = StandardTracer.game_height

  lazy val framerate = Idler.framerate
  def fps = Idler.fps

  def on_pause = Scage.on_pause
  def switchPause = Scage.switchPause
  def start = Scage.start
  def stop = Scage.stop

  implicit def vec2tracervec(old_coord:Vec) = new Vec(old_coord) {
    def ->(new_coord:Vec) = {
      new ScalaObject {
        def in(trace_id:Int) = {
          if(Tracer.currentTracer != null) Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
        }
      }      
    }
  }
  def point(v:Vec) = {
    if(Tracer.currentTracer != null) Tracer.currentTracer.point(v)
    else Point(0,0)
  }
}