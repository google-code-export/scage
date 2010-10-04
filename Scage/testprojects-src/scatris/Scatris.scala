package scatris

import figures.{Line, Square}
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.{ScageProperties, ScageLibrary}

object Scatris extends Application with ScageLibrary {
  //ScageProperties.file = "options.txt"

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
  var f:Figure = new Square(StandardTracer.pointCenter(3, 12))
  AI.registerAI(() => {
    for(y <- 0 to StandardTracer.N_y-1) {
      if(isFullRow(y)) {
        disableRow(y)
        score += StandardTracer.N_x
      }
    }

    if(!f.canMoveDown && !is_game_finished) {
      val rand = math.random
      /*if(rand < 0.5) f = new Square(StandardTracer.pointCenter(3, 12))
      else */f = new Line(StandardTracer.pointCenter(3, 12))
      if(!f.canMoveDown) is_game_finished = true
    }
  })
  
  Renderer.addInterfaceElement(() => {
    Message.print("score: "+score, 20, height-20)
    if(is_game_finished) Message.print("Game Over", 20, height-35)
  })

  start
}