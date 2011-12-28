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

  def print(message:Any, x:Float, y:Float, color:ScageColor = DEFAULT_COLOR) {
    val draw_color = if(color != DEFAULT_COLOR) color else currentColor
    font.drawString(x, y, message.toString, new org.newdawn.slick.Color(draw_color.red, draw_color.green, draw_color.blue))
  }
  def print(message:Any, coord:Vec, color:ScageColor = DEFAULT_COLOR) {
    val draw_color = if(color != DEFAULT_COLOR) color else currentColor
    font.drawString(coord.x, coord.y, message.toString, new org.newdawn.slick.Color(draw_color.red, draw_color.green, draw_color.blue))
  }
  def printStrings(messages:TraversableOnce[Any], x:Float, y:Float, x_interval:Float = 0, y_interval:Float = -20, color:ScageColor = DEFAULT_COLOR) {
    var x_pos = x
    var y_pos = y
    for(message <- messages) {
      print(message, x_pos, y_pos, color)
      x_pos += x_interval
      y_pos += y_interval
    }
  }
  def printInterface(messages:TraversableOnce[MessageData], x:Float = -1, y:Float = -1, x_interval:Float = 0, y_interval:Float = -20, color:ScageColor = DEFAULT_COLOR) {
    var x_pos = x
    var y_pos = y
    for(MessageData(message, message_x, message_y) <- messages) {
      val print_x = if(message_x != -1) message_x else x_pos  // priority to coords inside xml
      val print_y = if(message_y != -1) message_y else y_pos
      print(message, print_x, print_y, color)
      x_pos += x_interval
      y_pos += y_interval
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