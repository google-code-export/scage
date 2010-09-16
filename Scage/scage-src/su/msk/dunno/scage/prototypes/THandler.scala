package su.msk.dunno.scage.prototypes

import su.msk.dunno.scage.main.Scage
import org.apache.log4j.Logger

abstract class THandler {
  protected val log = Logger.getLogger(this.getClass)

  Scage.addHandler(this)
  	
  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def exitSequence():Unit = {}

  def ::(o:THandler) = o :: List[THandler](this)
}