package su.msk.dunno.blame.field

import su.msk.dunno.screens.support.tracer.{Tracer, Trace}
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.{ScageColors, Vec, ScageColor}
import su.msk.dunno.scage.support.ScageProperties._
import rlforj.los.{BresLos, ILosBoard, PrecisePermissive}
import collection.JavaConversions
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.blame.support.{BottomMessages, MyFont}
import su.msk.dunno.blame.screens.Blamer

trait FieldObject extends Trace {
  def getPoint = FieldTracer.point(getCoord)
  def getSymbol:Int
  def getColor:ScageColor
  def isTransparent:Boolean
  def isPassable:Boolean
  
  private var was_drawed = false
  def wasDrawed = was_drawed

  private var is_draw_prevented = false
  def preventDraw = is_draw_prevented = true
  def allowDraw= is_draw_prevented = false
  
  def draw = {
    if(!is_draw_prevented) Renderer.drawDisplayList(getSymbol, getCoord, getColor)
    was_drawed = true
  }
  def drawGray = if(!is_draw_prevented) Renderer.drawDisplayList(getSymbol, getCoord, ScageColors.GRAY)
}

object FieldTracer extends Tracer[FieldObject] {
  def addTraceSecondToLast(fo:FieldObject) = {
    val p = fo.getPoint
    if(isPointOnArea(p)) {
      if(coord_matrix(p.ix)(p.iy).size > 0)
        coord_matrix(p.ix)(p.iy) = coord_matrix(p.ix)(p.iy).head :: fo :: coord_matrix(p.ix)(p.iy).tail
      else coord_matrix(p.ix)(p.iy) = fo :: coord_matrix(p.ix)(p.iy)
      log.debug("added new trace #"+fo.id+" in coord ("+fo.getCoord+")")
    }
    else log.error("failed to add trace: coord ("+fo.getCoord+") is out of area")
    fo.id
  }

  override def removeTraceFromPoint(trace_id:Int, p:Vec) = {
    matrix(p.ix)(p.iy).find(_.id == trace_id) match {
      case Some(fieldObject) => {
        matrix(p.ix)(p.iy) = matrix(p.ix)(p.iy).filterNot(_ == fieldObject)
      }
      case None =>
    }
    light_sources = light_sources.filterNot(_._3 == trace_id)
  }

  def isPointOnArea(x:Int, y:Int) = {
    x >= 0 && x < N_x && y >= 0 && y < N_y
  }

