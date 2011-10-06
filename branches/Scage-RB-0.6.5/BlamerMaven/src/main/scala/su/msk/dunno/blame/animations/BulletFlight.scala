package su.msk.dunno.blame.animations

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.scage.single.support.{ScageColor, Vec}
import su.msk.dunno.scage.screens.prototypes.{ScageAction, ScageRender}
import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.screens.support.ScageLibrary._
import su.msk.dunno.scage.screens.handlers.Renderer

class BulletFlight(val start_point:Vec, val end_point:Vec, val color:ScageColor, val delay:Long = property("animation.bulletflight.delay", 30.toLong))
extends ScageScreen("Bullet Flight") {
  private val current_point = start_point.copy
  private val line = FieldTracer.line(start_point, end_point)

  val trace = FieldTracer.addTrace(new FieldObject(current_point) {
    def getSymbol = BULLET
    def getColor = color
    def isTransparent = true
    def isPassable = true

    def getState = new State
    def changeState(s:State) = {}
  })

  FieldTracer.addLightSource(current_point, 5, trace)

  private var count = 0
  private var last_move_time = System.currentTimeMillis
  addAction(new ScageAction {
    override def action = {
      if(System.currentTimeMillis - last_move_time > delay) {
        if(count < line.length-1) {
          FieldTracer.updatePointLocation(trace, current_point, line({count+=1; count}))
          last_move_time = System.currentTimeMillis
        }
        else stop
      }
    }

    override def exit = {
      FieldTracer.removeTraceFromPoint(trace, current_point)
    }
  })

  // render on main screen
  windowCenter = Vec((width - Blamer.right_messages_width)/2, 
  		     BottomMessages.bottom_messages_height + (height - BottomMessages.bottom_messages_height)/2)
  center = FieldTracer.pointCenter(Blamer.currentPlayer.getPoint)

  Renderer.backgroundColor = BLACK  

  addRender(new ScageRender {
    override def render = FieldTracer.drawField(Blamer.currentPlayer.getPoint)

    override def interface {
      BottomMessages.showBottomMessages(0)
      Blamer.drawInterface
    }
  })

  run
}
