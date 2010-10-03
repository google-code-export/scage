package scatris

import figures.Square
import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.handlers.{Renderer, AI}
import su.msk.dunno.scage.support.messages.Message

object Scatris extends Application with ScageLibrary {
  private def isRestingPoint(point:List[Trace[State]]) = point.find(trace => {
    val state = trace.getState
    state.contains("isActive") && state.getBool("isActive")
  }) match {
    case Some(trace) => true
    case None => false
  }

  def isGameFinished = {
    val matrix = StandardTracer.matrix
    (3 to 4).foldLeft(false)((is_finished, x) => is_finished || isRestingPoint(matrix(x)(StandardTracer.N_y-1)))
  }

  def isFullRow(y:Int) = {
    val matrix = StandardTracer.matrix
    (0 to StandardTracer.N_x-1).foldLeft(true)((is_full, x) => is_full && isRestingPoint(matrix(x)(y)))
  }

  def disableRow(y:Int) = {
    val matrix = StandardTracer.matrix
    (0 to StandardTracer.N_x-1).foreach(x => matrix(x)(y).foreach(trace => trace.changeState(new State("disable"))))
  }

  var f = new Square(StandardTracer.pointCenter(3, 12))
  var score = 0
  AI.registerAI(() => {
    for(y <- 0 to StandardTracer.N_y-1) {
      if(isFullRow(y)) {
        disableRow(y)
        score += StandardTracer.N_x
      }
    }

    if(!f.canMoveDown && !isGameFinished) f = new Square(StandardTracer.pointCenter(3, 12))
  })
  Renderer.addInterfaceElement(() => Message.print("score: "+score, 20, height-20))

  start
}