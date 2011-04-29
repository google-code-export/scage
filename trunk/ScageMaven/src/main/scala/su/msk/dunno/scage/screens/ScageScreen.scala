package su.msk.dunno.scage.screens

import handlers.controller.Controller
import handlers.Renderer
import org.apache.log4j.Logger
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.single.support.ScageProperties

object ScageScreen {
  private var isAllScreensStop = false
  def isAppRunning = !isAllScreensStop
  def allStop() {isAllScreensStop = true}

  private var operation_id = 0
  def nextOperationId = {
    operation_id += 1
    operation_id
  }
}

import ScageScreen._

class ScageScreen(val screen_name:String, val is_main_screen:Boolean = false, properties:String = "") {
  protected val log = Logger.getLogger(this.getClass);

  if(is_main_screen) log.info("starting main screen "+screen_name+"...")
  if(!"".equals(properties)) ScageProperties.properties = properties

  private var inits:List[(Int, () => Unit)] = Nil
  def init(init_func: => Unit) = {
    val operation_id = nextOperationId
    inits = (operation_id, () => init_func) :: inits
    if(is_running) init_func
    operation_id
  }
  // TODO: add boolean return value
  def delInitOperation(operation_id:Int) {
    inits = inits.filterNot(_._1 == operation_id)
  }

  private var actions:List[(Int, () => Unit)] = Nil
  def action(action_func: => Unit) = {
    val operation_id = nextOperationId
    actions = (operation_id, () => action_func) :: actions
    operation_id
  }
  def delActionOperation(operation_id:Int) {
    actions = actions.filterNot(_._1 == operation_id)
  }

  private var exits:List[(Int, () => Unit)] = Nil
  def exit(exit_func: => Unit) = {
    val operation_id = nextOperationId
    exits = (operation_id, () => exit_func) :: exits
    operation_id
  }
  def delExitOperation(operation_id:Int) {
    exits = exits.filterNot(_._1 == operation_id)
  }

  def delOperation(operation_id:Int) {
    delInitOperation(operation_id)
    delActionOperation(operation_id)
    delExitOperation(operation_id)
  }

  val controller = new Controller
  def key(key: => Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
    controller.key(key, repeatTime, onKeyDown, onKeyUp)
  }

  val renderer = new Renderer
  def render(render_func: => Unit) {renderer.render(render_func)}
  def interface(interface_func: => Unit) {renderer.interface(interface_func)}

  def scale = renderer.scale
  def scale_= (value:Float) {renderer.scale = value}
  
  def windowCenter = renderer.windowCenter
  def windowCenter_= (coord: => Vec) {renderer.windowCenter = coord}

  def center = renderer.center
  def center_= (coord: => Vec) {renderer.center = coord}

  private var on_pause = false
  def onPause = on_pause
  def switchPause() {on_pause = !on_pause}
  def pause() {on_pause = true}
  def pauseOff() {on_pause = false}

  private var is_running = false
  def isRunning = is_running
  def init() {
    log.info(screen_name+": init")
    inits.foreach(init_func => init_func._2())
  }
  def exit() {
    log.info(screen_name+": exit")
    exits.foreach(exit_func => exit_func._2())
  }
  def run() {
    if(!is_main_screen) log.info("starting screen "+screen_name+"...")
    init()
    is_running = true
    log.info(screen_name+": run")
    val is_global_pause = ScageProperties.property("pause.global", true)
    while(is_running && !ScageScreen.isAllScreensStop) {
      controller.checkControls
      if(!on_pause || !is_global_pause) actions.foreach(action_func => action_func._2())
      renderer.render()
    }
    exit()
    log.info(screen_name+" was stopped")
    if(is_main_screen) {
      renderer.exitRender()
      System.exit(0)
    }
  }
  def stop() {
    if(is_main_screen) ScageScreen.allStop()
    else is_running = false 
  }
}
