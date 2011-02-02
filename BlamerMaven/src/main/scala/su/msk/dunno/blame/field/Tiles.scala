package su.msk.dunno.blame.field.tiles

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.scage.single.support.messages.ScageMessage

private trait ColorChanger {
  protected var color = WHITE
  def changeColorState(state:State) = {
    if(state.contains("color")) color = state.getColor("color")
  }
}

class Wall(x:Int, y:Int) {
  FieldTracer.addTrace(new FieldObject(x, y) with ColorChanger {
    def getSymbol = WALL
    def getColor = color
    def isTransparent = false
    def isPassable = false

    def getState = new State("tile").put("wall").put("name", ScageMessage.xml("tile.wall"))
    def changeState(s:State) = changeColorState(s)
  })
}

class Floor(x:Int, y:Int) {
  FieldTracer.addTrace(new FieldObject(x, y) with ColorChanger {
    def getSymbol = FLOOR
    def getColor = color
    def isTransparent = true
    def isPassable = true

    def getState = new State("tile").put("floor").put("name", ScageMessage.xml("tile.floor"))
    def changeState(s:State) = changeColorState(s)
  })
}

class Door(x:Int, y:Int) {
  private var is_open = false

  FieldTracer.addTrace(new FieldObject(x, y) with ColorChanger {
    def getSymbol = if(is_open) DOOR_OPEN else DOOR_CLOSE
    def getColor = color
    def isTransparent = is_open
    def isPassable = is_open

    def getState = new State("tile").put("door")
                   .put(if(is_open) "open" else "close")
                   .put("name", if(is_open) ScageMessage.xml("tile.door.open")
                                else ScageMessage.xml("tile.door.close"))
    def changeState(s:State) = {
      changeColorState(s)
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
