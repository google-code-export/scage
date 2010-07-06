package su.msk.dunno.scage.main

import java.util.Properties
import java.io.FileInputStream
import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.prototypes.{THandler}
import su.msk.dunno.scage.handlers.{AI, Physics, Idler, Renderer}
import org.apache.log4j.Logger;

object Engine {
  private val log = Logger.getLogger(Engine.getClass);
  log.debug("starting scage...")
  
  private val properties:Properties = {
    if(properties == null) {
      val p:Properties = new Properties()
      p.load(new FileInputStream("options.txt"))
      p
    }
    else properties
  }
  def getProperty(key:String):String = properties.getProperty(key)
  def getIntProperty(key:String):Int = Integer.valueOf(properties.getProperty(key)).intValue
  def getFloatProperty(key:String):Float = java.lang.Float.valueOf(properties.getProperty(key)).floatValue
  def getBooleanProperty(key:String):Boolean = properties.getProperty(key).equalsIgnoreCase("yes")

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
	log.debug("starting game...")
    isRunning = true
    handlers.foreach(h => h.initSequence)
    run()
  }
  def stop() = {isRunning = false}

  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  var fps:Int = 0
  def countFPS() = {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }

  private def run():Unit = {
	if(isRunning) {
	  handlers.foreach(h => h.actionSequence)
      countFPS
      run
	}
	else {
		handlers.foreach(h => h.exitSequence)
		log.debug("game was stopped")
	}
  }
}