package su.msk.dunno.blame.field.tiles

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.scage.support.Colors._
class Wall(x:Int, y:Int, fieldTracer:FieldTracer) {
  private val coord = fieldTracer.pointCenter(x, y)

  fieldTracer.addTrace(new FieldObject {
    def getCoord = coord
    def getSymbol = WALL
    def getColor = WHITE
    def isTransparent = false
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })
}

class Floor(x:Int, y:Int, fieldTracer:FieldTracer) {
  private val coord = fieldTracer.pointCenter(x, y)

  fieldTracer.addTrace(new FieldObject {
    def getCoord = coord
    def getSymbol = FLOOR
    def getColor = WHITE
    def isTransparent = true
    def isPassable = true

    def getState = new State
    def changeState(s:State) = {}
  })
}

class Door(x:Int, y:Int, fieldTracer:FieldTracer) {
  private val coord = fieldTracer.pointCenter(x, y)
  private var is_open = false

  fieldTracer.addTrace(new FieldObject {
    def getCoord = coord
    def getSymbol = if(is_open) DOOR_OPEN else DOOR_CLOSE
    def getColor = WHITE
    def isTransparent = is_open
    def isPassable = is_open

    def getState = new State
    def changeState(s:State) = {
      if(s.contains("door_open")) is_open = true
      else if(s.contains("door_close")) is_open = false
    }
  })
}
