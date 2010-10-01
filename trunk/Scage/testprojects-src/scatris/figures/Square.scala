package scatris.figures

import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.{ScageLibrary, Vec}
import scatris.{Figure, Point}

class Square(init_coord:Vec) extends Figure with ScageLibrary {
  override val points:List[Point] = new Point(init_coord + Vec(StandardTracer.h_x, StandardTracer.h_y), this) ::
                                    new Point(init_coord + Vec(0, StandardTracer.h_y), this) ::
                                    new Point(init_coord + Vec(StandardTracer.h_x, 0), this) ::
                                    new Point(init_coord, this) ::
                                    Nil
}