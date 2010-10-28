package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.support.ScageProperties._
import su.msk.dunno.scage.Scage

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

  Scage.action {
      countFPS
//    if(framerate != 0) Thread.sleep(1000/framerate)
//    else Thread.sleep(10)    
      Thread.sleep(10)
  }
}
