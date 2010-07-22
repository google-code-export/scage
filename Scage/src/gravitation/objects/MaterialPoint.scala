package gravitation.objects

import su.msk.dunno.scage.support.{Colors, Vec}

// the name is self-explainable
class MaterialPoint(init_coord:Vec, init_velocity:Vec, val mass:Float, val radius:Int) extends Colors {
  var coord = init_coord
  var velocity = init_velocity
  var color = randomColor
  var consumed = false
}