package su.msk.dunno.scage.prototypes

import su.msk.dunno.scage.main.Scage
import org.apache.log4j.Logger

abstract class Handler {
  protected val log = Logger.getLogger(this.getClass)

  Scage.addHandler(this)
  	
  def initSequence():Unit = {}
  def actionSequence():Unit = {}
  def exitSequence():Unit = {}

  val handler_type = "default"

  if(Scage.isRunning) initSequence

  def ::(o:Handler) = o :: List[Handler](this)
}