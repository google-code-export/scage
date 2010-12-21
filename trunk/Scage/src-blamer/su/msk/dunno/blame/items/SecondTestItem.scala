package su.msk.dunno.blame.items

import su.msk.dunno.blame.prototypes.Item
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.messages.ScageMessage._

class SecondTestItem extends Item(
  name = xml("secondtestitem.name"),
  description = xml("secondtestitem.description"),
  symbol = BULLET,
  color = BLUE) {}