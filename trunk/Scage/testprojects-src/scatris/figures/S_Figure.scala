package scatris.figures

import su.msk.dunno.scage.support.Vec
import scatris.{Point, Figure}

class S_Figure(init_coord:Vec) extends Figure {
  override val name = "S_Figure"

  val point1 = new Point(init_coord, this)
  val point2 = new Point(init_coord + Vec(h_x, 0), this)
  val point3 = new Point(init_coord + Vec(h_x, h_y), this)
  val point4 = new Point(init_coord + Vec(h_x*2, h_y), this)
  override val points = point1 :: point2 :: point3 :: point4 :: Nil

  /*private val orientations:Map[Int, () => Boolean] = Map(
    1 -> (() => {
      if(point1.canMove(excludedTraces, Vec(h_x, h_y)) &&
         point3.canMove(excludedTraces, Vec(h_x, -h_y)) &&
         point4.canMove(excludedTraces, Vec(0, -h_y*2))) {
          point1.move(excludedTraces, Vec(h_x, h_y))
          point3.move(excludedTraces, Vec(h_x, -h_y))
          point4.move(excludedTraces, Vec(0, -h_y*2))
          true
      }
      else false
    }),
    2 -> (() => {
      if(point1.canMove(excludedTraces, Vec(-h_x, -h_y)) &&
         point3.canMove(excludedTraces, Vec(-h_x, h_y)) &&
         point4.canMove(excludedTraces, Vec(0, h_y*2))) {
          point1.move(excludedTraces, Vec(-h_x, -h_y))
          point3.move(excludedTraces, Vec(-h_x, h_y))
          point4.move(excludedTraces, Vec(0, h_y*2))
          true
      }
      else false
    })
  )*/

  private val orientations = generateOrientations(List(Vec(h_x, h_y), Vec(h_x, -h_y), Vec(0, -h_y*2)),
    List(Vec(-h_x, -h_y), Vec(-h_x, h_y), Vec(0, h_y*2)))
}