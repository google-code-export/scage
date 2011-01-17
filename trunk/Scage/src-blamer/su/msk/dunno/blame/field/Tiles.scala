package su.msk.dunno.blame.field.tiles

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.blame.support.BottomMessages

class Wall(x:Int, y:Int) {
  FieldTracer.addTrace(new FieldObject(x, y) {
    def getSymbol = WALL
    def getColor = WHITE
    def isTransparent = false
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })
}

class Floor(x:Int, y:Int) {
  FieldTracer.addTrace(new FieldObject(x, y) {
    def getSymbol = FLOOR
    def getColor = WHITE
    def isTransparent = true
    def isPassable = true

    def getState = new State
    def changeState(s:State) = {}
  })
}

class Door(x:Int, y:Int) {
  private var is_open = false

  FieldTracer.addTrace(new FieldObject(x, y) {
    def getSymbol = if(is_open) DOOR_OPEN else DOOR_CLOSE
    def getColor = WHITE
    def isTransparent = is_open
    def isPassable = is_open

    def getState = new State("door", if(is_open) "open" else "close")
    def changeState(s:State) = {
      if(s.contains("door_open")) {
        if(!is_open) {
          is_open = true
          BottomMessages.addPropMessage("door.open", s.getString("door_open"))
        }
      }
      else if(s.contains("door_close")) {
        if(is_open) {
          is_open = false
          BottomMessages.addPropMessage("door.close", s.getString("door_close"))
        }
      }
    }
  })
}
