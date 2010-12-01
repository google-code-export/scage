package su.msk.dunno.scage.support.messages

import su.msk.dunno.scage.support.{Colors, Color, Vec}
import org.newdawn.slick.AngelCodeFont

object Message extends Colors {
  //private val font = new AngelCodeFont("fonts/tt.fnt", "fonts/tt_0.png")
  private val font = new MyFont()

  def print(message:Any, x:Float, y:Float, color:Color) = {
    //TrueTypeFont.instance.drawString(message.toString, x, y, color)
    font.drawString(x,y,message.toString, org.newdawn.slick.Color.white)
  }
  def print(message:Any, x:Float, y:Float) = {
    //TrueTypeFont.instance.drawString(message.toString, x, y, BLACK)
    font.drawString(x,y,message.toString, org.newdawn.slick.Color.white)
  }
  def print(message:Any, coord:Vec, color:Color) = {
    //TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, color)
    font.drawString(coord.x, coord.y, message.toString, org.newdawn.slick.Color.white)
  }
  def print(message:Any, coord:Vec) = {
    //TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, BLACK)
    font.drawString(coord.x, coord.y, message.toString, org.newdawn.slick.Color.white)
  }
}