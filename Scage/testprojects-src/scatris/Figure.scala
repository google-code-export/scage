package scatris

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.ScageLibrary

abstract class Figure extends ScageLibrary {
  val points:List[Point]
  private var is_moving = true
  def isMoving = is_moving
  def canMoveDown = {
    val figure_traces = points.map(point => point.trace)
    points.foldLeft(true)((can_move_down, point) => can_move_down && point.canMoveDown(figure_traces))
  }

  private var last_move_time = System.currentTimeMillis
  private val move_period = 100
  AI.registerAI(() => {
    val is_next_move = System.currentTimeMillis - last_move_time > move_period
    if(isMoving && is_next_move) {
      if(!canMoveDown) is_moving = false
      else points.sortWith((p1, p2) => p1.coord.y < p2.coord.y).foreach(point => point.moveDown)      
      last_move_time = System.currentTimeMillis
    }
  })

  Controller.addKeyListener(Keyboard.KEY_LEFT, 1000, () => {
    if(isMoving) points.sortWith((p1, p2) => p1.coord.x < p2.coord.x).foreach(point => point.moveLeft)
  })

  Controller.addKeyListener(Keyboard.KEY_RIGHT, 1000, () => {
    if(isMoving) points.sortWith((p1, p2) => p1.coord.x > p2.coord.x).foreach(point => point.moveRight)
  })
}