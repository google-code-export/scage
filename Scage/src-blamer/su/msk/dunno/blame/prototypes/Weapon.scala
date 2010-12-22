package su.msk.dunno.blame.prototypes

import su.msk.dunno.screens.support.tracer.Tracer
import su.msk.dunno.blame.field.FieldObject
import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.handlers.Renderer

class WeaponTracer extends Tracer[FieldObject] {

}

class Weapon(val owner:Living) {
  private lazy val weapon_screen = new ScageScreen("Weapon Screen") {
    addRender(new ScageRender {
      override def interface ={
        ScageMessage.print(ScageMessage.xml("weapon.ownership", owner.stat("name")), 20, Renderer.height-20)
      }
    })
  }
}