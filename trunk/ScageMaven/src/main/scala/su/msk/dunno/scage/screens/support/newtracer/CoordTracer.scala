package su.msk.dunno.scage.screens.support.newtracer

import su.msk.dunno.scage.screens.support.ScageLibrary._
import su.msk.dunno.scage.single.support.Vec

trait CoordTrace extends Trace {
  val coord:Vec = Vec(-1,-1)
}

class CoordTracer[CT <: CoordTrace](field_from_x:Int = property("field.from.x", 0),
                                    field_to_x:Int = property("field.to.x", 800),
                                    field_from_y:Int = property("field.from.y", 0),
                                    field_to_y:Int = property("field.to.y", 600),
                                    N_x:Int = property("field.N_x", 16),
                                    N_y:Int = property("field.N_y", 12),
                                    are_solid_edges:Boolean = property("field.solid_edges", true))
extends Tracer[CT](field_from_x,field_to_x,field_from_y,field_to_y,N_x,N_y,are_solid_edges) {
  override def addTrace(coord:Vec, trace:CT) = {
    if(isCoordOnArea(coord)) trace.coord is coord
    super.addTrace(point(coord), trace)
  }

  override def updateLocation(trace:CT, _new_coord:Vec) {
    val new_coord = checkCoordEdges(_new_coord)
    if(isCoordOnArea(new_coord)) {
      trace.coord is new_coord
      super.updateLocation(trace, point(new_coord))
    }
  }

  override def move(trace:CT, delta:Vec) {
    updateLocation(trace, trace.coord + delta)
  }

  protected def checkCoordEdges(coord:Vec):Vec = {
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
}