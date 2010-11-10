package su.msk.dunno.screens

import handlers.controller.Controller
import handlers.{Idler, Renderer}
import org.apache.log4j.Logger
import prototypes.{Renderable, Handler}
import su.msk.dunno.scage.support.{Vec, ScageProperties}
import org.lwjgl.opengl.Display

object Screen {
  private var isAllScreensStop = false
  def allStop = isAllScreensStop = true
}

class Screen(val name:String, val isMain:Boolean) {
  def this(name:String) = this(name, false)

  private val log = Logger.getLogger(this.getClass);
  log.info("starting "+name+"...")

  def properties:String = "scage-properties.txt"
  ScageProperties.properties = properties
  
  private var handlers:List[Handler] = Nil
  def addHandler(handler:Handler) = handlers = handler :: handlers

  val controller = new Controller
  def keyListener(key:Int, repeatTime:Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) =
    controller.keyListener(key, repeatTime, onKeyDown, onKeyUp)

  val renderer = new Renderer
  def addRender(render:Renderable) = renderer.addRender(render)

  def scale = renderer.scale
  def scale_= (value:Float) = renderer.scale = value

  def center = renderer.center
  def center_= (coord: => Vec) = renderer.center = coord

  def fps = renderer.fps

  var onPause:Boolean = false
  def switchPause() = onPause = !onPause

  private var is_running = false
  def isRunning = is_running
  def run = {
    handlers.foreach(handler => handler.init)
    is_running = true
    while(is_running && !Screen.isAllScreensStop) {
      controller.checkControls
      handlers.foreach(handler => handler.action)
      renderer.render
    }
    handlers.foreach(handler => handler.exit)
    log.info(name+" was stopped")
    if(isMain) {
      Display.destroy
      System.exit(0)
    }
  }
  def stop = {
    if(isMain) Screen.allStop
    else is_running = false 
  }

  private[Screen] class ActionWaiter(period:Long, action_func: => Unit) {
    private var last_action_time:Long = 0

    def doAction = () => {
      if(System.currentTimeMillis - last_action_time > period) {
        action_func
        last_action_time = System.currentTimeMillis
      }
    }
  }
}
