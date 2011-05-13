package su.msk.dunno.q

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.tracer.{CoordTrace, CoordTracer}
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._

object QueersIsland extends ScageScreen("QueersIsland", is_main_screen=true, properties = "q.properties") {
  var alive_count = 0

  val tracer = new CoordTracer[CoordTrace](/*are_solid_edges = true*/) {
    /*render {
      color = CYAN
      for(i <- 0 to N_x) drawLine(Vec(i*h_x + field_from_x, field_from_y),
                                  Vec(i*h_x + field_from_x, field_to_y))
      for(j <- 0 to N_y) drawLine(Vec(field_from_x, j*h_y + field_from_y),
                                  Vec(field_to_x,   j*h_y + field_from_y))
    }*/
  }

  val landscape = image("landscape.png", width, height, 0, 0, 640, 480)
  //val landscape = image("desert.png", width, height, 0, 0, 400, 400)
  render {
    drawDisplayList(landscape, Vec(width/2, height/2), WHITE)
  }

  val skull = image("skull.png", 55, 38, 0, 0, 111, 76)

  def randomCoord = Vec(tracer.field_from_x + (tracer.field_to_x - tracer.field_from_x)*math.random.toFloat,
                        tracer.field_from_y + (tracer.field_to_y - tracer.field_from_y)*math.random.toFloat)

  val male_num = property("male.initial", 20)
  for(i <- 1 to male_num) tracer.addTrace(randomCoord, new Male)

  val female_num = property("female.initial", 20)
  for(i <- 1 to female_num) tracer.addTrace(randomCoord, new Female)

  backgroundColor = WHITE
  interface {
    print(alive_count, 10, height-20, RED)
  }

  def main(args:Array[String]) {
    run()
  }
}