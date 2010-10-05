package scatris.figures

import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.tracer.StandardTracer
import scatris.{Point, Figure}
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard

class Line(init_coord:Vec) extends Figure {
  override val name = "Line"

  val point1 = new Point(init_coord + Vec(-h_x*2, 0), this)
  val point2 = new Point(init_coord + Vec(-h_x, 0), this)
  val point3 = new Point(init_coord, this)
  val point4 = new Point(init_coord + Vec(h_x, 0), this)

  override val points:List[Point] = point1 :: point2 :: point3 :: point4 :: Nil

  private val positions = Map(
    1 -> (() => {
      if(point1.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*2) &&
         point2.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)) &&
         point3.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-1))) {
          point1.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*2)
          point2.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y))
          point4.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-1))
          true
      }
      else false
    }),
    2 -> (() => {
      if(point1.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-2)) &&
         point2.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-1)) &&
         point3.canMove(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y))) {
          point1.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-2))
          point2.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y)*(-1))
          point4.move(excludedTraces, Vec(StandardTracer.h_x, StandardTracer.h_y))
          true
      }
      else false
    }))

  private var cur_position = 1
  private val orientations = generateOrientations(List(Vec(h_x, h_y)*2, Vec(h_x, h_y), Vec(0, 0), Vec(h_x, h_y)*(-1)),
                                                  List(Vec(h_x, h_y)*(-2), Vec(h_x, h_y)*(-1), Vec(0, 0), Vec(h_x, h_y)))
  Controller.addKeyListener(Keyboard.KEY_UP, 500, () => {
    if(canMoveDown) {
      if(positions(cur_position)()) {
        cur_position += 1
        if(cur_position > 2) cur_position = 1
      }
    }
  })
}