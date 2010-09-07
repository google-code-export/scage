package su.msk.dunno.scage2.handlers.controller

trait Listener {
  protected var wasPressed:Boolean = false
  protected var lastPressed:Long = 0

  def check()

  def ::(o:Listener) = o :: List[Listener](this)
}