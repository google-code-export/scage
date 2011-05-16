package su.msk.dunno.scage.screens.support.input

import swing.{Frame, Button, MainFrame, SimpleSwingApplication}

object ScageUserInput {
  private val frame = new Frame {
    override def closeOperation() { visible = false }

    contents = new Button {
      text = "Click me"
    }
  }
  println(frame)

  def input(input_title:String) = {
    frame.title = input_title
    frame.visible = true
    "test test"
  }
}