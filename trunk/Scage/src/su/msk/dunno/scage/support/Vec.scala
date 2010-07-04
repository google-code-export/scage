package su.msk.dunno.scage.support

import net.phys2d.math.{ROVector2f}

case class Vec(val x:Float, val y:Float) {
  def this(v:ROVector2f) = this(v.getX, v.getY)
  def this(x:Double, y:Double) = this(x.toFloat, y.toFloat)
  def this(x:Int, y:Int) = this(x.toFloat, y.toFloat)
  def this() = this(0,0)

  def +(v:Vec) = Vec(x+v.x, y+v.y)
  def -(v:Vec) = Vec(x-v.x, y-v.y)

  def *(v:Vec) = x*v.x + y*v.y
  def *(k:Float) = Vec(x*k, y*k)
  def *(k:Int) = Vec(x*k, y*k)

  def /(k:Float) = if(k == 0)Vec(x*1000, y*1000) else Vec(x/k, y/k)
  def /(k:Double):Float = this/k.toFloat
  def /(k:Int):Float = this/k.toFloat

  def norma2():Float = x*x + y*y
  def norma() = Math.sqrt(norma2).toFloat
  def n() = this/norma

  def dist2(v:Vec) = (x - v.x)*(x - v.x) + (y - v.y)*(y - v.y)
  def dist(v:Vec) = Math.sqrt(dist2(v)).toFloat

  def notZero() = x != 0 || y != 0
  def ==(v:Vec) = x == v.x && y == v.y
  def !=(v:Vec) = null == v || x != v.x || y != v.y

  def ang(v:Vec) = (180/Math.Pi*Math.acos(n * v.n)).toFloat
  def rotate(ang:Double) = Vec((x * Math.cos(ang) - y * Math.sin(ang)).toFloat,
                               (x * Math.sin(ang) + y * Math.cos(ang)).toFloat)

  def ::(o:Vec) = o :: List[Vec](this)

  override def toString() = x.toInt+" : "+y.toInt
}