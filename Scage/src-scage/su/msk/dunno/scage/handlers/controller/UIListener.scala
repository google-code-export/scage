package su.msk.dunno.scage.handlers.controller

trait UIListener {
  protected[controller] var wasPressed:Boolean = false
  protected var lastPressedTime:Long = 0

  def check()
}