package su.msk.dunno.scage2.handlers

import su.msk.dunno.scage2.support.ScageProperties
import su.msk.dunno.scage2.prototypes.{Screen, Handler}

class Idler(screen:Screen) extends Handler(screen:Screen) {
  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  var fps:Int = 0
  def countFPS() = {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }

  val framerate:Int = ScageProperties.getInt("framerate");

  override def actionSequence() = {
    countFPS
    Thread.sleep(1000/framerate)
  }
}