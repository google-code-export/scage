package su.msk.dunno.scage.screens.support

import su.msk.dunno.scage.single.support.ScageProperties._

object ScageId extends ScageId(start_id = property("id.start", 10000))

class ScageId(start_id:Int = property("id.start", 10000)) {
  protected var id = start_id
  require(id >= 10000)
  def nextId = {
    id += 1
    id
  }
}