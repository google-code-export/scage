package su.msk.dunno.scage.prototypes

import su.msk.dunno.scage.main.Engine

abstract class THandler {
  Engine.addHandler(this)
  	
  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def exitSequence():Unit = {}

  def ::(o:THandler) = o :: List[THandler](this)
}