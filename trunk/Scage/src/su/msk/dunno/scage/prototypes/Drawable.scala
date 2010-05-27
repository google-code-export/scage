package su.msk.dunno.scage.prototypes

trait Drawable {
  def render()

  def ::(d:Drawable) = d :: List[Drawable](this)
}