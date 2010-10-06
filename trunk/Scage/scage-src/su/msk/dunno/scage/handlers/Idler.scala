package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.prototypes.Handler
import su.msk.dunno.scage.support.ScageProperties

object Idler extends Handler {
  val framerate:Int = ScageProperties.intProperty("framerate", 100);

  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  private var _time = 0
  def time = _time
  var fps:Int = 0
  def countFPS() = {
    frames += 1
    _time += 1
    if(System.currentTimeMillis - msek >= 1000) {
      fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }

  override def actionSequence() = {
    countFPS
    if(framerate != 0) Thread.sleep(1000/framerate)
    else Thread.sleep(10)
  }
}