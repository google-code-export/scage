package su.msk.dunno.scage.single.support

class ScageColor(r:Float, g:Float, b:Float) {
  val red:Float = if(r >= 0 && r <= 1)r else if(r > 1 && r < 256)r/256 else 0
  val green:Float = if(g >= 0 && g <= 1)g else if(g > 1 && g < 256)g/256 else 0
  val blue:Float = if(b >= 0 && b <= 1)b else if(b > 1 && b < 256)b/256 else 0

  override def toString = "{red="+red+" green="+green+" blue="+blue+"}"
}