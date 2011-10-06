package su.msk.dunno.scage.single.handlers.controller

trait UIListener {
  protected[controller] var wasPressed:Boolean = false
  protected var lastPressedTime:Long = 0

  def check()
}