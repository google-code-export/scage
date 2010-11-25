package su.msk.dunno.blame.livings

import su.msk.dunno.blame.field.{FieldObject, FieldTracer}
import su.msk.dunno.screens.support.tracer.State
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.prototypes.Living

class Killy(point:Vec) extends Living(point) {
  override val trace = FieldTracer.addTrace(new FieldObject {
    def getCoord = FieldTracer.pointCenter(point)
    def getSymbol = PLAYER
    def getColor = RED
    def isTransparent = true
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })

  override def name = "Killy"
  override def isPlayer = true
  
  FieldTracer.addLightSource(point)
}

class Cibo(point:Vec) extends Living(point) {
  override val trace = FieldTracer.addTrace(new FieldObject {
    def getCoord = FieldTracer.pointCenter(point)
    def getSymbol = PLAYER
    def getColor = BLUE
    def isTransparent = true
    def isPassable = false

    def getState = new State
    def changeState(s:State) = {}
  })

  override def name = "Cibo"
  override def isPlayer = true
  
  FieldTracer.addLightSource(point)
}
