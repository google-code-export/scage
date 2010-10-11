package scatris

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.{Vec, ScageLibrary}

abstract class Figure extends ScageLibrary {
  val name:String

  protected val _points:List[Point]
  private var was_disabled = false
  private def points = {
    val active_points = _points.filter(point => point.isActive)
    if(active_points.length == 0) was_disabled = true
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
            can_move && point.canMove(excludedTraces, step)
          })
          if(canMove) {
            (0 to _points.length-1).foreach(point_number => {
              val point = _points(point_number)
              val step = positions_array(next)(point_number)
              point.move(excludedTraces, step)
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

  private def canMove(dir: (Point) => Boolean) = {
    val active_points = points
    active_points.length > 0 && active_points.foldLeft(true)((can_move, point) => can_move && dir(point))
  }

  private def haveDisabledPoints =
    _points.foldLeft(false)((have_disabled, point) => have_disabled && point.isActive)

  protected def excludedTraces = points.map(point => point.trace)

  private var last_move_time = System.currentTimeMillis
  private var is_acceleration = false
  private def movePeriod = if(!is_acceleration) Scatris.gameSpeed else 50
  private def isNextMove = System.currentTimeMillis - last_move_time > movePeriod

  private val down = Vec(0, -h_y)
  private var was_landed = false
  def canMoveDown:Boolean = {
    val can_move = canMove(point => point.canMove(excludedTraces, down))
    if(!can_move) was_landed = true
    can_move
  }
  AI.registerAI(() => {
    if(!was_disabled && isNextMove) {
      if(canMoveDown) points.foreach(point => point.move(excludedTraces, down))
      else if(haveDisabledPoints) points.foreach(point => if(point.canMove(Nil, down)) point.move(Nil, down))
      last_move_time = System.currentTimeMillis
    }
  })

  private val left = Vec(-h_x, 0)
  private def canMoveLeft = canMove(point => point.canMove(excludedTraces, left))
  Controller.addKeyListener(Keyboard.KEY_LEFT, 75, () => {
    if(!onPause && !was_landed && canMoveLeft) points.foreach(point => point.move(excludedTraces, left))
  })

  private val right = Vec(h_x, 0)
  private def canMoveRight = canMove(point => point.canMove(excludedTraces, right))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 75, () => {
    if(!onPause && !was_landed && canMoveRight) points.foreach(point => point.move(excludedTraces, right))
  })

  Controller.addKeyListener(Keyboard.KEY_DOWN, 500, () => is_acceleration = true, () => is_acceleration = false)

  private var cur_orientation = 0
  protected val orientations:(Int, Map[Int, () => Boolean]) = (0, Map())
  Controller.addKeyListener(Keyboard.KEY_UP, 500, () => {
    if(!onPause) {
      if(orientations._1 > 0 && canMoveDown) {
        if(orientations._2(cur_orientation)()) {
          cur_orientation += 1
          if(cur_orientation > orientations._1 - 1) cur_orientation = 0
        }
      }
    }
  })
}