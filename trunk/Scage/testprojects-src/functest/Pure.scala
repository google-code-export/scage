package functest

import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.{Vec, ScageLibrary}
import su.msk.dunno.scage.handlers.{Renderer, Controller2}

object Pure extends Application with ScageLibrary {
  properties = "pure-properties.txt"

  private val up = Controller2.addKey(Keyboard.KEY_UP)
  def moveUpTimes = Vec(0, 1)*up.numPressed

  private val down = Controller2.addKey(Keyboard.KEY_DOWN)
  def moveDownTimes = Vec(0, -1)*down.numPressed

  private val right = Controller2.addKey(Keyboard.KEY_RIGHT)
  def moveRightTimes = Vec(1, 0)*right.numPressed

  private val left = Controller2.addKey(Keyboard.KEY_LEFT)
  def moveLeftTimes = Vec(-1, 0)*left.numPressed

  val init_coord = Vec(100, 100)
  def coord = init_coord + moveUpTimes + moveDownTimes + moveRightTimes + moveLeftTimes

  Renderer.addRender(() => {
    Renderer.setColor(BLACK)
    Renderer.drawCircle(coord, 5)
  })

  start
}