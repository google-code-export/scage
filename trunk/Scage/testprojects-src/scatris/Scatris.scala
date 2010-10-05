package scatris

import figures.{Line, S_Figure, Square}
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.{ScageProperties, ScageLibrary}

object Scatris extends Application with ScageLibrary {
  properties = "scatris-properties.txt"

  private def isRestingPoint(point:List[Trace[State]]) = point.find(trace => {
    val state = trace.getState
    state.contains("isActive") && state.getBool("isActive") &&
    state.contains("isMoving") && !state.getBool("isMoving")
  }) match {
    case Some(trace) => true
    case None => false
  }

  def isFullRow(y:Int) = {
    val matrix = StandardTracer.matrix
    (0 to StandardTracer.N_x-1).foldLeft(true)((is_full, x) => is_full && isRestingPoint(matrix(x)(y)))
  }

  def disableRow(y:Int) = {
    val matrix = StandardTracer.matrix
    (0 to StandardTracer.N_x-1).foreach(x => matrix(x)(y).foreach(trace => trace.changeState(new State("disable"))))
  }

  var score = 0
  private var is_game_finished = false
  def upperCenter = StandardTracer.pointCenter(StandardTracer.N_x/2, StandardTracer.N_y-2)
  var f:Figure = new Square(upperCenter)
  AI.registerAI(() => {
    for(y <- 0 to StandardTracer.N_y-1) {
      if(isFullRow(y)) {
        disableRow(y)
        score += StandardTracer.N_x
      }
    }

    if(!f.canMoveDown && !is_game_finished) {
      val rand = math.random
      if(rand < 0.3) f = new S_Figure(upperCenter)
      else if(rand >= 0.3 && rand < 0.6) f = new Square(upperCenter)
      else f = new Line(upperCenter)
      if(!f.canMoveDown) is_game_finished = true
    }
  })
  
  Renderer.addInterfaceElement(() => {
    Message.print("score: "+score, 340, height-25)
    if(is_game_finished) Message.print("Game Over", 340, height-45)
  })

  start
}