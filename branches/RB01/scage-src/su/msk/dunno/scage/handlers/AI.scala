package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.Handler
import su.msk.dunno.scage.main.Scage

object AI extends Handler {
  private var ai_list:List[() => Unit] = List[() => Unit]()
  def registerAI(ai: () => Unit) = {ai_list = ai :: ai_list}

  override def actionSequence() = {
    if(!Scage.on_pause)ai_list.foreach(ai => ai())
  }
}