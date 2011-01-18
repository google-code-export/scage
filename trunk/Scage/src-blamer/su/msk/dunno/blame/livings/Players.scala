package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.prototypes.Living
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.blame.items.{SocketExtender, SecondTestItem, TestItem}

class Killy(point:Vec)
extends Living(name = ScageMessage.xml("player.killy.name"),
               description = ScageMessage.xml("player.killy.description"),
               point, PLAYER, RED) {
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point, intStat("dov"), trace)
  
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new TestItem)
  inventory.addItem(new SocketExtender)
  inventory.addItem(new SocketExtender)
  inventory.addItem(new SocketExtender)
  inventory.addItem(new SocketExtender)
}

class Cibo(point:Vec)
extends Living(name = ScageMessage.xml("player.cibo.name"),
               description = ScageMessage.xml("player.cibo.description"),
               point, PLAYER, BLUE) {
  setStat("is_player", true)
  
  FieldTracer.addLightSource(point, intStat("dov"), trace)

  inventory.addItem(new SecondTestItem)
  inventory.addItem(new SecondTestItem)
  
  override def changeState(s:State) = {
    if(s.contains("damage")) {
      BottomMessages.addPropMessageSameString("changestatus.damage", stat("name"), s.getString("damage"))
    }
  }
}
