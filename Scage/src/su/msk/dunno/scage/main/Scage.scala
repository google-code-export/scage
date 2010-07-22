package su.msk.dunno.scage.main

import java.util.Properties
import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.prototypes.{THandler}
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

  var onPause:Boolean = false
  def switchPause() = onPause = !onPause
  
  private var isRunning = true
  def start() = {
    Idler
    isRunning = true
    handlers.foreach(h => h.initSequence)
    run()
  }
  def stop() = {isRunning = false}

  private def run():Unit = {
    if(isRunning) {
      handlers.foreach(h => h.actionSequence)
        run
    }
    else {
      handlers.foreach(h => h.exitSequence)
      log.debug("app was stopped")
    }
  }

  def main(args:Array[String]):Unit = {
    val app_classname = getProperty("app")
    log.debug("starting app "+app_classname)
    Class.forName(app_classname).getField("MODULE$").get(null)
    start
  }
}