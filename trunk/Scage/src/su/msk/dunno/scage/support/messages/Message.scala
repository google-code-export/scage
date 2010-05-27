package su.msk.dunno.scage.support.messages

import su.msk.dunno.scage.support.{Color, Vec}

object Message {
  def print(message:Any, x:Float, y:Float, color:Color) = {
    TrueTypeFont.instance.drawString(message.toString, x, y, color)
  }
  def print(message:Any, x:Float, y:Float) = {
    TrueTypeFont.instance.drawString(message.toString, x, y, Color.BLACK)
  }
  def print(message:Any, coord:Vec, color:Color) = {
    TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, color)
  }
  def print(message:Any, coord:Vec) = {
    TrueTypeFont.instance.drawString(message.toString, coord.x, coord.y, Color.BLACK)
  }
}