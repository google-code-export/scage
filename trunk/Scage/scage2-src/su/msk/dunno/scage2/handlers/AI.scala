package su.msk.dunno.scage2.handlers

import su.msk.dunno.scage2.prototypes.{Screen, Handler}

class AI(screen:Screen) extends Handler(screen) {
  private var ai_list:List[() => Unit] = List[() => Unit]()
  def registerAI(ai: () => Unit) = {ai_list = ai :: ai_list}

  override def actionSequence() = {
    if(!screen.onPause)ai_list.foreach(ai => ai())
  }
}