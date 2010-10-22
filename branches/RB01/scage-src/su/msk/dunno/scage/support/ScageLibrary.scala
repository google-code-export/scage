package su.msk.dunno.scage.support

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import tracer.{State, Trace}
import tracer.Tracer

object ScageLibrary extends Colors {
  implicit def rangeToPairs(range:Range) = {
    new ScalaObject {
      def foreachpair(doIt:(Int, Int) => Unit) = {
        range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }

      def foreachpair(second_range:Range)(doIt:(Int, Int) => Unit) = {
        second_range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(second_range.length, number)).toList ::: pairs)
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

  def onPause = Scage.on_pause
  def switchPause = Scage.switchPause
  def start = Scage.start
  def stop = Scage.stop
  def isRunning = Scage.isRunning

  type StateTrace = Trace[_ <: State]

  implicit def traceInVec(trace_id:Int) = new ScalaObject {
    def in(old_coord:Vec) = new ScalaObject {
      def ->(new_coord:Vec):Boolean = Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)

      def -->(new_coord:Vec, range:Range, dist:Float):Boolean = {
        if(!Tracer.currentTracer.hasCollisions(trace_id, new_coord, range, dist, (t:StateTrace) => true))
          Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
        else false
      }

      def -->(new_coord:Vec, range:Range, dist:Float, condition:(StateTrace) => Boolean):Boolean = {
        if(!Tracer.currentTracer.hasCollisions(trace_id, new_coord, range, dist, condition))
          Tracer.currentTracer.updateLocation(trace_id, old_coord, new_coord)
        else false
      }

      def ?(range:Range, dist:Float):Boolean = {
        Tracer.currentTracer.hasCollisions(trace_id, old_coord, range, dist, (t:StateTrace) => true)
      }

      def ?(range:Range, dist:Float, condition:(StateTrace) => Boolean):Boolean = {
        Tracer.currentTracer.hasCollisions(trace_id, old_coord, range, dist, condition)
      }
    }
  }
  
  def point(v:Vec) = Tracer.currentTracer.point(v)

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

  def properties = ScageProperties.properties
  def properties_= (f:String) = ScageProperties.properties = f

  def property[A](key:String, default:A)(implicit m:Manifest[A]):A = ScageProperties.property(key, default)

  def stringProperty(key:String):String = ScageProperties.stringProperty(key)
  def intProperty(key:String):Int = ScageProperties.intProperty(key)
  def floatProperty(key:String):Float = ScageProperties.floatProperty(key)
  def booleanProperty(key:String):Boolean = ScageProperties.booleanProperty(key)
}