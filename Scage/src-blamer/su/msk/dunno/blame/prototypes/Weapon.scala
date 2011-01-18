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

private class RestrictedPlace extends Item(
  name = "Restricted Place",
  description = "The place in weapon that is restricted to use",
  symbol = BULLET,
  color = DARK_GRAY
) {
  setStat("restricted")
}

private class FreeSocket extends Item(
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

class WeaponTracer(val owner:Living) extends PointTracer[FieldObject] (
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
            val coord = pointCenter(x, y)
            if(coord_matrix(x)(y).length > 0) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glPushMatrix();
                Renderer.color = coord_matrix(x)(y).last.getColor
                GL11.glTranslatef(coord.x, coord.y, 0.0f);
                GL11.glRectf(-h_x/2+1, -h_y/2+1, h_x/2-1, h_y/2-1);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            if(is_show_cursor && cursor.getPoint == Vec(x,y)) {
              GL11.glDisable(GL11.GL_TEXTURE_2D);
              GL11.glPushMatrix();
              Renderer.color = YELLOW
              GL11.glTranslatef(coord.x, coord.y, 0.0f);
              GL11.glBegin(GL11.GL_LINE_LOOP)
                GL11.glVertex2f(-h_x/2, -h_y/2)
                GL11.glVertex2f(-h_x/2, h_y/2)
                GL11.glVertex2f(h_x/2, h_y/2)
                GL11.glVertex2f(h_x/2, -h_y/2)
              GL11.glEnd
              GL11.glPopMatrix();
              GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
          }
       }
  }
  def moveCursor(delta:Vec) = {
    is_show_cursor = true
    val new_point = cursor.getPoint + delta
    if(this.isPointOnArea(new_point)) cursor.changeState(new State("point", new_point))
  }
  def disableCursor = is_show_cursor = false

  def objectsAtCursor = coord_matrix(cursor.getPoint.ix)(cursor.getPoint.iy)
  def removeItemAtCursor(item:FieldObject) = {
    val cursor_point = cursor.getPoint
    coord_matrix(cursor_point.ix)(cursor_point.iy) = coord_matrix(cursor_point.ix)(cursor_point.iy).filterNot(_.id == item.id)
    owner.inventory.addItem(item)
  }
  def insertItemAtCursor = {
    val cursor_point = cursor.getPoint
    owner.inventory.selectItem match {
      case Some(item) => {
        coord_matrix(cursor_point.ix)(cursor_point.iy) = coord_matrix(cursor_point.ix)(cursor_point.iy) ::: List(item)
        owner.inventory.removeItem(item)
      }
      case None =>
    }
  }
}

class Weapon(val owner:Living) {
  private val weapon_tracer = new WeaponTracer(owner)
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
    keyListener(Keyboard.KEY_RETURN, onKeyDown = {
      weapon_tracer.objectsAtCursor.find(item => {
        !item.getState.contains("socket") && !item.getState.contains("restricted")
      }) match {
        case Some(item) => weapon_tracer.removeItemAtCursor(item)
        case None => weapon_tracer.insertItemAtCursor
        }
      }
    )
    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
      if(weapon_tracer.isShowCursor) weapon_tracer.disableCursor
      else stop
    })
  }
  def showWeapon = weapon_screen.run
}