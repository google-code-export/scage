package su.msk.dunno.screens

import handlers.controller.Controller
import handlers.{Idler, Renderer}
import org.apache.log4j.Logger
import su.msk.dunno.scage.support.Vec

object Screen {
  private var isAllScreensStop = false
  def stopApp = isAllScreensStop = true
}

class Screen(val name:String, val isMain:Boolean) {
  def this(name:String) = this(name, false)

  private val log = Logger.getLogger(this.getClass);
  log.info("starting "+name+"...")

  private var init_list:List[() => Unit] = Nil
  def init(init_func: => Unit) = if(is_running) init_func else init_list = (() => init_func) :: init_list

  private var action_list:List[() => Unit] = Nil
  def action(action_func: => Unit) = action_list = (() => action_func) :: action_list
  def action(action_period:Long)(action_func: => Unit) =
    action_list = new ActionWaiter(action_period, action_func).doAction :: action_list

  private var exit_list:List[() => Unit] = Nil
  def exit(exit_func: => Unit) = exit_list = (() => exit_func) :: exit_list

  val controller = new Controller(this)
  def keyListener(key:Int, repeatTime:Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) =
    controller.keyListener(key, repeatTime, onKeyDown, onKeyUp)

  val renderer = new Renderer(this)
  def scale = renderer.scale
  def scale_= (value:Float) = renderer.scale = value

  def center = renderer.center
  def center_= (coord: => Vec) = renderer.center = coord

  def render(render_func: => Unit) = renderer.render(render_func)
  def interface(interface_func: => Unit) = renderer.interface(interface_func)

  val idler = new Idler(this)
  def fps = idler.fps

  var onPause:Boolean = false
  def switchPause() = onPause = !onPause

  private var is_running = false
  def isRunning = is_running
  def run = {
    init_list.foreach(init_func => init_func())
    is_running = true
    while(is_running && !Screen.isAllScreensStop) action_list.foreach(action_func => action_func())
    exit_list.foreach(exit_func => exit_func())
    log.info(name+" was stopped")
    if(isMain) System.exit(0)
  }
  def stop = {
    if(isMain) Screen.stopApp
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