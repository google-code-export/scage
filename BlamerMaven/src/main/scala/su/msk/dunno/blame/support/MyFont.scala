package su.msk.dunno.blame.support

import su.msk.dunno.scage.screens.handlers.Renderer
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageProperties._
import org.apache.log4j.Logger

object MyFont {
  private val log = Logger.getLogger(this.getClass)
  log.info("generating new ingame font...")

  val font_file = "resources/images/font.png"
  log.info("image file for the font is "+font_file)
  private val symbol_width = property("symbol.width", 32)
  private val symbol_height = property("symbol.height", 32)

  val WALL             = symbol('#')
  val FLOOR            = symbol('.')
  val PLAYER           = symbol('@')
  val SILICON_CREATURE = symbol('S')
  val DOOR_CLOSE       = symbol('+')
  val DOOR_OPEN        = symbol('\'')
  val MINOR_SELECTOR   = symbol('x')
  val MAIN_SELECTOR    = symbol('X')
  val BULLET           = symbol('*')
  val CORPSE           = symbol('%')

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

  def symbol(ch:Char) = {
    val char_coord = charCoord(ch)
    log.info("creating display list for symbol "+ch)
    Renderer.createDisplayList(font_file, symbol_width, symbol_height, char_coord.ix, char_coord.iy, 32, 32)
  }
}
