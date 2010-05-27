package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Engine

object AI extends THandler {
  private var ai_list:List[() => Unit] = List[() => Unit]()
  def registerAI(ai: () => Unit) = {ai_list = ai :: ai_list}

  override def actionSequence() = {
    if(!Engine.onPause)ai_list.foreach(ai => ai())
  }
}