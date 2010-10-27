package scatris

import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import org.apache.log4j.Logger
import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.support.tracer.Trace

abstract class Figure {
  private val log = Logger.getLogger(this.getClass)

  val name:String

  protected val _points:List[Point]
  private var was_disabled = false
  private def activePoints = {
    val active_points = _points.filter(_.isActive)
    if(active_points.length == 0) {
      was_disabled = true
      log.debug("figure "+name +" was disabled")
    }
    active_points
  }

  protected def generateOrientations(positions_array:List[Vec]*):(Int, Map[Int, () => Boolean]) = {
    def addOrientation(orientations:Map[Int, () => Boolean], next:Int):Map[Int, () => Boolean] = {
      if(next >= positions_array.length) orientations
      else {
        val new_orientations = orientations + (next -> (() => {
          val canMove = (0 to _points.length-1).foldLeft(true)((can_move, point_number) => {
            val point = _points(point_number)
            val step = positions_array(next)(point_number)
            can_move && point.canMove(isIncludedTrace, step)
          })
          if(canMove) {
            (0 to _points.length-1).foreach(point_number => {
              val point = _points(point_number)
              val step = positions_array(next)(point_number)
              point.move(isIncludedTrace, step)
            })
            true
          }
          else false
        }))
        addOrientation(new_orientations, next+1)
      }
    }
    (positions_array.length, addOrientation(Map(), 0))
  }

  private def canMove(canMovePoint: (Point) => Boolean) = {
    val active_points = activePoints
    active_points.length > 0 && active_points.forall(canMovePoint(_))
  }

  private def haveDisabledPoints = _points.exists(!_.isActive)

  private def isIncludedTrace(trace:StateTrace) = {
    !activePoints.map(point => point.trace_id).contains(trace.id) && trace.getState.getBool("isActive")
  }

  private var last_move_time = System.currentTimeMillis
  private var is_acceleration = false
  private def movePeriod = if(!is_acceleration) Scatris.gameSpeed else 10
  private def isNextMove = System.currentTimeMillis - last_move_time > movePeriod

  private val down = Vec(0, -h_y)
  private[scatris] var was_landed = false
  def canMoveDown:Boolean = {
    val can_move = canMove(point => point.canMove(isIncludedTrace, down))
    if(!can_move) {
      was_landed = true
    }
    can_move
  }
  action {
    if(!onPause && !was_disabled && isNextMove) {
      if(canMoveDown) {
        activePoints.foreach(point => point.move(isIncludedTrace, down))
        last_move_time = System.currentTimeMillis
      }
      else if(haveDisabledPoints) activePoints.foreach(point => if(point.canMove((t:Trace[_]) => true, down)) point.move((t:Trace[_]) => true, down))      
    }
  }

  private val left = Vec(-h_x, 0)
  private def canMoveLeft = canMove(point => point.canMove(isIncludedTrace, left))
  keyListener(Keyboard.KEY_LEFT, 50, onKeyDown = {
    if(!onPause && !was_landed && canMoveLeft) activePoints.foreach(point => point.move(isIncludedTrace, left))
  })

  private val right = Vec(h_x, 0)
  private def canMoveRight = canMove(point => point.canMove(isIncludedTrace, right))
  keyListener(Keyboard.KEY_RIGHT, 50, onKeyDown = {
    if(!onPause && !was_landed && canMoveRight) activePoints.foreach(point => point.move(isIncludedTrace, right))
  })

  keyListener(Keyboard.KEY_DOWN, 500, onKeyDown = is_acceleration = true, onKeyUp = is_acceleration = false)

  private var cur_orientation = 0
  protected val orientations:(Int, Map[Int, () => Boolean]) = (0, Map())
  keyListener(Keyboard.KEY_UP, 500, onKeyDown = {
    if(!onPause && !was_landed && canMoveDown && orientations._1 > 0) {
      if(orientations._2(cur_orientation)()) {
        cur_orientation += 1
        if(cur_orientation > orientations._1 - 1) cur_orientation = 0
      }
    }
  })
}
