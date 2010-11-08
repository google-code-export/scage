package su.msk.dunno.blame.support

import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.ScageProperties._

object MyFont {
  val font_file = "img/font.png"
  val WALL = symbol('#')
  val FLOOR = symbol('.')

  private def charCoord(ch:Char):Vec = {
    val code = ch.toInt
    if(code >= 32 && code <= 47)        Vec((code - 32) *32, 2*32)
    else if(code >= 48 && code <= 63)   Vec((code - 48) *32, 3*32)
    else if(code >= 64 && code <= 79)   Vec((code - 64) *32, 4*32)
    else if(code >= 80 && code <= 95)   Vec((code - 80) *32, 5*32)
    else if(code >= 96 && code <= 111)  Vec((code - 96) *32, 6*32)
    else if(code >= 112 && code <= 126) Vec((code - 112)*32, 7*32)
    else Vec(480, 96) // coord for symbol '?'
  }

  private val symbol_width = property("symbol_width", 32)
  private val symbol_height = property("symbol_height", 32)
  private def symbol(ch:Char) = {
    val char_coord = charCoord(ch)
    Renderer.createDisplayList(font_file, symbol_width, symbol_height, char_coord.ix, char_coord.iy, 32, 32)
  }
}