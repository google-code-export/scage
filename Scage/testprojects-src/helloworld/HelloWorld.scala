package helloworld

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.Vec

object HelloWorld {
  var coord = Vec(0, 0)
  var center = Vec(400,300)
  var x = Vec(100, 0)
  var steps = (math.random*200).toInt
  var rotate_speed:Float = 1
  
  action(50) {
    x = x.rotateDeg(rotate_speed)
    coord = center + x
    steps -= 1
    if(steps < 0) {
      center = new Vec(math.random*width, math.random*height)
      steps = (math.random*200).toInt
      x = coord - center
      rotate_speed = 100/x.norma
    }
  }

  Renderer.interface(() => Message.print("Hello World! "+steps, coord))

  def main(args:Array[String]):Unit = run
}