package su.msk.dunno.scage.handlers.tracer

import su.msk.dunno.scage.support.Vec

trait Trace {
  def getCoord():Vec
  def getState():State
  def changeState(state:State):Unit
}