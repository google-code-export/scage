package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.prototypes.Living
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.blame.items.TestItem

class Killy(point:Vec) extends Living(point, PLAYER, RED) {
  setStat("name", ScageMessage.xml("player.killy"))
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point)
  
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
}

class Cibo(point:Vec) extends Living(point, PLAYER, BLUE) {
  setStat("name", ScageMessage.xml("player.cibo"))
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point)
}
