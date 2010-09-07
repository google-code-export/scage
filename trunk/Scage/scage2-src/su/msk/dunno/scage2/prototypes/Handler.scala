package su.msk.dunno.scage2.prototypes

abstract class Handler {
  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def exitSequence():Unit = {}

  def ::(o:Handler) = o :: List[Handler](this)
}