package helloworld

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import su.msk.dunno.scage.Scage

object HelloWorld {
  Renderer.addInterfaceElement(() => Message.print("Hello World! "+Idler.fps, 400, 300))

  Scage.action(5000) {
    println("ttt")
  }

  def main(args:Array[String]):Unit = start
}