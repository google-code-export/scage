package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.prototypes.Living
import su.msk.dunno.scage.support.messages.ScageMessage._
import su.msk.dunno.blame.items.{SocketExtender, SecondTestItem, TestItem}

class Killy(point:Vec)
extends Living(name        = xml("player.killy.name"),
               description = xml("player.killy.description"),
               point, PLAYER, RED) {
  override def getSymbol = PLAYER
  override def onDeath = {
    super.onDeath
    setStat("name", xml("player.killy.dead.name"))
  }
  setStat("player")
  
  FieldTracer.addLightSource(point, intStat("dov"), trace)
  
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)

  for(i <- 1 to 100)
    inventory.addItem(new SocketExtender)
}

class Cibo(point:Vec)
extends Living(name        = xml("player.cibo.name"),
               description = xml("player.cibo.description"),
               point, PLAYER, BLUE) {
  override def getSymbol = PLAYER
  override def onDeath = {
    super.onDeath
    setStat("name", xml("player.cibo.dead.name"))
  }
  setStat("player")
  
  FieldTracer.addLightSource(point, intStat("dov"), trace)

  inventory.addItem(new SecondTestItem)
  inventory.addItem(new SecondTestItem)
}
