package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Engine
import su.msk.dunno.scage.support.Vec

object Tracer extends THandler {
  val N_x = Engine.getIntProperty("N_x")
  val N_y = Engine.getIntProperty("N_y")

  private var object_points:List[((Int, Int), () => Vec)] = List[((Int, Int), () => Vec)]()
  private var coord_matrix = Array.ofDim[List[() => Vec]](N_x, N_y)
  for(i <- 0 to N_x-1) {
    for(j <- 0 to N_y-1) {
      coord_matrix(i)(j) = List[() => Vec]()
    }
  }

  def addTrace(coord: () => Vec) = {
    val p = point(coord())
    if(p._1 >= 0 && p._1 < N_x && p._2 >= 0 && p._2 < N_y/* && !coord_matrix(p._1)(p._2).contains(coord)*/)
      coord_matrix(p._1)(p._2) = coord :: coord_matrix(p._1)(p._2)
      object_points = (point(coord()), coord) :: object_points
  }

  def point(v:Vec):(Int, Int) = ((v.x/Renderer.width*N_x).toInt, (v.y/Renderer.height*N_y).toInt)
  
  def getNeighbours(v:Vec):List[Vec] = {
    val p = point(v)
    if(p._1 >= 0 && p._1 < N_x && p._2 >= 0 && p._2 < N_y) {
      coord_matrix(p._1)(p._2).foldLeft(List[Vec]())((acc, coord) => {
        val c = coord()
        if(c != v) coord() :: acc
        else acc
      })
    }
    else List[Vec]()
  }

  override def actionSequence() = {
    object_points = object_points.map(obj => {
      val old_p = obj._1
      val new_p = point(obj._2())
      if(old_p._1 != new_p._1 || old_p._2 != new_p._2) {
        //coord_matrix(old_p._1)(old_p._2) = coord_matrix(old_p._1)(old_p._2).filterNot(coord => coord.equals(obj._2))
        coord_matrix(new_p._1)(new_p._2) = obj._2 :: coord_matrix(new_p._1)(new_p._2)
      }
      (new_p, obj._2)
    })
  }
}