package su.msk.dunno.blame.items

import su.msk.dunno.blame.prototypes.Item
import su.msk.dunno.scage.single.support.ScageColor
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.scage.single.support.Vec

abstract class Modifier(name:String, description:String, symbol:Int, color:ScageColor)
extends Item(name, description, symbol, color) {
  setStat("modifier")
}

class SocketExtender extends Modifier(
  name = xml("item.extender.name"),
  description = xml("item.extender.description"),
  symbol = BULLET,
  color = WHITE) {
  setStat("extender")
}

class EnergyItem extends Modifier(
  name = xml("item.energy.name"),
  description = xml("item.energy.description"),
  symbol = BULLET,
  color = YELLOW) {
  setStat("max_energy", new State("effect", 10))
  setStat("energy_increase_rate", new State("effect", 1))
}

class HealthItem extends Modifier(
  name = xml("item.health.name"),
  description = xml("item.health.description"),
  symbol = BULLET,
  color = MAROON) {
  setStat("max_health", new State("effect", 10))
}

class ShieldItem extends Modifier(
  name = xml("item.shield.name"),
  description = xml("item.shield.description"),
  symbol = BULLET,
  color = CYAN) {
  setStat("max_shield", new State("effect", 10))
  setStat("shield_increase_rate", new State("effect", 1))
}

class DamageItem extends Modifier(
  name = xml("item.damage.name"),
  description = xml("item.damage.description"),
  symbol = BULLET,
  color = RED) {
  setStat("damage", new State("effect", 10))
}

abstract class UniqueItem(name:String, description:String, symbol:Int, color:ScageColor)
extends Modifier(name, description, symbol, color) {
  setStat("unique")
}

class KickItem extends UniqueItem(
  name = xml("item.kick.name"),
  description = xml("item.kick.description"),
  symbol = BULLET,
  color = DARK_GREEN) {
  setStat("kick",
    new State("conditions",
      new State().put("one", new State("damage", Vec(-1,0)))
                 .put("two", new State("damage", Vec(1,0)))
    )
  )
}