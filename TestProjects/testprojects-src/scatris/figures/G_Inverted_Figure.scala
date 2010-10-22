package scatris.figures

import su.msk.dunno.scage.support.Vec
import scatris.{Point, Figure}
import su.msk.dunno.scage.support.ScageLibrary._

class G_Inverted_Figure(init_coord:Vec) extends Figure {
  override val name = "G_Inverted_Figure"

  override val _points = new Point(init_coord + Vec(-h_x*2, -h_y), this) ::
                         new Point(init_coord + Vec(-h_x, -h_y), this) ::
                         new Point(init_coord + Vec(0, -h_y), this) ::
                         new Point(init_coord, this) :: Nil

  override protected val orientations = generateOrientations(
    List(Vec(h_x, h_y), Vec(0, 0), Vec(-h_x, -h_y), Vec(0, -h_y*2)),
    List(Vec(h_x, -h_y), Vec(0, 0), Vec(-h_x, h_y), Vec(-h_x*2, 0)),
    List(Vec(-h_x, -h_y), Vec(0, 0), Vec(h_x, h_y), Vec(0, h_y*2)),
    List(Vec(-h_x, h_y), Vec(0, 0), Vec(h_x, -h_y), Vec(h_x*2, 0))
  )
}