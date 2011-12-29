package net.scage.support

import collection.mutable.HashMap
import com.weiglewilczek.slf4s.Logger
import org.newdawn.slick.Color

class ScageColor(r:Float, g:Float, b:Float) {       // TODO: add color name as value parameter
  def this(c:Color) {this(c.r, c.g, c.b)}
  val red:Float   = if(r >= 0 && r <= 1) r else if(r > 1 && r < 256) r/256 else -1
  val green:Float = if(g >= 0 && g <= 1) g else if(g > 1 && g < 256) g/256 else -1
  val blue:Float  = if(b >= 0 && b <= 1) b else if(b > 1 && b < 256) b/256 else -1

  def ==(other_color:ScageColor) =
    red   == other_color.red &&
    green == other_color.green &&
    blue  == other_color.blue

  def toSlickColor = new Color(r, g, b)

  override def toString = "{red="+red+" green="+green+" blue="+blue+"}"
}

object ScageColors {
  private val log = Logger(this.getClass.getName)

  val DEFAULT_COLOR = new ScageColor(-1, -1, -1)

  val RED: ScageColor = new ScageColor(1, 0, 0)
  val GREEN: ScageColor = new ScageColor(0, 1, 0)
  val BLUE: ScageColor = new ScageColor(0, 0, 1)
  val CORNFLOWER: ScageColor = new ScageColor(0.39f, 0.58f, 0.93f)
  val CYAN: ScageColor = new ScageColor(0, 1, 1)
  val YELLOW: ScageColor = new ScageColor(1, 1, 0)
  val WHITE: ScageColor = new ScageColor(1, 1, 1)
  val GRAY: ScageColor = new ScageColor(0x80, 0x80, 0x80)
  val BLACK: ScageColor = new ScageColor(0, 0, 0)

