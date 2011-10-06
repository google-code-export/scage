package su.msk.dunno.blame.items

import su.msk.dunno.blame.prototypes.Item
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._

class TestItem extends Item(
  name = xml("testitem.name"),
  description = xml("testitem.description"),
  symbol = BULLET,
  color = RED) {}

class SecondTestItem extends Item(
  name = xml("secondtestitem.name"),
  description = xml("secondtestitem.description"),
  symbol = BULLET,
  color = BLUE) {}