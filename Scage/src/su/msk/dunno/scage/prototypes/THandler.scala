package su.msk.dunno.scage.prototypes

trait THandler {
  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def updateSequence():Unit = {}
  def exitSequence():Unit = {}

  def ::(o:THandler) = o :: List[THandler](this)
}