package net.scage.support.tracer

import net.scage.support.Vec
import net.scage.support.ScageProperties._
import net.scage.handlers.Renderer._

object CoordTracer {
  def apply[CT <: CoordTrace](traces:(Vec, CT)*) = {
    val tracer = new CoordTracer[CT]
    for((coord, trace) <- traces) tracer.addTrace(coord, trace)
    tracer
  }

  /*def apply[CT <: CoordTrace](coords:Vec*) = {
    val tracer = new ScageTracer[CT]
    for(coord <- coords) tracer.addTrace(coord, new CoordTrace {
      def changeState(changer: Trace, state: State) {}
      def getState: State = new State
    })
    tracer
  }*/

  def emptyCoordTrace = new EmptyCoordTrace
}

trait CoordTrace extends Trace {
  val coord:Vec = Vec(-1,-1)
}

class EmptyCoordTrace extends CoordTrace {
  def changeState(changer:Trace, state:State) {}
  def getState = new State
}

class CoordTracer[CT <: CoordTrace](field_from_x:Int        = property("field.from.x", 0),
                                    field_to_x:Int          = property("field.to.x", screen_width),
                                    field_from_y:Int        = property("field.from.y", 0),
                                    field_to_y:Int          = property("field.to.y", screen_height),
                                    init_h_x:Int            = property("field.h_x", 0),
                                    init_h_y:Int            = property("field.h_y", 0),
                                    init_N_x:Int            = if(property("field.h_x", 0) == 0) property("field.N_x", screen_width/50) else 0,
                                    init_N_y:Int            = if(property("field.h_y", 0) == 0) property("field.N_y", screen_height/50) else 0,
                                    are_solid_edges:Boolean = property("field.solid_edges", true))
extends ScageTracer[CT](field_from_x,field_to_x,field_from_y,field_to_y,init_h_x,init_h_y,init_N_x,init_N_y,are_solid_edges) {
  override def addTrace(coord:Vec, trace:CT) = {
    if(isCoordOnArea(coord)) trace.coord is coord
    super.addTrace(point(coord), trace)
  }

  override def updateLocation(trace:CT, new_coord:Vec):Vec = {
    val new_coord_edges_affected = outsideCoord(new_coord)
    if(isCoordOnArea(new_coord_edges_affected)) {
      trace.coord is new_coord_edges_affected
      super.updateLocation(trace, point(new_coord_edges_affected))
    }
    trace.coord
  }

  override def move(trace:CT, delta:Vec):Vec = {
    updateLocation(trace, trace.coord + delta)
  }

  def outsideCoord(coord:Vec):Vec = {
    def checkC(c:Float, from:Float, to:Float):Float = {
      val dist = to - from
      if(c >= to) checkC(c - dist, from, to)
      else if(c < from) checkC(c + dist, from, to)
      else c
    }

    if(are_solid_edges) coord else {
      val x = checkC(coord.x, field_from_x, field_to_x)
      val y = checkC(coord.y, field_from_y, field_to_y)

      Vec(x, y)
    }
  }

  def isCoordOnArea(coord:Vec) = {
    coord.x >= field_from_x && coord.x < field_to_x && coord.y >= field_from_y && coord.y < field_to_y
  }

  def hasCollisions(target_trace:CT, tested_coord:Vec, min_dist:Float, condition:(CT) => Boolean = (trace) => true) = {
    if(are_solid_edges && !isCoordOnArea(tested_coord)) true
    else {
      val tested_coord_edges_affected = outsideCoord(tested_coord)
      val min_dist2 = min_dist*min_dist
      val modified_condition = (trace:CT) => trace.id != target_trace.id && condition(trace)

      val xrange = (2*min_dist/h_x).toInt + 1
      val yrange = (2*min_dist/h_y).toInt + 1
      traces(point(tested_coord_edges_affected), -xrange to xrange, -yrange to yrange, modified_condition).exists(
        trace => (trace.coord dist2 tested_coord_edges_affected) < min_dist2)
    }
  }
}