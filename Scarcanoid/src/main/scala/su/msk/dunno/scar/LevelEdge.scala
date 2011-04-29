package su.msk.dunno.scar

import su.msk.dunno.scage.screens.physics.objects.StaticLine
import su.msk.dunno.scage.single.support.Vec
import Scaranoid._
import net.phys2d.math.Vector2f
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._

class LevelEdge (from:Vec, to:Vec) extends StaticLine(from, to) {
  render {
    val verts:Array[Vector2f] = line.getVertices(body.getPosition(), body.getRotation());
    color = WHITE
    drawLine(Vec(verts(0).getX, verts(0).getY),
             Vec(verts(1).getX, verts(1).getY))
  }

  Scaranoid --> this
}