package su.msk.dunno.scage.screens.support

import su.msk.dunno.scage.single.support.ScageProperties._

object ScageId {
  private var operation_id = property("scageid.start", 10000L)
  require(operation_id >= 10000)

  def nextId = {
    operation_id += 1
    operation_id
  }
}