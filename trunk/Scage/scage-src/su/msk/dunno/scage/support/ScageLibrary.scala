package su.msk.dunno.scage.support

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import tracer.{Tracer, StandardTracer}

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
    def in(trace_id:Int) = new ScalaObject {
      def ->(new_coord:Vec):Boolean = {
        if(Tracer.currentTracer != null) Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
        else false
      }

      def -->(new_coord:Vec, range:Range, dist:Float):Boolean = {
        if(Tracer.currentTracer != null) {
          if(!Tracer.currentTracer.hasCollisions(trace_id, new_coord, range, dist))
            Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
          else false
        }
        else false
      }

      def ?(range:Range, dist:Float):Boolean = {
        if(Tracer.currentTracer != null) Tracer.currentTracer.hasCollisions(trace_id, old_coord, range, dist)
        else false
      }
    }
  }
  def point(v:Vec) = {
    if(Tracer.currentTracer != null) Tracer.currentTracer.point(v)
    else Vec(0,0)
  }
}