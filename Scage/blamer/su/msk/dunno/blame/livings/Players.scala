package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.prototypes.Living

class Killy(point:Vec) extends Living(point, PLAYER, RED) {
  setStat("name", "Killy")
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point)
}

class Cibo(point:Vec) extends Living(point, PLAYER, BLUE) {
  setStat("name", "Cibo")
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point)
}
