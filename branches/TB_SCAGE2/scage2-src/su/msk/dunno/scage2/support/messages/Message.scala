package su.msk.dunno.scage2.support.messages

import su.msk.dunno.scage2.support.{Vec, Color, Colors}

object Message extends Colors {
  def print(message:Any, x:Float, y:Float, color:Color) = {
    TrueTypeFont.instance.drawString(message.toString, x, y, color)
  }
  def print(message:Any, x:Float, y:Float) = {
    TrueTypeFont.instance.drawString(message.toString, x, y, BLACK)
  }
  def print(message:Any, coord:Vec, color:Color) = {
    TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, color)
  }
  def print(message:Any, coord:Vec) = {
    TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, BLACK)
  }
}