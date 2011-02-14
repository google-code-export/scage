package su.msk.dunno.blame.livings

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.blame.items.{SocketExtender, SecondTestItem, TestItem, KickItem}
import su.msk.dunno.blame.prototypes.Player

class Killy(point:Vec)
extends Player(name        = xml("player.killy.name"),
               description = xml("player.killy.description"),
               point, RED) {
  override def onDeath = {
    super.onDeath
    setStat("name", xml("player.killy.dead.name"))
  }
  
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new KickItem)

  for(i <- 1 to 100)
    inventory.addItem(new SocketExtender)
}

class Cibo(point:Vec)
extends Player(name        = xml("player.cibo.name"),
               description = xml("player.cibo.description"),
               point, BLUE) {
  override def onDeath = {
    super.onDeath
    setStat("name", xml("player.cibo.dead.name"))
  }

  inventory.addItem(new SecondTestItem)
  inventory.addItem(new SecondTestItem)
}
