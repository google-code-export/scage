package helloworld

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.Renderer

object HelloWorld {
  Renderer.addInterfaceElement(() => Message.print("Hello World!", 400, 300))

  def main(args:Array[String]):Unit = start
}