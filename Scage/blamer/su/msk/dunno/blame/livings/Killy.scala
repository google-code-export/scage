package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.MyFont
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.scage.support.Vec

class Killy(val point:Vec, fieldTracer:FieldTracer) {
  val trace = fieldTracer.addTrace(new FieldObject {
    def getCoord = fieldTracer.pointCenter(point)
    def getSymbol = MyFont.PLAYER
    def getColor = RED
    def isTransparent = true
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })

  //def move()
}