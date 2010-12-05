package su.msk.dunno.screens

import handlers.controller.Controller
import handlers.Renderer
import org.apache.log4j.Logger
import prototypes.{Renderable, ActionHandler}
import su.msk.dunno.scage.support.{Vec, ScageProperties}

object ScageScreen {
  private var isAllScreensStop = false
  def isAppRunning = !isAllScreensStop
  def allStop = isAllScreensStop = true
}

class ScageScreen(val screen_name:String, val is_main_screen:Boolean = false, properties:String = "") {
  private val log = Logger.getLogger(this.getClass);
  log.info("starting "+screen_name+"...")

  if(!"".equals(properties)) ScageProperties.properties = properties
  
  private var handlers:List[ActionHandler] = Nil
  def addHandler(handler:ActionHandler, period:Long = 0) = 
    if(period > 0) handlers = new ActionWaiter(period, handler) :: handlers
    else handlers = handler :: handlers

  val controller = new Controller
  def keyListener(key:Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) =
    controller.keyListener(key, repeatTime, onKeyDown, onKeyUp)

  val renderer = if(is_main_screen) new Renderer(main_screen = this) else new Renderer
  def addRender(render:Renderable) = renderer.addRender(render)

  def scale = renderer.scale
  def scale_= (value:Float) = renderer.scale = value
  
  def windowCenter = renderer.windowCenter
  def windowCenter_= (coord: => Vec) = renderer.windowCenter = coord

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
    while(is_running && !ScageScreen.isAllScreensStop) {
      controller.checkControls
      handlers.foreach(handler => handler.action)
      renderer.render
    }
    handlers.foreach(handler => handler.exit)
    log.info(screen_name+" was stopped")
    if(is_main_screen) System.exit(0)
  }
  def stop = {
    if(is_main_screen) ScageScreen.allStop
    else is_running = false 
  }

  private[ScageScreen] class ActionWaiter(val period:Long, private val action_handler:ActionHandler) extends ActionHandler {
    private var last_action_time:Long = 0

    override def action {
      if(System.currentTimeMillis - last_action_time > period) {
        action_handler.action
        last_action_time = System.currentTimeMillis
      }
    }
  }
}
