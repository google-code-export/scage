package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.blame.prototypes.{Living}
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.screens.handlers.Renderer._
import su.msk.dunno.blame.field.FieldTracer
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.support.BottomMessages._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.scage.support.messages.ScageMessage._
import su.msk.dunno.scage.support.{Vec}

class CommandScreen(living:Living) extends ScageScreen("Command Screen") {
  def findPlayer =
    FieldTracer.findVisibleObject(living.trace, living.getPoint, living.getState.getInt("dov"), obj => {
      obj.getState.contains("player") && obj.getState.getInt("health") > 0
    })

  keyListener(Keyboard.KEY_1, onKeyDown = {
    findPlayer match {
      case Some(player) => player.changeState(new State("follow"))
      case None =>
    }
  })
  keyListener(Keyboard.KEY_2, onKeyDown = {
    findPlayer match {
      case Some(player) => player.changeState(new State("stay"))
      case None =>
    }
  })
  keyListener(Keyboard.KEY_3, onKeyDown = {
    findPlayer match {
      case Some(player) => player.changeState(new State("attack"))
      case None =>
    }
  })
  keyListener(Keyboard.KEY_4, onKeyDown = {
    findPlayer match {
      case Some(player) => player.changeState(new State("noattack"))
      case None =>
    }
  })
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)

  // render on main screen
  windowCenter = Vec((width - Blamer.right_messages_width)/2,
  		            bottom_messages_height + (height - bottom_messages_height)/2)
  center = FieldTracer.pointCenter(living.getPoint)

  backgroundColor = BLACK

  addRender(new ScageRender {
    override def render = {
      FieldTracer.drawField(Blamer.currentPlayer.getPoint)
    }

    override def interface {
      print(xml("commands.list"), 10, bottom_messages_height - (row_height*2), GREEN)
      Blamer.drawInterface
    }
  })
}