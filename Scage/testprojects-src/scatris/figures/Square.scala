package scatris.figures

import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.{Vec}
import scatris.{Figure, Point}
import su.msk.dunno.scage.support.ScageLibrary._

class Square(init_coord:Vec) extends Figure {
  override val name = "Square"
  override val _points:List[Point] = new Point(init_coord + Vec(0, h_y), this) ::
                                     new Point(init_coord + Vec(-h_x, h_y), this) ::
                                     new Point(init_coord + Vec(0, 0), this) ::
                                     new Point(init_coord + Vec(-h_x, 0), this) :: Nil
}