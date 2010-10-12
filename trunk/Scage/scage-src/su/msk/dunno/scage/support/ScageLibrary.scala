package su.msk.dunno.scage.support

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import tracer.{State, Trace, Tracer, StandardTracer}

object ScageLibrary extends Colors {
  implicit def rangeToPairs(range:Range) = {
    new ScalaObject {
      def foreachpair(doIt:(Int, Int) => Unit) = {
        range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }

      def foreachpair(second_range:Range)(doIt:(Int, Int) => Unit) = {
        range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (second_range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }
    }
  }

  lazy val width = Renderer.width
  lazy val height = Renderer.height
  def scale = Renderer.scale
  def scale_= (value:Float):Unit = Renderer.scale = value

  lazy val framerate = Idler.framerate
  def fps = Idler.fps
  def time = Idler.time

  def onPause = Scage.on_pause
  def switchPause = Scage.switchPause
  def start = Scage.start
  def stop = Scage.stop

  implicit def traceInVec(trace_id:Int) = new ScalaObject {
    def in(old_coord:Vec) = new ScalaObject {
      def ->(new_coord:Vec):Boolean = {
        if(Tracer.currentTracer != null) Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
        else false
      }

      def -->(new_coord:Vec, range:Range, dist:Float):Boolean = {
        if(Tracer.currentTracer != null) {
          if(!Tracer.currentTracer.hasCollisions(trace_id, new_coord, range, dist, (t:Trace[_]) => true))
            Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
          else false
        }
        else false
      }

      def -->[R <: State](new_coord:Vec, range:Range, dist:Float, condition:(Trace[R]) => Boolean):Boolean = {
        if(Tracer.currentTracer != null) {
          if(!Tracer.currentTracer.hasCollisions(trace_id, new_coord, range, dist, condition))
            Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
          else false
        }
        else false
      }

      def ?(range:Range, dist:Float):Boolean = {
        if(Tracer.currentTracer != null)
          Tracer.currentTracer.hasCollisions(trace_id, old_coord, range, dist, (t:Trace[_]) => true)
        else false
      }

      def ?[R <: State](range:Range, dist:Float, condition:(Trace[R]) => Boolean):Boolean = {
        if(Tracer.currentTracer != null)
          Tracer.currentTracer.hasCollisions(trace_id, old_coord, range, dist, condition)
        else false
      }
    }
  }
  
  def point(v:Vec) = {
    if(Tracer.currentTracer != null) Tracer.currentTracer.point(v)
    else Vec(0,0)
  }

  lazy val game_from_x = Tracer.currentTracer.game_from_x
  lazy val game_to_x = Tracer.currentTracer.game_to_x

  lazy val game_from_y = Tracer.currentTracer.game_from_y
  lazy val game_to_y = Tracer.currentTracer.game_to_y

  lazy val game_width = Tracer.currentTracer.game_width
  lazy val game_height = Tracer.currentTracer.game_height

  lazy val N_x = Tracer.currentTracer.N_x
  lazy val N_y = Tracer.currentTracer.N_y

  lazy val h_x = Tracer.currentTracer.h_x
  lazy val h_y = Tracer.currentTracer.h_y

  def properties = ScageProperties.file
  def properties_=(f:String)  = ScageProperties.file = f
}