package gravitation.objects

import su.msk.dunno.scage.support.{ScageLibrary, Colors, Vec}

// the name is self-explainable
class MaterialPoint(init_coord:Vec, init_velocity:Vec, val mass:Float, val radius:Int) extends ScageLibrary {
  var coord = init_coord
  var velocity = init_velocity
  var color = randomColor
  var consumed = false
}