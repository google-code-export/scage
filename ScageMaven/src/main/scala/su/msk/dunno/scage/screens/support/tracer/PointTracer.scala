package su.msk.dunno.scage.screens.support.tracer

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.ScageLibrary._

trait PointTrace extends Trace {
  def getPoint:Vec
  def getCoord = getPoint
}

class PointTracer[PT <: PointTrace](field_from_x:Int = property("field.from.x", 0),
                                    field_to_x:Int = property("field.to.x", 800),
                                    field_from_y:Int = property("field.from.y", 0),
                                    field_to_y:Int = property("field.to.y", 600),
                                    N_x:Int = property("field.N_x", 16),
                                    N_y:Int = property("field.N_y", 12),
                                    are_solid_edges:Boolean = property("field.solid_edges", true))
extends Tracer[PT](field_from_x,field_to_x,field_from_y,field_to_y,N_x,N_y,are_solid_edges) {
  def addPointTrace(pt:PT) = {
    val p = pt.getPoint
    if(isPointOnArea(p)) {
      coord_matrix(p.ix)(p.iy) = pt :: coord_matrix(p.ix)(p.iy)
      log.debug("added new field trace #"+pt.id+" in point ("+pt.getPoint+")")
    }
    else log.warn("failed to add field trace: point ("+pt.getPoint+") is out of area")
    pt.id
  }
  override def addTrace(pt:PT) = addPointTrace(pt)

  override def isCoordOnArea(point:Vec) = isPointOnArea(point)

  override protected def checkEdges(point:Vec):Vec = checkPointEdges(point)

  def updatePointLocation(trace_id:Int, old_point:Vec, new_point:Vec):Boolean = {
    if(are_solid_edges && !isPointOnArea(new_point)) false
    else {
      val new_point_edges_affected = checkPointEdges(new_point)
      if(old_point != new_point_edges_affected) {
        coord_matrix(old_point.ix)(old_point.iy).find(trace => trace.id == trace_id) match {
          case Some(target_trace) => {
            coord_matrix(old_point.ix)(old_point.iy) = coord_matrix(old_point.ix)(old_point.iy).filter(trace => trace.id != trace_id)
            coord_matrix(new_point.ix)(new_point.iy) = target_trace :: coord_matrix(new_point.ix)(new_point.iy)

            old_point is new_point_edges_affected
            true
          }
          case None => false
        }
      }
      else true
    }
  }
  override def updateLocation(trace_id:Int, old_point:Vec, new_point:Vec):Boolean = {
    updatePointLocation(trace_id, old_point, new_point)
  }

  override def removeTrace(trace_id:Int, point:Vec) = {
    removeTraceFromPoint(trace_id, point)
  }

  override def neighbours(trace_id:Int, point:Vec, range:Range, condition:(PT) => Boolean):List[PT] = {
    var neighbours:List[PT] = Nil
    range.foreachpair((i, j) => {
      val near_point = checkPointEdges(point + Vec(i, j))
    	neighbours = coord_matrix(near_point.ix)(near_point.iy).foldLeft(List[PT]())((acc, trace) => {
    	  if(condition(trace) && trace.id != trace_id) trace :: acc
    		else acc
    	}) ::: neighbours
    })
    neighbours
  }
}