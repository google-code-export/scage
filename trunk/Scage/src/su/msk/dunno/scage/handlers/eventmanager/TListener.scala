package su.msk.dunno.scage.handlers.eventmanager

trait TListener {
  protected var wasPressed:Boolean = false
  protected var lastPressed:Long = 0

  def check()

  def ::(o:TListener) = o :: List[TListener](this)
}