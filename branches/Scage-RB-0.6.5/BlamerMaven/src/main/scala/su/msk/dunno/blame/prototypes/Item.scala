package su.msk.dunno.blame.prototypes

import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.field.FieldObject
import su.msk.dunno.scage.single.support.{Vec, ScageColor}

class Item(val name:String,
           val description:String,
           private val symbol:Int,
           private val color:ScageColor)
extends FieldObject(Vec(0,0)) with HaveStats {
  setStat("item")
  setStat("name", name)
  setStat("description", description)

  def getSymbol = symbol
  def getColor = color
  def isTransparent = true
  def isPassable = true

  def getState = stats
  def changeState(s:State) = {
    if(s.contains("point")) point is s.getVec("point")
  }
}