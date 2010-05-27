package su.msk.dunno.scage.prototypes

trait TObject {
  def update():Unit = {}
  def render():Unit = {}

  def ::(o:TObject) = o :: List[TObject](this)
} 