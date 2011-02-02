package su.msk.dunno.screens.support

import su.msk.dunno.screens.handlers.{Idler, Renderer}
import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.scage.support.{ScageColor, Vec, ScageProperties, ScageColors}

object ScageLibrary extends ScageColors {
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

  def allStop = ScageScreen.allStop

  lazy val width = Renderer.width
  lazy val height = Renderer.height
  
  def drawDisplayList(list_code:Int, coord:Vec) = Renderer.drawDisplayList(list_code:Int, coord:Vec)
  def drawDisplayList(list_code:Int, coord:Vec, color:ScageColor) = Renderer.drawDisplayList(list_code:Int, coord:Vec, color:ScageColor)

  lazy val framerate = Idler.framerate

  def properties = ScageProperties.properties
  def properties_= (f:String) = ScageProperties.properties = f

  def property[A](key:String, default:A)(implicit m:Manifest[A]):A = ScageProperties.property(key, default)

  def stringProperty(key:String):String = ScageProperties.stringProperty(key)
  def intProperty(key:String):Int = ScageProperties.intProperty(key)
  def floatProperty(key:String):Float = ScageProperties.floatProperty(key)
  def booleanProperty(key:String):Boolean = ScageProperties.booleanProperty(key)
}