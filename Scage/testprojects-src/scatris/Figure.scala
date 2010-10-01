package scatris

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard

trait Figure {
  val points:List[Point]
  def isMoving = points.foldLeft(true)((is_moving, point) => is_moving && point.isMoving)

  private var last_move_time = System.currentTimeMillis
  private val move_period = 500
  AI.registerAI(() => {
    val is_next_move = System.currentTimeMillis - last_move_time > move_period
    if(isMoving && is_next_move) {
      points.sortWith((p1, p2) => p1.coord.y < p2.coord.y).foreach(point => point.moveDown)
      last_move_time = System.currentTimeMillis
    }
  })

  Controller.addKeyListener(Keyboard.KEY_LEFT, 1000, () => {
    if(isMoving) {
      points.sortWith((p1, p2) => p1.coord.x < p2.coord.x).foreach(point => point.moveLeft)
    }
  })

  Controller.addKeyListener(Keyboard.KEY_RIGHT, 1000, () => {
    if(isMoving) {
      points.sortWith((p1, p2) => p1.coord.x > p2.coord.x).foreach(point => point.moveRight)
    }
  })
}