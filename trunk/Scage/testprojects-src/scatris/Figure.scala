package scatris

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.{Vec, ScageLibrary}

abstract class Figure extends ScageLibrary {
  val name:String
  val points:List[Point]

  private def canMove(dir: (Point) => Boolean) = {
    points.filter(point => point.isActive) match {
      case Nil => false
      case active_points =>
        active_points.foldLeft(true)((can_move, point) => can_move && dir(point))
    }
  }

  private def excludedTraces = points.map(point => point.trace)

  private var last_move_time = System.currentTimeMillis
  private var is_acceleration = false
  private def movePeriod = if(!is_acceleration) 1000 else 50
  private def isNextMove = System.currentTimeMillis - last_move_time > movePeriod

  private val down = Vec(0, -StandardTracer.h_y)
  def canMoveDown:Boolean = canMove(point => point.canMove(excludedTraces, down))
  AI.registerAI(() => {
    if(isNextMove && canMoveDown) {
      points.foreach(point => point.move(excludedTraces, down))
      last_move_time = System.currentTimeMillis
    }
  })

  private val left = Vec(-StandardTracer.h_x, 0)
  private def canMoveLeft = canMove(point => point.canMove(excludedTraces, left))
  Controller.addKeyListener(Keyboard.KEY_LEFT, 100, () => {
    if(canMoveDown && canMoveLeft) points.foreach(point => point.move(excludedTraces, left))
  })

  private val right = Vec(StandardTracer.h_x, 0)
  private def canMoveRight = canMove(point => point.canMove(excludedTraces, right))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 100, () => {
    if(canMoveDown && canMoveRight) points.foreach(point => point.move(excludedTraces, right))
  })

  Controller.addKeyListener(Keyboard.KEY_DOWN, 500, () => is_acceleration = true, () => is_acceleration = false)
}