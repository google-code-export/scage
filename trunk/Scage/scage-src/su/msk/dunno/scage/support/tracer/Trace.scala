package su.msk.dunno.scage.support.tracer

import su.msk.dunno.scage.support.Vec

trait Trace[S <: State] {
  val id = Tracer.nextTraceID
  def isActive:Boolean = true
  def getCoord():Vec
  def getState():S
  def changeState(state:S):Unit
}