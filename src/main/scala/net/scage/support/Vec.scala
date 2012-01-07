package net.scage.support

import _root_.net.phys2d.math.{ROVector2f, Vector2f}

case class Vec(private var _x:Float, private var _y:Float) {
  def x = _x
  def y = _y

  def ix = _x.toInt
  def iy = _y.toInt

  def this(v:Vec) = this(v.x, v.y)
  def this(v:ROVector2f) = this(v.getX, v.getY)
  def this(x:Double, y:Double) = this(x.toFloat, y.toFloat)
  def this(x:Int, y:Int) = this(x.toFloat, y.toFloat)
  def this() = this(0,0)

  def +(v:Vec) = Vec(x+v.x, y+v.y)
  def -(v:Vec) = Vec(x-v.x, y-v.y)

  def *(v:Vec) = x*v.x + y*v.y
  def *(k:Float) = Vec(x*k, y*k)
  def *(k:Int) = Vec(x*k, y*k)

  def **(v:Vec) = Vec(x*v.x, y*v.y)

  def /(k:Float) = if(k == 0) Vec(x*1000, y*1000) else Vec(x/k, y/k)
  def /(k:Double):Float = this / k.toFloat
  def /(k:Int):Float = this / k.toFloat

  def norma2:Float = x*x + y*y
  def norma = math.sqrt(norma2).toFloat
  def n = this/norma

  def dist2(v:Vec) = (x - v.x)*(x - v.x) + (y - v.y)*(y - v.y)
  def dist(v:Vec) = math.sqrt(dist2(v)).toFloat

  def notZero() = x != 0 || y != 0
  def isZero = _x == 0 && _y == 0
  def ==(v:Vec) = x == v.x && y == v.y
  def !=(v:Vec) = null == v || x != v.x || y != v.y

  def deg(v:Vec) = (180/math.Pi*math.acos(n * v.n)).toFloat
  def rad(v:Vec) = (math.acos(n * v.n)).toFloat
  def rotateRad(ang:Double) = Vec((x * math.cos(ang) - y * math.sin(ang)).toFloat,
                                  (x * math.sin(ang) + y * math.cos(ang)).toFloat)
  def rotate(ang:Double) = rotateRad(ang)
  def rotateDeg(ang:Double) = rotateRad(ang/180*math.Pi)

  def ::(o:Vec) = o :: List[Vec](this)

  def copy = new Vec(x, y)

  def toPhys2dVec = new Vector2f(x, y)

  override def toString = "{"+x+" : "+y+"}"
}