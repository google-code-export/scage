package su.msk.dunno.blame.screens

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.blame.prototypes.Living
import su.msk.dunno.scage.screens.prototypes.ScageRender
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.blame.field.FieldTracer
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.support.BottomMessages._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.Vec

class CommandScreen(living:Living) extends ScageScreen("Command Screen") {
  def findPlayer =
    FieldTracer.findVisibleObject(living.trace, living.getPoint, living.getState.getInt("dov"), obj => {
      obj.getState.contains("player") && obj.getState.getInt("health") > 0
    })

  private var command_num = -1
  def selectCommand = {
    command_num = -1
    run
    command_num
  }
  keyListener(Keyboard.KEY_1, onKeyDown = {
    command_num = 1
    stop
  })
  keyListener(Keyboard.KEY_2, onKeyDown = {
    command_num = 2
    stop
  })
  keyListener(Keyboard.KEY_3, onKeyDown = {
    command_num = 3
    stop
  })
  keyListener(Keyboard.KEY_4, onKeyDown = {
    command_num = 4
    stop
  })
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    command_num = -1
    stop
  })

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
      print(xml("commands.list"), 10, bottom_messages_height)
      print(xml("commands.helpmessage"), 10, bottom_messages_height - (row_height*5), GREEN)
      Blamer.drawInterface
    }
  })
}