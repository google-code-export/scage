package scatris.figures

import su.msk.dunno.scage.support.Vec
import scatris.{Point, Figure}
import su.msk.dunno.scage.support.ScageLibrary._

class Line(init_coord:Vec) extends Figure {
  override val name = "Line"

  override val _points:List[Point] = new Point(init_coord + Vec(-h_x*2, 0), this) ::
                                     new Point(init_coord + Vec(-h_x, 0), this) ::
                                     new Point(init_coord, this) ::
                                     new Point(init_coord + Vec(h_x, 0), this) :: Nil

  override protected val orientations = generateOrientations(List(Vec(h_x*2, h_y*2), Vec(h_x, h_y), Vec(0, 0), Vec(-h_x, -h_y)),
                                                             List(Vec(h_x*(-2), h_y*(-2)), Vec(-h_x, -h_y), Vec(0, 0), Vec(h_x, h_y))
  )
}