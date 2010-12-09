package su.msk.dunno.blame.field

import su.msk.dunno.screens.support.tracer.{Tracer, Trace}
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.{Colors, Vec, Color}
import su.msk.dunno.scage.support.ScageProperties._
import rlforj.los.{BresLos, ILosBoard, PrecisePermissive}
import collection.JavaConversions
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.blame.support.{IngameMessages, MyFont}
import su.msk.dunno.blame.screens.Blamer

trait FieldObject extends Trace {
  def getSymbol:Int
  def getColor:Color
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
  def drawGray = if(!is_draw_prevented) Renderer.drawDisplayList(getSymbol, getCoord, Colors.GRAY)
}

object FieldTracer extends Tracer[FieldObject](
  property("game_from_x", 0), 
  property("game_to_x", 800), 
  property("game_from_y", 0), 
  property("game_to_y", 600), 
  property("N_x", 16), 
  property("N_y", 12), 
  true) {
  override def removeTraceFromPoint(trace_id:Int, p:Vec) = {
    matrix(p.ix)(p.iy).find(_.id == trace_id) match {
      case Some(fieldObject) => {
        light_sources = light_sources.filterNot(source => pointCenter(source._1()) == fieldObject.getCoord)
        matrix(p.ix)(p.iy) = matrix(p.ix)(p.iy).filterNot(_ == fieldObject)
      }
      case None =>
    }
  }

  def isPointOnArea(x:Int, y:Int) = {
    x >= 0 && x < N_x && y >= 0 && y < N_y
  }

  def isPointPassable(x:Int, y:Int):Boolean = 
    isPointOnArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isPassable))
  def isPointPassable(point:Vec):Boolean = isPointPassable(point.ix, point.iy)
  
  def isPointTransparent(x:Int, y:Int) = {
    isPointOnArea(x, y) && (matrix(x)(y).length == 0 || matrix(x)(y).forall(_.isTransparent))
  }
  
  def isLocationPassable(coord:Vec) = {
    val p = point(coord)
    isPointPassable(p.ix, p.iy)
  }

  def randomPassablePoint(from:Vec = Vec(0, 0), to:Vec = Vec(N_x, N_y)):Vec = {
    log.debug("looking for new random passable point")

    var x = -1
    var y = -1

    val max_count = 10
    var count = max_count
    while(!isPointPassable(x, y) && count > 0) {
      x = (from.x + math.random*(to.x - from.x)).toInt
      y = (from.y + math.random*(to.y - from.y)).toInt

      count -= 1
    }
    if(count == 0 && !isPointPassable(x, y))
      log.warn("warning: cannot locate random passable point within "+max_count+" tries")

    Vec(x, y)
  }
  
  def move2PointIfPassable(trace_id:Int, old_point:Vec, new_point:Vec) = {
    if(isPointPassable(new_point)) {
      updatePointLocation(trace_id, old_point, new_point)
    }
    else false
  }
  
  def neighboursOfPoint(trace_id:Int, point:Vec, range:Range) = {
    neighbours(trace_id, pointCenter(point), -1 to 1, (f) => true)
  }
  def objectsAtPoint(point:Vec) = matrix(point.ix)(point.iy)

  def preventDraw(point:Vec) =
    if(!matrix(point.ix)(point.iy).isEmpty) matrix(point.ix)(point.iy).foreach(_.preventDraw)
  def allowDraw(point:Vec) =
    if(!matrix(point.ix)(point.iy).isEmpty) matrix(point.ix)(point.iy).foreach(_.allowDraw)

  private val lineView = new ILosBoard {
    def contains(x:Int, y:Int):Boolean = isPointOnArea(x, y)    
    def isObstacle(x:Int, y:Int):Boolean = !isPointTransparent(x, y)
    def visit(x:Int, y:Int) = {}
  }  
  def line(p1:Vec, p2:Vec):List[Vec] = {
    val ila = new BresLos(false)
    ila.existsLineOfSight(lineView, p1.ix, p1.iy, p2.ix, p2.iy, true);
    JavaConversions.asBuffer(ila.getProjectPath).foldLeft(List[Vec]())((line, point) => new Vec(point.x, point.y) :: line)
  }

  private var light_sources:List[(() => Vec, () => Int)] = Nil
  def addLightSource(point: => Vec, dov: => Int = 5) = light_sources = (() => point, () => dov) :: light_sources
  
  private val pp = new PrecisePermissive();  
  private val drawView = new ILosBoard() {
    def contains(x:Int, y:Int):Boolean = isPointOnArea(x, y)    
    def isObstacle(x:Int, y:Int):Boolean = !isPointTransparent(x, y)
    def visit(x:Int, y:Int) = {
      if(matrix(x)(y).length > 0) matrix(x)(y).head.draw
    }   
  }

  def draw(player_point:Vec) = {
    drawGray(player_point)
    drawEnlighted(player_point)
  }
  private val distance_from_player = math.min(N_x/2, N_y/2)*math.min(N_x/2, N_y/2)
  private def drawEnlighted(player_point:Vec) = {
    light_sources.filter(source => (source._1() dist2 player_point) < distance_from_player).foreach(source => {
      pp.visitFieldOfView(drawView, source._1().ix, source._1().iy, source._2())
    })
  }

  val visible_width  = property("visible_width",  Renderer.width - Blamer.right_messages_width)
  val visible_height = property("visible_height", Renderer.height - IngameMessages.bottom_messages_height)

  private val half_visible_N_x:Int = visible_width/h_x/2
  private val half_visible_N_y:Int = visible_height/h_y/2
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
