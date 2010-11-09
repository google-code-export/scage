package su.msk.dunno.blame.tiles.tiles

import su.msk.dunno.blame.tiles.{FieldObject, FieldTracer}
import su.msk.dunno.blame.support.MyFont
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.scage.support.Colors._

class Wall(x:Int, y:Int, fieldTracer:FieldTracer) {
  val coord = fieldTracer.pointCenter(x, y)

  fieldTracer.addTrace(new FieldObject {
    def getCoord = coord
    def getSymbol = MyFont.WALL
    def getColor = WHITE
    def isTransparent = false
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })
}

class Floor(x:Int, y:Int, fieldTracer:FieldTracer) {
  val coord = fieldTracer.pointCenter(x, y)

  fieldTracer.addTrace(new FieldObject {
    def getCoord = coord
    def getSymbol = MyFont.FLOOR
    def getColor = WHITE
    def isTransparent = true
    def isPassable = true

    def getState = new State
    def changeState(s:State) = {}
  })
}
