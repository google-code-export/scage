package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Scage

object Idler extends THandler {
  val framerate:Int = Scage.getIntProperty("framerate");

  override def actionSequence() = Thread.sleep(1000/framerate)
}