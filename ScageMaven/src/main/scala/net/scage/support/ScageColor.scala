package net.scage.support

class ScageColor(val name:String, r:Float, g:Float, b:Float) {
  def this(r:Float, g:Float, b:Float) {this("Color", r, g, b)}
  def this(c:org.newdawn.slick.Color) {this(c.r, c.g, c.b)}
  val red:Float   = if(r >= 0 && r <= 1) r else if(r > 1 && r < 256) r/256 else -1
  val green:Float = if(g >= 0 && g <= 1) g else if(g > 1 && g < 256) g/256 else -1
  val blue:Float  = if(b >= 0 && b <= 1) b else if(b > 1 && b < 256) b/256 else -1

  def ==(other_color:ScageColor) =
    red   == other_color.red &&
    green == other_color.green &&
    blue  == other_color.blue

  def toSlickColor = new org.newdawn.slick.Color(r, g, b)

  override def toString = name+"(red="+red+" green="+green+" blue="+blue+")"
}