  def isPointPassable(x:Int, y:Int, trace_id:Int):Boolean = 
    isPointOnArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).filter(_.id != trace_id).forall(_.isPassable))
  def isPointPassable(point:Vec, trace_id:Int = -1):Boolean = isPointPassable(point.ix, point.iy, trace_id)
  
  def isPointTransparent(x:Int, y:Int) = {
    isPointOnArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isTransparent))
  }
  
  def isLocationPassable(coord:Vec) = {
    val p = point(coord)
    isPointPassable(p.ix, p.iy, -1)
  }

  def randomPassablePoint(from:Vec = Vec(0, 0), to:Vec = Vec(N_x, N_y)):Option[Vec] = {
    log.debug("looking for new random passable point")

    var x = -1
    var y = -1

    val max_count = 10
    var count = max_count
    while(!isPointPassable(x, y, -1) && count > 0) {
      x = (from.x + math.random*(to.x - from.x)).toInt
      y = (from.y + math.random*(to.y - from.y)).toInt

      count -= 1
    }
    if(count == 0 && !isPointPassable(x, y, -1)) {
      log.warn("warning: cannot locate random passable point within "+max_count+" tries")
      None
    }
    else Some(Vec(x, y))
  }
  
  def direction(from:Vec, to:Vec) = {
    val diff = (to - from)
    Vec(math.signum(diff.x), math.signum(diff.y))
  }
  
  def isDirectionPassable(from:Vec, to:Vec) = {
    isPointPassable(from + direction(from, to))
  }
  
  def move2PointIfPassable(trace_id:Int, old_point:Vec, new_point:Vec) = {
    if(isPointPassable(new_point, trace_id)) {
      updatePointLocation(trace_id, old_point, new_point)
    }
    else false
  }
  
  def neighboursOfPoint(trace_id:Int, point:Vec, dov:Int) = {
    neighbours(trace_id, pointCenter(point), -dov to dov, (fieldObject) =>
      isVisible(point, fieldObject.getPoint, dov))
  }
  def objectsAtPoint(point:Vec) = matrix(point.ix)(point.iy)

  def isNearPlayer(point:Vec) = (Blamer.currentPlayer.point dist point) < visibility_distance

  private val lineView = new ILosBoard {
    def contains(x:Int, y:Int):Boolean = isPointOnArea(x, y)    
    def isObstacle(x:Int, y:Int):Boolean = !isPointTransparent(x, y)
    def visit(x:Int, y:Int) = {}
  }
  private val bresenham = new BresLos(false)  
  def isVisible(p1:Vec, p2:Vec, dov:Int) = {
    if((p2 dist p1) > dov*dov) false
    else if(p2 == p1) true
    else bresenham.existsLineOfSight(lineView, p1.ix, p1.iy, p2.ix, p2.iy, false)
  }
  def line(p1:Vec, p2:Vec):List[Vec] = {
    bresenham.existsLineOfSight(lineView, p1.ix, p1.iy, p2.ix, p2.iy, true)
    JavaConversions.asBuffer(bresenham.getProjectPath).foldLeft(List[Vec]())((line, point) => new Vec(point.x, point.y) :: line)
  }
  
  def preventDraw(point:Vec) =
    if(!matrix(point.ix)(point.iy).isEmpty) matrix(point.ix)(point.iy).foreach(_.preventDraw)
  def allowDraw(point:Vec) =
    if(!matrix(point.ix)(point.iy).isEmpty) matrix(point.ix)(point.iy).foreach(_.allowDraw)  

  private var light_sources:List[(() => Vec, () => Int, Int)] = Nil
  def addLightSource(point: => Vec, dov: => Int = 5, trace_id:Int) = 
    light_sources = (() => point, () => dov, trace_id) :: light_sources
  
  private val pp = new PrecisePermissive();  
  private val drawView = new ILosBoard() {
    def contains(x:Int, y:Int):Boolean = isPointOnArea(x, y)    
    def isObstacle(x:Int, y:Int):Boolean = !isPointTransparent(x, y)
    def visit(x:Int, y:Int) = {
      if(matrix(x)(y).length > 0) matrix(x)(y).head.draw
    }   
  }

  def drawField(player_point:Vec) = {
    drawGray(player_point)
    drawEnlighted(player_point)
  }

  val field_visible_width  = property("field.visible.width",  Renderer.width - Blamer.right_messages_width)
  val field_visible_height = property("field.visible.height", Renderer.height - BottomMessages.bottom_messages_height)

  private val half_visible_N_x:Int = field_visible_width/h_x/2
  private val half_visible_N_y:Int = field_visible_height/h_y/2

  val visibility_distance = math.min(half_visible_N_x, half_visible_N_y)*math.min(half_visible_N_x, half_visible_N_y)
  private def drawEnlighted(player_point:Vec) = {
    light_sources.filter(source => (source._1() dist2 player_point) < visibility_distance).foreach(source => {
      pp.visitFieldOfView(drawView, source._1().ix, source._1().iy, source._2())
    })
  }

  private def drawGray(player_point:Vec) = {
    val from_x = math.max(0,     player_point.ix - half_visible_N_x)
    val to_x   = math.min(N_x-1, player_point.ix + half_visible_N_x)
    val from_y = math.max(0,     player_point.iy - half_visible_N_y)
    val to_y   = math.min(N_y-1, player_point.iy + half_visible_N_y)
    for(x <- from_x to to_x) {
      for(y <- from_y to to_y) {
        if(matrix(x)(y).length > 0) {
          val tile = matrix(x)(y).last
          if(tile.wasDrawed && tile.getSymbol != MyFont.FLOOR) matrix(x)(y).head.drawGray
        }
      }
    }
  }
}
