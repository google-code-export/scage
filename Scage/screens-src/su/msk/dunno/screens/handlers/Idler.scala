package su.msk.dunno.screens.handlers

import su.msk.dunno.scage.support.ScageProperties._
import su.msk.dunno.screens.Screen

object Idler {
  val framerate:Int = property("framerate", 100);
}

class Idler(screen:Screen) {
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

  private val sleep:Long = if(Idler.framerate != 0) 1000/Idler.framerate else 10
  screen.action {
    countFPS
    Thread.sleep(sleep)
  }
}