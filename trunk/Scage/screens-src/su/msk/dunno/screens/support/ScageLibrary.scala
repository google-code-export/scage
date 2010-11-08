package su.msk.dunno.screens.support

import su.msk.dunno.screens.handlers.{Idler, Renderer}
import tracer.{Trace, State}
import su.msk.dunno.screens.Screen
import su.msk.dunno.scage.support.{Color, Vec, ScageProperties, Colors}

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
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }
    }
  }

  def allStop = Screen.allStop

  lazy val width = Renderer.width
  lazy val height = Renderer.height
  
  def drawList(list_code:Int, coord:Vec) = Renderer.drawList(list_code:Int, coord:Vec)
  def drawList(list_code:Int, coord:Vec, color:Color) = Renderer.drawList(list_code:Int, coord:Vec, color:Color)

  lazy val framerate = Idler.framerate

  def properties = ScageProperties.properties
  def properties_= (f:String) = ScageProperties.properties = f

  def property[A](key:String, default:A)(implicit m:Manifest[A]):A = ScageProperties.property(key, default)

  def stringProperty(key:String):String = ScageProperties.stringProperty(key)
  def intProperty(key:String):Int = ScageProperties.intProperty(key)
  def floatProperty(key:String):Float = ScageProperties.floatProperty(key)
  def booleanProperty(key:String):Boolean = ScageProperties.booleanProperty(key)

  type StateTrace = Trace[_ <: State]
}