package su.msk.dunno.scage.support.tracer

import su.msk.dunno.scage.support.Vec

trait Trace[S <: State] {
  private[tracer] var _id = -1
  def id = _id
  def getCoord():Vec
  def getState():S
  def changeState(state:S):Unit
}