package su.msk.dunno.scage2.prototypes

import org.apache.log4j.Logger
import su.msk.dunno.scage2.handlers.controller.Controller
import su.msk.dunno.scage2.handlers.{Idler, Renderer}

object Screen {
  private var isAllStop = false
  def stopApp = isAllStop = true
}

class Screen(val name:String, val isMain:Boolean) {
  def this(name:String) = this(name, false)

  private val log = Logger.getLogger(this.getClass);
  log.debug("starting "+name+"...")

  private var handlers = List[Handler]()
  def getHandlers() = handlers
  def addHandler(h:Handler) = {
	  handlers = h :: handlers
	  log.debug("loaded handler "+h.getClass.getName)
  }
  def addHandlers(h:List[Handler]) = {handlers = h ::: handlers}

  val controller = new Controller(this)
  val renderer = new Renderer(this)
  val idler = new Idler(this)

  var onPause:Boolean = false
  def switchPause() = onPause = !onPause

  private var isRunning = true
  def start() = {
    isRunning = true
    handlers.foreach(h => h.initSequence)
    run()
  }
  def stop() = {isRunning = false}

  private def run():Unit = {
    if(isRunning && !Screen.isAllStop) {
      handlers.foreach(h => h.actionSequence)
        run
    }
    else {
      handlers.foreach(h => h.exitSequence)
      log.debug(name+" was stopped")
    }
  }
}