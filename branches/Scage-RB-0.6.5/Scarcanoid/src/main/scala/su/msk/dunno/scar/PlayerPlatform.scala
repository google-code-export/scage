package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._
import Scaranoid._
import org.lwjgl.input.Keyboard._
import net.phys2d.math.Vector2f
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.support.physics.objects.StaticBox

object PlayerPlatform extends StaticBox(Vec(screen_width/2,25), 50, 10) {
  init {
    coord = Vec(screen_width/2,25)
  }

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && coord.x > 60) move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && coord.x < screen_width-40) move(Vec(3, 0)))

  render {
    val verts:Array[Vector2f] = box.getPoints(body.getPosition(), body.getRotation());
    color = WHITE
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
}