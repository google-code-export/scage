package su.msk.dunno.scage2.handlers

import su.msk.dunno.scage2.support.ScageProperties
import su.msk.dunno.scage2.prototypes.{Screen, Handler}

class Idler(screen:Screen) extends Handler(screen) {
  val framerate:Int = ScageProperties.getInt("framerate");

  override def actionSequence() = Thread.sleep(1000/framerate)
}