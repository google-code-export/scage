package su.msk.dunno.blame.prototypes

import su.msk.dunno.blame.field.FieldObject
import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.handlers.Renderer
import org.lwjgl.input.Keyboard
import su.msk.dunno.screens.support.ScageLibrary._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.screens.support.tracer.{PointTracer, Trace, Tracer, State}

class RestrictedPlace extends Item(
  name = "Restricted Place",
  description = "The place in weapon that is restricted to use",
  symbol = BULLET,
  color = GRAY
) {
  setStat("restricted")
}

class FreeSocket extends Item(
  name = "Free Socket",
  description = "The socket of the weapon that can be used to insert some imp",
  symbol = BULLET,
  color = DARK_GRAY
) {
  setStat("socket")
}

class WeaponTracer extends PointTracer[FieldObject] (
  field_from_x = property("weapon.from.x", 0),
  field_to_x = property("weapon.to.x", 800),
  field_from_y = property("weapon.from.y", 0),
  field_to_y = property("weapon.to.y", 600),
  N_x = property("weapon.N_x", 16),
  N_y = property("weapon.N_y", 12),
  are_solid_edges = true
) {
  override def addTrace(fo:FieldObject) = {
    val p = fo.getPoint
    if(isPointOnArea(p)) {
      coord_matrix(p.ix)(p.iy) = fo :: coord_matrix(p.ix)(p.iy)
      log.debug("added new field trace #"+fo.id+" in point ("+fo.getPoint+")")
    }
    else log.error("failed to add field trace: point ("+fo.getPoint+") is out of area")
    fo.id
  }

  def drawWeapon = {
    for(x <- 0 to N_x-1) {
      for(y <- 0 to N_y-1) {
        if(coord_matrix(x)(y).length > 0) {
          coord_matrix(x)(y).last.draw(this)
        }
      }
    }
    //System.exit(0)
  }
}

class Weapon(val owner:Living) {
  protected val weapon_tracer = new WeaponTracer
  for(x <- 0 to weapon_tracer.N_x-1) {
      for(y <- 0 to weapon_tracer.N_y-1) {
        weapon_tracer.addTrace({
          val fs = new FreeSocket
          fs.changeState(new State("point", Vec(x, y)))
/*          println(fs.getPoint)
          println(fs.getCoord)*/
          fs
        })
      }
  }

  def showWeapon = weapon_screen.run

  private lazy val weapon_screen = new ScageScreen("Weapon Screen") {
    addRender(new ScageRender {
      override def interface ={
        weapon_tracer.drawWeapon
        ScageMessage.print(ScageMessage.xml("weapon.ownership", owner.stat("name")), 20, Renderer.height-20)
      }
    })

    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)
  }
}