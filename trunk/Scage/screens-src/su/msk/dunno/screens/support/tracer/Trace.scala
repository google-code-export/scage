package su.msk.dunno.screens.support.tracer

import su.msk.dunno.scage.support.Vec

trait Trace {
  private[tracer] var _id = -1
  def id = _id
  def getCoord():Vec
  def getState():State
  def changeState(state:State):Unit
}