  val SNOW: ScageColor = new ScageColor(0xFF, 0xFA, 0xFA)
  val GHOSTWHITE: ScageColor = new ScageColor(0xF8, 0xF8, 0xFF)
  val ANTIQUE_WHITE: ScageColor = new ScageColor(0xFA, 0xEB, 0xD7)
  val CREAM: ScageColor = new ScageColor(0xFF, 0xFB, 0xF0)
  val PEACHPUFF: ScageColor = new ScageColor(0xFF, 0xDA, 0xB9)
  val NAVAJO_WHITE: ScageColor = new ScageColor(0xFF, 0xDE, 0xAD)
  val CORNSILK: ScageColor = new ScageColor(0xFF, 0xF8, 0xDC)
  val IVORY: ScageColor = new ScageColor(0xFF, 0xFF, 0xF0)
  val LEMON_CHIFFON: ScageColor = new ScageColor(0xFF, 0xFA, 0xCD)
  val SEASHELL: ScageColor = new ScageColor(0xFF, 0xF5, 0xEE)
  val HONEYDEW: ScageColor = new ScageColor(0xF0, 0xFF, 0xF0)
  val AZURE: ScageColor = new ScageColor(0xF0, 0xFF, 0xFF)
  val LAVENDER: ScageColor = new ScageColor(0xE6, 0xE6, 0xFA)
  val LAVENDER_BLUSH: ScageColor = new ScageColor(0xFF, 0xF0, 0xF5)
  val MISTY_ROSE: ScageColor = new ScageColor(0xFF, 0xE4, 0xE1)
  val DIM_GRAY: ScageColor = new ScageColor(0x69, 0x69, 0x69)
  val SLATE_GRAY: ScageColor = new ScageColor(0x70, 0x80, 0x90)
  val LIGHT_SLATE_GRAY: ScageColor = new ScageColor(0x77, 0x88, 0x99)
  val LIGHT_GRAY: ScageColor = new ScageColor(0xC0, 0xC0, 0xC0)
  val MEDIUM_GRAY: ScageColor = new ScageColor(0xA0, 0xA0, 0xA4)
  val DARK_GRAY: ScageColor = new ScageColor(0.3f, 0.3f, 0.3f)
  val MIDNIGHT_BLUE: ScageColor = new ScageColor(0x19, 0x19, 0x70)
  val NAVY: ScageColor = new ScageColor(0x00, 0x00, 0x80)
  val SLATE_BLUE: ScageColor = new ScageColor(0x6A, 0x5A, 0xCD)
  val LIGHT_SLATE_BLUE: ScageColor = new ScageColor(0x84, 0x70, 0xFF)
  val ROYAL_BLUE: ScageColor = new ScageColor(0x41, 0x69, 0xE1)
  val SKY_BLUE: ScageColor = new ScageColor(0x87, 0xCE, 0xEB)
  val LIGHT_SKY_BLUE: ScageColor = new ScageColor(0x87, 0xCE, 0xFA)
  val STEEL_BLUE: ScageColor = new ScageColor(0x46, 0x82, 0xB4)
  val LIGHT_STEEL_BLUE: ScageColor = new ScageColor(0xB0, 0xC4, 0xDE)
  val LIGHT_BLUE: ScageColor = new ScageColor(0xA6, 0xCA, 0xF0)
  val POWDER_BLUE: ScageColor = new ScageColor(0xB0, 0xE0, 0xE6)
  val PALE_TURQUOISE: ScageColor = new ScageColor(0xAF, 0xEE, 0xEE)
  val TURQUOISE: ScageColor = new ScageColor(0x40, 0xE0, 0xD0)
  val LIGHT_CYAN: ScageColor = new ScageColor(0xE0, 0xFF, 0xFF)
  val DARK_CYAN: ScageColor = new ScageColor(0x00, 0x80, 0x80)
  val CADET_BLUE: ScageColor = new ScageColor(0x5F, 0x9E, 0xA0)
  val AQUAMARINE: ScageColor = new ScageColor(0x7F, 0xFF, 0xD4)
  val SEAGREEN: ScageColor = new ScageColor(0x54, 0xFF, 0x9F)
  val LIGHT_SEAGREEN: ScageColor = new ScageColor(0x20, 0xB2, 0xAA)
  val PALE_GREEN: ScageColor = new ScageColor(0x98, 0xFB, 0x98)
  val SPRING_GREEN: ScageColor = new ScageColor(0x00, 0xFF, 0x7F)
  val LAWN_GREEN: ScageColor = new ScageColor(0x7C, 0xFC, 0x00)
  val MEDIUM_GREEN: ScageColor = new ScageColor(0xC0, 0xDC, 0xC0)
  val DARK_GREEN: ScageColor = new ScageColor(0x00, 0x80, 0x00)
  val CHARTREUSE: ScageColor = new ScageColor(0x7F, 0xFF, 0x00)
  val GREEN_YELLOW: ScageColor = new ScageColor(0xAD, 0xFF, 0x2F)
  val LIME_GREEN: ScageColor = new ScageColor(0x32, 0xCD, 0x32)
  val YELLOW_GREEN: ScageColor = new ScageColor(0x9A, 0xCD, 0x32)
  val FOREST_GREEN: ScageColor = new ScageColor(0x22, 0x8B, 0x22)
  val HAKI: ScageColor = new ScageColor(0xF0, 0xE6, 0x8C)
  val PALE_GOLDENROD: ScageColor = new ScageColor(0xEE, 0xE8, 0xAA)
  val LIGHT_GOLDENROD_YELLOW: ScageColor = new ScageColor(0xFA, 0xFA, 0xD2)
  val LIGHT_YELLOW: ScageColor = new ScageColor(0xFF, 0xFF, 0xE0)
  val DARK_YELLOW: ScageColor = new ScageColor(0x80, 0x80, 0x00)
  val GOLD: ScageColor = new ScageColor(0xFF, 0xD7, 0x00)
  val LIGHT_GOLDENROD: ScageColor = new ScageColor(0xFF, 0xEC, 0x8B)
  val GOLDEN_ROD: ScageColor = new ScageColor(0xDA, 0xA5, 0x20)
  val BURLY_WOOD: ScageColor = new ScageColor(0xDE, 0xB8, 0x87)
  val ROSY_BROWN: ScageColor = new ScageColor(0xBC, 0x8F, 0x8F)
  val SADDLE_BROWN: ScageColor = new ScageColor(0x8B, 0x45, 0x13)
  val SIENNA: ScageColor = new ScageColor(0xA0, 0x52, 0x2D)
  val BEIGE: ScageColor = new ScageColor(0xF5, 0xF5, 0xDC)
  val WHEAT: ScageColor = new ScageColor(0xF5, 0xDE, 0xB3)
  val TAN: ScageColor = new ScageColor(0xD2, 0xB4, 0x8C)
  val CHOCOLATE: ScageColor = new ScageColor(0xD2, 0x69, 0x1E)
  val FIREBRICK: ScageColor = new ScageColor(0xB2, 0x22, 0x22)
  val BROWN: ScageColor = new ScageColor(0xA5, 0x2A, 0x2A)
  val SALMON: ScageColor = new ScageColor(0xFA, 0x80, 0x72)
  val LIGHT_SALMON: ScageColor = new ScageColor(0xFF, 0xA0, 0x7A)
  val ORANGE: ScageColor = new ScageColor(0xFF, 0xA5, 0x00)
  val CORAL: ScageColor = new ScageColor(0xFF, 0x7F, 0x50)
  val LIGHT_CORAL: ScageColor = new ScageColor(0xF0, 0x80, 0x80)
  val ORANGE_RED: ScageColor = new ScageColor(0xFF, 0x45, 0x00)
  val DARK_RED: ScageColor = new ScageColor(0x80, 0x00, 0x00)
  val HOT_PINK: ScageColor = new ScageColor(0xFF, 0x69, 0xB4)
  val PINK: ScageColor = new ScageColor(0xFF, 0xC0, 0xCB)
  val LIGHT_PINK: ScageColor = new ScageColor(0xFF, 0xB6, 0xC1)
  val PALE_VIOLET_RED: ScageColor = new ScageColor(0xDB, 0x70, 0x93)
  val MAROON: ScageColor = new ScageColor(0xB0, 0x30, 0x60)
  val VIOLET_RED: ScageColor = new ScageColor(0xD0, 0x20, 0x90)
  val MAGENTA: ScageColor = new ScageColor(0xFF, 0x00, 0xFF)
  val DARK_MAGENTA: ScageColor = new ScageColor(0x80, 0x00, 0x80)
  val VIOLET: ScageColor = new ScageColor(0xEE, 0x82, 0xEE)
  val PLUM: ScageColor = new ScageColor(0xDD, 0xA0, 0xDD)
  val ORCHID: ScageColor = new ScageColor(0xDA, 0x70, 0xD6)
  val BLUE_VIOLET: ScageColor = new ScageColor(0x8A, 0x2B, 0xE2)
  val PURPLE: ScageColor = new ScageColor(0xA0, 0x20, 0xF0)

  private val colors = new HashMap[String, ScageColor]()
  ScageColors.getClass.getDeclaredFields.foreach(field => {
    field.setAccessible(true)
    val color = try{field.get(ScageColors).asInstanceOf[ScageColor]}
    catch {
      case ex:Exception => {
        //log.error("failed to create color with name "+field.getName+": "+ex.getLocalizedMessage)
        WHITE
      }
    }
    colors += (field.getName.toUpperCase -> color)
    field.setAccessible(false)
  })
  def colorFromString(color_string:String) = {
    colors.get(color_string.toUpperCase) match {
      case Some(color) => color
      case None => DEFAULT_COLOR
    }
  }

  def randomColor = new ScageColor(math.random.toFloat, math.random.toFloat, math.random.toFloat)
}