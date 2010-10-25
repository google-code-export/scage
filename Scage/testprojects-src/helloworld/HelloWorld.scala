package helloworld

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.{Idler, Renderer}
import su.msk.dunno.scage.support.Vec

object HelloWorld {
  var coord = Vec(0, 0)
  val center = Vec(400,300)
  var x = Vec(100, 0)
  action(100) {
    x = x.rotateDeg(1)
    coord = center + x
  }

  Renderer.addInterfaceElement(() => Message.print("Hello World! "+Idler.fps, coord))

  def main(args:Array[String]):Unit = start
}