package scatris.figures

import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.{ScageLibrary, Vec}
import scatris.Point

class Square(init_coord:Vec) extends Figure with ScageLibrary {
  val points:List[Point] = new Point(init_coord + Vec(StandardTracer.h_x, StandardTracer.h_y)) ::
                           new Point(init_coord + Vec(0, StandardTracer.h_y)) ::
                           new Point(init_coord + Vec(StandardTracer.h_x, 0)) ::
                           new Point(init_coord) ::
                           Nil
  
  override def isMoving = points.foldLeft(true)((is_moving, point) => is_moving && point.isMoving)
}