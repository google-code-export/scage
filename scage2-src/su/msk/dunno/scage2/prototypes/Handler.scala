package su.msk.dunno.scage2.prototypes

abstract class Handler(val screen:Screen) {
  screen.addHandler(this)

  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def exitSequence():Unit = {}

  def ::(o:Handler) = o :: List[Handler](this)
}