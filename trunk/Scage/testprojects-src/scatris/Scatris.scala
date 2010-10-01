package scatris

import figures.Square
import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard

object Scatris extends Application with ScageLibrary {
  var f = new Square(StandardTracer.pointCenter(3, 11))
  //AI.registerAI(() => if(!f.isMoving) f = new Square(StandardTracer.pointCenter(3, 12)))
  Controller.addKeyListener(Keyboard.KEY_SPACE,1000, () => new Square(StandardTracer.pointCenter(3, 12)))
  start
}