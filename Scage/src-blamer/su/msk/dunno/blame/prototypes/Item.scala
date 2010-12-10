package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.scage.support.{Vec, ScageColor}

class Item(val name:String, val description:String, val symbol:Int, val color:ScageColor) 
extends HaveStats {
  setStat("name", name)
  setStat("description", description)

  private val _point = Vec(0,0)
  private var _trace = -1
  private val fieldObject = new FieldObject {
    def getCoord = FieldTracer.pointCenter(_point)
    def getSymbol = symbol
    def getColor = color
    def isTransparent = true
    def isPassable = true

    def getState = stats
    def changeState(s:State) = {}
  }

  def drop(point:Vec) = {
    _point is point
    _trace = FieldTracer.addTrace(fieldObject)
  }

  def pickUp = {
    FieldTracer.removeTraceFromPoint(_trace, _point)
  }
}
