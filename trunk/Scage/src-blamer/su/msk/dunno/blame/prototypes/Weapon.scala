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

private class BasePart extends Item(
  name = "Base Part",
  description = "The unremovable part of the weapon",
  symbol = BULLET,
  color = WHITE
) {
  setStat("base")
}

class Weapon(val owner:Living) extends PointTracer[FieldObject] (
  field_from_x = property("weapon.from.x", 0),
  field_to_x = property("weapon.to.x", 800),
  field_from_y = property("weapon.from.y", 0),
  field_to_y = property("weapon.to.y", 600),
  N_x = property("weapon.N_x", 16),
  N_y = property("weapon.N_y", 12),
  are_solid_edges = true
) {
  private def removeAllTracesFromPoint(point:Vec) = {
    if(isPointOnArea(point)) {
      coord_matrix(point.ix)(point.iy) = Nil
    }
  }

  protected def init = {
    for(x <- N_x/2-2 to N_x/2+2) {
      for(y <- N_y/2-2 to N_y/2+2) {
        val cur_point = Vec(x,y)
        removeAllTracesFromPoint(cur_point)
        addTrace({
          val fs = new BasePart
          fs.changeState(new State("point", cur_point))
          fs
        })
        addSockets(cur_point)
      }
    }
  }
  init

  private def addSockets(point:Vec) = {
    val points = List(Vec(-1,0)+point, Vec(1,0)+point, Vec(0,-1)+point, Vec(0,1)+point)
    for(cur_point <- points) {
      if(isPointOnArea(cur_point)) {
        val objects_at_point = coord_matrix(cur_point.ix)(cur_point.iy)
        if(objects_at_point.isEmpty) {
          addTrace({
            val fs = new FreeSocket
            fs.changeState(new State("point", cur_point))
            fs
          })
        }
      }
    }
  }

  private def removeSockets(point:Vec):Unit = {
    val points = List(Vec(-1,0)+point, Vec(1,0)+point, Vec(0,-1)+point, Vec(0,1)+point)
    for(cur_point <- points) {
      if(isPointOnArea(cur_point) && isNoExtenderOrBasePartNear(cur_point)) {
        coord_matrix(cur_point.ix)(cur_point.iy).filterNot(_.getState.contains("restricted")).foreach(item => {
          removeTraceFromPoint(item.id, item.getPoint)
          if(!item.getState.contains("socket")) owner.inventory.addItem(item)
          if(item.getState.contains("extender")) removeSockets(item.getPoint)
        })
      }
    }
  }

  private def isNoExtenderOrBasePartNear(point:Vec) = {
    val point1 = checkPointEdges(point + Vec(-1,0))
    val point2 = checkPointEdges(point + Vec(1,0))
    val point3 = checkPointEdges(point + Vec(0,-1))
    val point4 = checkPointEdges(point + Vec(0,1))

    val items:List[FieldObject] = coord_matrix(point1.ix)(point1.iy) :::
                                  coord_matrix(point2.ix)(point2.iy) :::
                                  coord_matrix(point3.ix)(point3.iy) :::
                                  coord_matrix(point4.ix)(point4.iy)
    !items.exists(item => item.getState.contains("extender") || item.getState.contains("base"))
  }

  private lazy val weapon_screen = new ScageScreen("Weapon Screen") {
    private var cursor = new Vec(N_x/2, N_y/2)
    private var is_show_cursor = false
    private def moveCursor(delta:Vec) = {
      is_show_cursor = true
      val new_point = cursor + delta
      if(isPointOnArea(new_point)) cursor is new_point
    }

    center = Vec((field_to_x - field_from_x)/2,
                 (field_to_y - field_from_y)/2)
    addRender(new ScageRender {
      override def render = {
        for(x <- 0 to N_x-1) {
          for(y <- 0 to N_y-1) {
            val coord = pointCenter(x, y)
            if(coord_matrix(x)(y).length > 0) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glPushMatrix();
                Renderer.color = coord_matrix(x)(y).head.getColor
                GL11.glTranslatef(coord.x, coord.y, 0.0f);
                GL11.glRectf(-h_x/2+1, -h_y/2+1, h_x/2-1, h_y/2-1);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            if(is_show_cursor && cursor == Vec(x,y)) {
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

      override def interface ={
        ScageMessage.print(ScageMessage.xml("weapon.ownership", owner.stat("name")), 20, Renderer.height-20)
      }
    })

    keyListener(Keyboard.KEY_UP, onKeyDown = {
      moveCursor(Vec(0,1))
    })
    keyListener(Keyboard.KEY_DOWN, onKeyDown = {
      moveCursor(Vec(0,-1))
    })
    keyListener(Keyboard.KEY_RIGHT, onKeyDown = {
      moveCursor(Vec(1,0))
    })
    keyListener(Keyboard.KEY_LEFT, onKeyDown = {
      moveCursor(Vec(-1,0))
    })
    keyListener(Keyboard.KEY_RETURN, onKeyDown = {
      val objects_at_cursor = coord_matrix(cursor.ix)(cursor.iy)
      if(objects_at_cursor.exists(item => item.getState.contains("socket"))) {
        objects_at_cursor.find(!_.getState.contains("socket")) match {
          case Some(item) => {
            removeTraceFromPoint(item.id, cursor)
            if(item.getState.contains("extender")) removeSockets(item.getPoint)
            owner.inventory.addItem(item)
          }
          case None => {
            owner.inventory.selectItem match {
              case Some(item) => {
                owner.inventory.removeItem(item)
                addPointTrace({
                  item.changeState(new State("point", cursor))
                  item
                })
                if(item.getState.contains("extender")) addSockets(item.getPoint)
              }
              case None =>
            }
          }
        }
      }
    })
    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
      if(is_show_cursor) is_show_cursor = false
      else stop
    })
  }
  def showWeapon = weapon_screen.run
}