package net.scage.support.messages

import _root_.net.scage.handlers.Renderer._
import _root_.net.scage.support.ScageProperties._
import net.scage.support.messages.unicode.UnicodeFont
import net.scage.support.{Vec, ScageColor}
import com.weiglewilczek.slf4s.Logger

class ScageMessage(
  val fonts_base:String    = property("fonts.base", "resources/fonts/"),
  val font_file:String     = property("font.file", "DroidSans.ttf"),
  val font_size:Int        = property("font.size", 18),
  val glyph_from:Int       = property("glyph.from", 1024),
  val glyph_to:Int         = property("glyph.to", 1279)
) {
  private val log = Logger(this.getClass.getName)

  private val font = try {
    new UnicodeFont(fonts_base+font_file, font_size, glyph_from, glyph_to)
  } catch {
    case e:Exception => {
      log.error("failed to create font:\n"+e.getLocalizedMessage)
      log.error("please provide the path to some unicode ttf font")
      System.exit(1)
      null
    }
  }

  def print(message:Any, x:Float, y:Float, color:ScageColor) {
    font.drawString(x, y, message.toString, new org.newdawn.slick.Color(color.red, color.green, color.blue))
  }
  def print(message:Any, x:Float, y:Float) {
    print(message, x, y, color)
  }
  def print(message:Any, coord:Vec, color:ScageColor) {
    font.drawString(coord.x, coord.y, message.toString, new org.newdawn.slick.Color(color.red, color.green, color.blue))
  }
  def print(message:Any, coord:Vec) {
    print(message, coord, color)
  }
}

object ScageMessage extends ScageMessage (
  fonts_base    = property("fonts.base", "resources/fonts/"),
  font_file     = property("font.file", "DroidSans.ttf"),
  font_size     = property("font.size", 18),
  glyph_from    = property("glyph.from", 1024),
  glyph_to      = property("glyph.to", 1279)
)