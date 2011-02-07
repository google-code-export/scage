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

class SocketExtender extends Item(
  name = xml("item.extender.name"),
  description = xml("item.extender.description"),
  symbol = BULLET,
  color = WHITE) {
  setStat("extender")
}

class EnergyItem extends Item(
  name = xml("item.energy.name"),
  description = xml("item.energy.description"),
  symbol = BULLET,
  color = YELLOW) {
  setStat("max_energy")
}

class Health extends Item(
  name = xml("item.health.name"),
  description = xml("item.health.description"),
  symbol = BULLET,
  color = MAROON) {
  setStat("max_health")
}

class Shield extends Item(
  name = xml("item.shield.name"),
  description = xml("item.shield.description"),
  symbol = BULLET,
  color = CYAN) {
  setStat("max_shield")
}

class Damage extends Item(
  name = xml("item.damage.name"),
  description = xml("item.damage.description"),
  symbol = BULLET,
  color = RED) {
  setStat("damage")
}

class UniqueItem extends Item(
  name = xml("item.unique.name"),
  description = xml("item.unique.description"),
  symbol = BULLET,
  color = DARK_GREEN) {
  setStat("unique")
}