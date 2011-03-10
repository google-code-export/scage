package su.msk.dunno.scage.screens

import handlers.controller.Controller
import handlers.{Idler, Renderer}
import org.apache.log4j.Logger
import prototypes.{ScageRender, ScageAction}
import su.msk.dunno.scage.single.support.{Vec}
import su.msk.dunno.scage.single.support.ScageProperties

object ScageScreen {
  private var isAllScreensStop = false
  def isAppRunning = !isAllScreensStop
  def allStop = isAllScreensStop = true
}

class ScageScreen(val screen_name:String, val is_main_screen:Boolean = false, properties:String = "") {
  protected val log = Logger.getLogger(this.getClass);

  if(is_main_screen) log.info("starting "+screen_name+"...")
  if(!"".equals(properties)) ScageProperties.properties = properties
  
  /*private var handlers:List[ScageAction] = Nil
  def addAction(handler:ScageAction, period:Long = 0) =
    if(period > 0) handlers = new ActionWaiter(period, handler) :: handlers
    else handlers = handler :: handlers*/

  private var inits:List[() => Unit] = Nil
  def init(init_func: => Unit) = if(is_running) inits else inits = (() => init_func) :: inits

  private var actions:List[() => Unit] = Nil
  def action(action_func: => Unit) = actions = (() => action_func) :: actions
  def action(action_period:Long)(action_func: => Unit) =
    actions = new ActionWaiter(action_period, action_func).doAction :: actions

  private var exits:List[() => Unit] = Nil
  def exit(exit_func: => Unit) = exits = (() => exit_func) :: exits

  val controller = new Controller
  def keyListener(key: => Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) =
    controller.keyListener(key, repeatTime, onKeyDown, onKeyUp)

  val renderer = if(is_main_screen) new Renderer(main_screen = this) else new Renderer
  /*def addRender(render:ScageRender) = renderer.addRender(render)*/
  def render(render_func: => Unit) = renderer.render(render_func)
  def interface(interface_func: => Unit) = renderer.interface(interface_func)

  def scale = renderer.scale
  def scale_= (value:Float) = renderer.scale = value
  
  def windowCenter = renderer.windowCenter
  def windowCenter_= (coord: => Vec) = renderer.windowCenter = coord

  def center = renderer.center
  def center_= (coord: => Vec) = renderer.center = coord

  private var on_pause = false
  def onPause = on_pause
  def switchPause = on_pause = !on_pause
  def pause = on_pause = true
  def pauseOff = on_pause = false

  private var is_running = false
  def isRunning = is_running
  def init = {
    /*handlers.foreach(handler => handler.init)*/
    log.info(screen_name+": init")
    inits.foreach(init_func => init_func())
  }
  def exit = {
    /*handlers.foreach(handler => handler.exit)*/
    log.info(screen_name+": exit")
    exits.foreach(exit_func => exit_func())
  }
  def run = {
    if(!is_main_screen) log.info("starting "+screen_name+"...")
    init
    is_running = true
    log.info(screen_name+": run")
    while(is_running && !ScageScreen.isAllScreensStop) {
      controller.checkControls
      if(!on_pause) /*handlers.foreach(handler => handler.action)*/ actions.foreach(action_func => action_func())
      renderer.render
    }
    exit
    log.info(screen_name+" was stopped")
    if(is_main_screen) System.exit(0)
  }
  def stop = {
    if(is_main_screen) ScageScreen.allStop
    else is_running = false 
  }

  /*private[ScageScreen] class ActionWaiter(val period:Long, private val action_handler:ScageAction) extends ScageAction {
    private var last_action_time:Long = 0

    override def action {
      if(System.currentTimeMillis - last_action_time > period) {
        action_handler.action
        last_action_time = System.currentTimeMillis
      }
    }
  }*/

  private[ScageScreen] class ActionWaiter(period:Long, action_func: => Unit) {
    private var last_action_time:Long = 0

    def doAction = () => {
      if(System.currentTimeMillis - last_action_time > period) {
        action_func
        last_action_time = System.currentTimeMillis
      }
    }
  }
}
