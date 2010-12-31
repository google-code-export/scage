package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.scage.support.{Vec, ScageColor}
import su.msk.dunno.screens.support.tracer.{Trace, Tracer, State}

class Item(val name:String,
           val description:String,
           private val symbol:Int,
           private val color:ScageColor)
extends FieldObject with HaveStats {
  setStat("item")
  setStat("name", name)
  setStat("description", description)

  private val _point = Vec(0,0)

  override def getPoint = _point
  def getCoord(tracer:Tracer[FieldObject]) = tracer.pointCenter(_point)
  def getSymbol = symbol
  def getColor = color
  def isTransparent = true
  def isPassable = true

  def getState = stats
  def changeState(s:State) = {
    if(s.contains("point")) _point is s.getVec("point")
  }
}