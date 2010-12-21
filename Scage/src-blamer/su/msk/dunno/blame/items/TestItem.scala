package su.msk.dunno.blame.items

import su.msk.dunno.blame.prototypes.Item
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.messages.ScageMessage._
import su.msk.dunno.blame.support.MyFont

class TestItem extends Item(
  name = xml("testitem.name"),
  description = xml("testitem.description"),
  symbol = MyFont.BULLET,
  color = RED) {

}
