package scatris.figures

import su.msk.dunno.scage.support.Vec
import scatris.{Point, Figure}

class G_Figure(init_coord:Vec) extends Figure {
  override val name = "G_Figure"

  override val points = new Point(init_coord + Vec(-h_x*2, 0), this) ::
                        new Point(init_coord + Vec(-h_x*2, -h_y), this) ::
                        new Point(init_coord + Vec(-h_x, -h_y), this) ::
                        new Point(init_coord + Vec(0, -h_y), this) :: Nil

  override protected val orientations = generateOrientations(
    List(Vec(h_x*2, 0), Vec(h_x, h_y), Vec(0, 0), Vec(-h_x, -h_y)),
    List(Vec(0, -h_y*2), Vec(h_x, -h_y), Vec(0, 0), Vec(-h_x, h_y)),
    List(Vec(-h_x*2, 0), Vec(-h_x, -h_y), Vec(0, 0), Vec(h_x, h_y)),
    List(Vec(0, h_y*2), Vec(-h_x, h_y), Vec(0, 0), Vec(h_x, -h_y))
  )
}