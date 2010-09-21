package su.msk.dunno.scage.main

import java.util.Properties
import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.handlers.{AI, Physics, Idler, Renderer}
import org.apache.log4j.Logger
import java.io.{FileNotFoundException, FileInputStream}

object Scage {
  private val log = Logger.getLogger(Scage.getClass);
  log.debug("starting scage...")
  
  private val properties:Properties = {
    if(properties == null) {
      val p:Properties = new Properties()
      try{p.load(new FileInputStream("options.txt"))}
      catch {
        case ex:FileNotFoundException =>
          log.debug("failed to load properties: options.txt not found")
          System.exit(0)
      }
      p
    }
    else properties
  }
  def getProperty(key:String):String = {
    val s = properties.getProperty(key)
    log.debug("read property "+key+": "+s)
    s
  }
  def getIntProperty(key:String):Int = {
    val i = Integer.valueOf(properties.getProperty(key)).intValue
    log.debug("read property "+key+": "+i)
    i
  }
  def getFloatProperty(key:String):Float = {
    val f = java.lang.Float.valueOf(properties.getProperty(key)).floatValue
    log.debug("read property "+key+": "+f)
    f
  }
  def getBooleanProperty(key:String):Boolean = {
    val b= properties.getProperty(key).equalsIgnoreCase("yes")
    log.debug("read property "+key+": "+b)
    b
  }

  private var handlers = List[THandler]()
  def getHandlers() = handlers
  def setDefaultHandlers() = {Controller; Physics; AI; Renderer; Idler;}
  def addHandler(h:THandler) = {
	  handlers = h :: handlers
	  log.debug("loaded handler "+h.getClass.getName)
  }
  def addHandlers(h:List[THandler]) = {handlers = h ::: handlers}

  var on_pause:Boolean = false
  def switchPause() = on_pause = !on_pause
  
  private var is_running = true
  def isRunning = is_running
  def start() = {
    Idler
    is_running = true
    handlers.foreach(h => h.initSequence)
    run()
  }
  def stop() = {is_running = false}

  private def run():Unit = {
    while(is_running) {
      handlers.foreach(h => h.actionSequence)
    }
    handlers.foreach(h => h.exitSequence)
    log.debug("app was stopped")
    System.exit(0)
  }

  def main(args:Array[String]):Unit = {
    val app_classname = getProperty("app")
    log.debug("starting app "+app_classname)
    Class.forName(app_classname).getField("MODULE$").get(null)
    start
  }
}