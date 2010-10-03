package scatris

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.tracer.StandardTracer
import su.msk.dunno.scage.support.ScageLibrary

abstract class Figure extends ScageLibrary {
  val name:String
  val points:List[Point]

  private def excludedTraces = points.map(point => point.trace)
  def canMoveDown = {
    points.foldLeft(true)((can_move_down, point) => can_move_down && point.canMoveDown(excludedTraces))
  }

  private var last_move_time = System.currentTimeMillis
  private var is_acceleration = false
  private def movePeriod = if(!is_acceleration) 300 else 50
  private def isNextMove = System.currentTimeMillis - last_move_time > movePeriod
  AI.registerAI(() => {
    if(isNextMove) {
      if(canMoveDown) points.sortWith((p1, p2) => p1.coord.y < p2.coord.y).foreach(point => point.moveDown)
      last_move_time = System.currentTimeMillis
    }
  })

  Controller.addKeyListener(Keyboard.KEY_LEFT, 1000, () => {
    if(canMoveDown) points.sortWith((p1, p2) => p1.coord.x < p2.coord.x).foreach(point => point.moveLeft)
  })

  Controller.addKeyListener(Keyboard.KEY_RIGHT, 1000, () => {
    if(canMoveDown) points.sortWith((p1, p2) => p1.coord.x > p2.coord.x).foreach(point => point.moveRight)
  })

  Controller.addKeyListener(Keyboard.KEY_DOWN, 1000, () => is_acceleration = true, () => is_acceleration = false)
}