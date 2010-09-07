package su.msk.dunno.scage2.handlers.controller

trait Listener {
  def check()
  def ::(o:Listener) = o :: List[Listener](this)
}