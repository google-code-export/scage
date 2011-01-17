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
import org.lwjgl.opengl.GL11

class RestrictedPlace extends Item(
  name = "Restricted Place",
  description = "The place in weapon that is restricted to use",
  symbol = BULLET,
  color = DARK_GRAY
) {
  setStat("restricted")
}

class FreeSocket extends Item(
  name = "Free Socket",
  description = "The socket of the weapon that can be used to insert some imp",
  symbol = BULLET,
  color = GRAY
) {
  setStat("socket")
}

private class WeaponCursor extends Item(
  name = "Weapon Cursor",
  description = "Element to navigate through weapon",
  symbol = MAIN_SELECTOR,
  color = WHITE
) {
  setStat("cursor")
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

  def init = {
    for(x <- 0 to N_x-1) {
      for(y <- 0 to N_y-1) {
        addTrace({
          val fs = new FreeSocket
          fs.changeState(new State("point", Vec(x, y)))
          fs
        })
      }
    }
  }
  init

  private val cursor = new WeaponCursor
  private var is_show_cursor = false
  def isShowCursor = is_show_cursor
  def drawWeapon = {
    for(x <- 0 to N_x-1) {
          for(y <- 0 to N_y-1) {
            if(is_show_cursor && cursor.getPoint == Vec(x,y)) cursor.draw(this)
            else {
              if(coord_matrix(x)(y).length > 0) {
                val coord = pointCenter(x, y)
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glPushMatrix();
                Renderer.color = coord_matrix(x)(y).last.getColor
                GL11.glTranslatef(coord.x, coord.y, 0.0f);
                GL11.glRectf(-h_x/2+0.5f, -h_y/2+0.5f, h_x/2-0.5f, h_y/2-0.5f);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
              }
            }
          }
       }
  }
  def moveCursor(delta:Vec) = {
    is_show_cursor = true
    cursor.changeState(new State("point", cursor.getPoint+delta))
  }
  def disableCursor = is_show_cursor = false
}

class Weapon(owner:Living) {
  private val weapon_tracer = new WeaponTracer
  private lazy val weapon_screen = new ScageScreen("Weapon Screen") {
    center = Vec((weapon_tracer.field_to_x - weapon_tracer.field_from_x)/2,
                 (weapon_tracer.field_to_y - weapon_tracer.field_from_y)/2)
    addRender(new ScageRender {
      override def render = weapon_tracer.drawWeapon

      override def interface ={
        ScageMessage.print(ScageMessage.xml("weapon.ownership", owner.stat("name")), 20, Renderer.height-20)
      }
    })

    keyListener(Keyboard.KEY_UP, onKeyDown = {
      weapon_tracer.moveCursor(Vec(0,1))
    })
    keyListener(Keyboard.KEY_DOWN, onKeyDown = {
      weapon_tracer.moveCursor(Vec(0,-1))
    })
    keyListener(Keyboard.KEY_RIGHT, onKeyDown = {
      weapon_tracer.moveCursor(Vec(1,0))
    })
    keyListener(Keyboard.KEY_LEFT, onKeyDown = {
      weapon_tracer.moveCursor(Vec(-1,0))
    })
    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
      if(weapon_tracer.isShowCursor) weapon_tracer.disableCursor
      else stop
    })
  }
  def showWeapon = weapon_screen.run
}