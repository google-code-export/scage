package net.scage.handlers

import _root_.net.scage.support.ScageProperties

object Idler {
  val framerate = ScageProperties.property("framerate", 100)

  private var _fps:Int = 0
  def fps = _fps

  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  private def countFPS() = {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      _fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }
  
  /*
   * Best sync method that works reliably. From lwjgl library.
   *
   * @param fps The desired frame rate, in frames per second
   */
  /*def sync = {
    var timeNow:Long
    var gapTo:Long
    var savedTimeLate:Long
    
    gapTo = Sys.getTimerResolution() / framerate + timeThen
    timeNow = Sys.getTime
    savedTimeLate = timeLate

    try {
      while ( gapTo > timeNow + savedTimeLate ) {
        Thread.sleep(1)
	timeNow = Sys.getTime
      }
    } catch {
      case e:InterruptedException => Thread.currentThread.interrupt
    }
    if(gapTo < timeNow) timeLate = timeNow - gapTo;
    else timeLate = 0
    timeThen = timeNow
  }*/
}
