package scatris.figures

import su.msk.dunno.scage.support.Vec
import scatris.{Point, Figure}

class T_Figure(init_coord:Vec) extends Figure {
  override val name = "T_Figure"

  override val _points = new Point(init_coord + Vec(-h_x*2, -h_y), this) ::
                         new Point(init_coord + Vec(-h_x, -h_y), this) ::
                         new Point(init_coord + Vec(0, -h_y), this) ::
                         new Point(init_coord + Vec(-h_x, 0), this) :: Nil

  override protected val orientations = generateOrientations(
    List(Vec(h_x, h_y), Vec(0, 0), Vec(-h_x, -h_y), Vec(h_x, -h_y)),
    List(Vec(h_x, -h_y), Vec(0, 0), Vec(-h_x, h_y), Vec(-h_x, -h_y)),
    List(Vec(-h_x, -h_y), Vec(0, 0), Vec(h_x, h_y), Vec(-h_x, h_y)),
    List(Vec(-h_x, h_y), Vec(0, 0), Vec(h_x, -h_y), Vec(h_x, h_y))
  )
}