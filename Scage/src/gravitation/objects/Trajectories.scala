package gravitation.objects

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.handlers.Renderer
import gravitation.{Gravitation, Universe}
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

class Trajectories extends Gravitation with ScageLibrary {
    var material_points = initPoints()
    var points = List[Vec]()

    def initPoints(m_points:List[MaterialPoint] = Universe.bodies) = m_points.foldLeft(List[MaterialPoint]())((m_points, body) => new MaterialPoint(body.coord, body.velocity, body.mass, body.radius) :: m_points)
    def init = {
      material_points = initPoints()
      points = List[Vec]()
    }

    def calculateTrajectories() = {
      material_points.foreach(point => {
        val next_step = calculateStep(material_points, point)
        point.velocity = next_step._1
        point.coord = next_step._2
      })
      points = points ::: material_points.foldLeft(List[Vec]())((new_points, m_point) => m_point.coord :: new_points)
    }

    Renderer.addRender(() => {
      if(onPause) {
        calculateTrajectories()
        Renderer.setColor(GREEN)
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    	    GL11.glBegin(GL11.GL_POINTS);
            points.foreach(point => GL11.glVertex2f(point.x, point.y))
          GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
      }
    })
  }