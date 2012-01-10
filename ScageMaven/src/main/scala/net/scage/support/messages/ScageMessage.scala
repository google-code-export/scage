package net.scage.support.messages

import _root_.net.scage.handlers.Renderer._
import _root_.net.scage.support.ScageProperties._
import net.scage.support.messages.unicode.UnicodeFont
import com.weiglewilczek.slf4s.Logger
import net.scage.support.{Vec, ScageColor}
import net.scage.support.ScageColors._

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
    val print_color = if(color != DEFAULT_COLOR) color.toSlickColor else currentColor.toSlickColor
    font.drawString(x, y, message.toString, print_color)
  }
  def print(message:Any, x:Float, y:Float) {print(message, x, y, currentColor)}
  def print(message:Any, coord:Vec, color:ScageColor) {
    val print_color = if(color != DEFAULT_COLOR) color.toSlickColor else currentColor.toSlickColor
    font.drawString(coord.x, coord.y, message.toString, new org.newdawn.slick.Color(color.red, color.green, color.blue))
  }
  def print(message:Any, coord:Vec) {print(message, coord, currentColor)}
  def printStrings(messages:TraversableOnce[Any], x:Float, y:Float, x_interval:Float = 0, y_interval:Float = -20, color:ScageColor = DEFAULT_COLOR) {
    var x_pos = x
    var y_pos = y
    for(message <- messages) {
      print(message, x_pos, y_pos, color)
      x_pos += x_interval
      y_pos += y_interval
    }
  }
  def printInterface(interface_id:String, parameters:Any*) {
    val messages = ScageXML.xmlInterface(interface_id, parameters:_*)
    for(MessageData(message, message_x, message_y, message_color) <- messages) {
      print(message, message_x, message_y, message_color)
    }    
  }
}

object ScageMessage extends ScageMessage (
  fonts_base    = property("fonts.base", "resources/fonts/"),
  font_file     = property("font.file", "DroidSans.ttf"),
  font_size     = property("font.size", 18),
  glyph_from    = property("glyph.from", 1024),
  glyph_to      = property("glyph.to", 1279)
)