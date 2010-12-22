package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.field.FieldObject
import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.handlers.Renderer
import org.lwjgl.input.Keyboard
import su.msk.dunno.screens.support.ScageLibrary._
import su.msk.dunno.screens.support.tracer.State

class RestrictedPlace extends FieldObject {
  def getCoord = Vec(0,0)
  def getColor = GRAY
  def getState = new State("restricted")
  def changeState(s:State) = {}
}

class FreeSocket extends FieldObject {
  def getCoord = Vec(0,0)
  def getColor = DARK_GRAY
  def getState = new State("free")
  def changeState(s:State) = {}
}

class Weapon(val owner:Living) {
  protected var socket_matrix = Array.ofDim[List[FieldObject]](20, 20)
  (0 to 19).foreachpair(0 to 19) ((i, j) => socket_matrix(i)(j) = Nil)

  def showWeapon = weapon_screen.run

  private lazy val weapon_screen = new ScageScreen("Weapon Screen") {
    addRender(new ScageRender {
      override def interface ={
        ScageMessage.print(ScageMessage.xml("weapon.ownership", owner.stat("name")), 20, Renderer.height-20)
      }
    })

    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)
  }
}