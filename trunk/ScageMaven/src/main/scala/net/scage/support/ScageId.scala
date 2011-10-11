package net.scage.support

object ScageId extends ScageId(start_id = 10000)

class ScageId(start_id:Int) {
  protected var id = start_id
  def nextId = {
    synchronized  {
      id += 1
      id
    }
  }
}