package su.msk.dunno.blame.animations

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.scage.support.{ScageColor, Vec}
import su.msk.dunno.screens.prototypes.{ScageAction, ScageRender}
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.screens.support.ScageLibrary._
import su.msk.dunno.screens.handlers.Renderer

class BulletFlight(val start_point:Vec, val end_point:Vec, val color:ScageColor, val delay:Long = 30)
extends ScageScreen("Bullet Flight") {
  private val line = FieldTracer.line(end_point, start_point).toArray
  private var count = 0

  val trace = FieldTracer.addTrace(new FieldObject {
    def getCoord = FieldTracer.pointCenter(line(count))
    def getSymbol = BULLET
    def getColor = color
    def isTransparent = true
    def isPassable = true

    def getState = new State
    def changeState(s:State) = {}
  })

  FieldTracer.addLightSource(line(count), 5, trace)

  private var last_move_time = System.currentTimeMillis
  addAction(new ScageAction {
    override def action = {
      if(System.currentTimeMillis - last_move_time > delay) {
        if(count < line.length-1) {
          FieldTracer.updatePointLocation(trace, line(count), line(count+1))
          count += 1
          last_move_time = System.currentTimeMillis
        }
        else stop
      }
    }

    override def exit = {
      FieldTracer.removeTraceFromPoint(trace, line(count))
    }
  })

  // render on main screen
  windowCenter = Vec((width - Blamer.right_messages_width)/2, 
  		     BottomMessages.bottom_messages_height + (height - BottomMessages.bottom_messages_height)/2)
  center = FieldTracer.pointCenter(Blamer.currentPlayer.point)

  Renderer.backgroundColor = BLACK  

  addRender(new ScageRender {
    override def render = FieldTracer.drawField(Blamer.currentPlayer.point)

    override def interface {
      BottomMessages.showBottomMessages
      Blamer.drawInterface
    }
  })

  run
}
