package su.msk.dunno.scage.single.handlers

import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.Scage

object Idler {
  val framerate:Int = property("framerate", 100);

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

  private val sleep:Long = if(framerate != 0) 1000/framerate else 10
  Scage.action {
    countFPS
    Thread.sleep(sleep)
  }
}
