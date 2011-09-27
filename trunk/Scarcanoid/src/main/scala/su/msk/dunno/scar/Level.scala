package su.msk.dunno.scar

import levels.{LevelMap1, LevelMap}
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._
import Scaranoid._
import su.msk.dunno.scage.single.support.ScageColors._
import net.phys2d.math.Vector2f
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.screens.support.physics.Physical
import su.msk.dunno.scage.screens.support.physics.objects.StaticBox
import su.msk.dunno.scage.single.support.ScageProperties._

object Level {
  private var boxes:List[Physical] = Nil
  def winCondition = boxes.forall(!_.isActive)

  def loadMap(level_map:LevelMap) {
    physics.removePhysicals(boxes)
    boxes = level_map.load
  }

  physics.addPhysical(
    new LevelEdge(Vec(30,              10),                Vec(30,              screen_height-10)),
    new LevelEdge(Vec(30,              screen_height-10),  Vec(screen_width-10, screen_height-10)),
    new LevelEdge(Vec(screen_width-10, screen_height-10),  Vec(screen_width-10, 10)),
    new LevelEdge(Vec(screen_width-10, 10),                Vec(30,              10)) {
      init {
        prepare()
      }

      action {
        if(isTouching(PlayerBall)) pause()
      }
    }
  )

  val additional_platform = physics.addPhysical(new StaticBox(Vec(screen_width/4, 200), 150, 10) {
    init {
      coord = Vec(screen_width/4, 200)
    }

    private var dir = 1
    action {
      if(isTouching(PlayerBall)) PlayerBall.ball_color = WHITE
      move(Vec(dir,0))
      if(coord.x > screen_width-90) dir = -1
      else if(coord.x < 110) dir = 1
    }

    render {
      val verts:Array[Vector2f] = box.getPoints(body.getPosition, body.getRotation);
      color = WHITE
      GL11.glDisable(GL11.GL_TEXTURE_2D);
          GL11.glBegin(GL11.GL_LINE_LOOP);
          verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
        GL11.glEnd();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
  })
}