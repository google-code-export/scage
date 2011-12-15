package net.scage

import handlers.controller2.ScageController
import handlers.Renderer
import org.lwjgl.input.Mouse
import net.scage.support.ScageId._
import support.{ScageProperties, Vec}
import collection.mutable.{ArrayBuffer, HashMap}
import com.weiglewilczek.slf4s.{Logging, Logger}

object ScageScreen {
  private var is_all_screens_stop = false
  def isAppRunning = !is_all_screens_stop
  def stopApp() {is_all_screens_stop = true}

  private var current_operation_id = 0
  def currentOperation = current_operation_id
}

import ScageScreen._

class ScageScreen(val screen_name:String = "Scage App", val is_main_screen:Boolean = false, properties:String = "") {
  protected val log = Logger(this.getClass.getName)
  if(is_main_screen) log.info("starting main screen "+screen_name+"...")
  if(properties != "") ScageProperties.properties = properties
  else if(is_main_screen) ScageProperties.properties = screen_name.toLowerCase+".properties"

  private val inits = ArrayBuffer[(Int, () => Any)]()
  def init(init_func: => Any) = {
    val operation_id = nextId
    inits += (operation_id, () => init_func)
    if(is_running) init_func
    operation_id
  }
  def delInits(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val deletion_result = inits.find(_._1 == operation_id) match {
          case Some(i) => {
            inits -= i
            log.debug("deleted init operation with id "+operation_id)
            true
          }
          case None => {
            log.warn("operation with id "+operation_id+" not found among inits so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    } else {
      inits.clear()
      log.info("deleted all init operations")
      true
    }
  }

  // (operation_id, operation, is_pausable)
  private var actions = ArrayBuffer[(Int, () => Any, Boolean)]()
  private def addAction(operation: => Any, is_pausable:Boolean) = {
    val operation_id = nextId
    actions += (operation_id, () => operation, is_pausable)
    operation_id
  }

  // pausable actions
  def action(action_func: => Any) = {
    addAction(action_func, true)
  }
  def action(action_period: => Long)(action_func: => Unit) = {
    val action_waiter = new ActionWaiterDynamic(action_period, action_func)
    addAction(action_waiter.doAction(), true)
  }
  def actionWithStaticPeriod(action_period:Long)(action_func: => Unit) = {  // TODO: мб ActionStaticPeriod
    if(action_period > 0) {
      val action_waiter = new ActionWaiterStatic(action_period, action_func)
      addAction(action_waiter.doAction(), true)
    } else addAction(action_func, true)
  }

  // non-pausable variants
  def actionNoPause(action_func: => Any) = {
    addAction(action_func, false)
  }
  def actionNoPause(action_period: => Long)(action_func: => Unit) = {
    val action_waiter = new ActionWaiterDynamic(action_period, action_func)
    addAction(action_waiter.doAction(), false)
  }
  def actionWithStaticPeriodNoPause(action_period:Long)(action_func: => Unit) = { // TODO: мб ActionStaticPeriodNoPause
    if(action_period > 0) {
      val action_waiter = new ActionWaiterStatic(action_period, action_func)
      addAction(action_waiter.doAction(), false)
    } else addAction(action_func, false)
  }

  def delActions(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val deletion_result = actions.find(_._1 == operation_id) match {
          case Some(a) => {
            actions -= a
            log.debug("deleted action operation with id "+operation_id)
            true
          }
          case None => {
            log.warn("operation with id "+operation_id+" not found among actions so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    } else {
      actions.clear()
      log.info("deleted all action operations")
      true
    }
  }

  private var exits = ArrayBuffer[(Int, () => Any)]()
  def exit(exit_func: => Any) = {
    val operation_id = nextId
    exits += (operation_id, () => exit_func)
    operation_id
  }
  def delExits(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val deletion_result = exits.find(_._1 == operation_id) match {
          case Some(e) => {
            exits -= e
            log.debug("deleted exit operation with id "+operation_id)
            true
          }
          case None => {
            log.warn("operation with id "+operation_id+" not found among exits so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    } else {
      exits.clear()
      log.info("deleted all exit operations")
      true
    }
  }

  private val controller = ScageController()
  def key(key: => Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
    controller.key(key, repeatTime, onKeyDown, onKeyUp)
  }
  def anykey(onKeyDown: => Any) {
    controller.anykey(onKeyDown)
  }
  def leftMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    controller.leftMouse(repeatTime, onBtnDown, onBtnUp)
  }
  def rightMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    controller.rightMouse(repeatTime, onBtnDown, onBtnUp)
  }
  def mouseMotion(onMotion: Vec => Any) {
    controller.mouseMotion(onMotion)
  }
  def leftMouseDrag(onDragMotion: Vec => Any) {
    controller.leftMouseDrag(onDragMotion)
  }
  def rightMouseDrag(onDragMotion: Vec => Any) {
    controller.rightMouseDrag(onDragMotion)
  }
  def mouseWheelUp(onWheelUp: Vec => Any) {
    controller.mouseWheelUp(onWheelUp)
  }
  def mouseWheelDown(onWheelDown: Vec => Any) {
    controller.mouseWheelDown(onWheelDown)
  }
  def mouseCoord = Vec(Mouse.getX, Mouse.getY)

  private val renderer = new Renderer
  def render(render_func: => Unit) = {renderer.render(render_func)}
  def render(position:Int)(render_func: => Unit) = {renderer.render(position)(render_func)}
  def delRenders(render_ids:Int*) = renderer.delRenders(render_ids:_*)
  def interface(interface_func: => Unit) = {renderer.interface(interface_func)}
  def delInterfaces(interface_ids:Int*) = renderer.delInterfaces(interface_ids:_*)

  def scale = renderer.scale
  def scale_= (value:Float) {renderer.scale = value}
  
  def windowCenter = renderer.windowCenter
  def windowCenter_= (coord: => Vec) {renderer.windowCenter = coord}

  def center = renderer.center
  def center_= (coord: => Vec) {renderer.center = coord}

  // will be uncommented on real purpose appeared
  /*def delOperation(operation_id:Int) = {
    delInit(operation_id)   ||
    delAction(operation_id) ||
    delExit(operation_id)   ||
    delRender(operation_id) ||
    delInterface(operation_id)
  }*/

  private var on_pause = false
  private def logPause() {log.info("pause = " + on_pause)}
  def onPause = on_pause
  def switchPause() {on_pause = !on_pause; logPause()}
  def pause() {on_pause = true; logPause()}
  def pauseOff() {on_pause = false; logPause()}

  private var is_running = false
  def isRunning = is_running
  def init() {
    log.info(screen_name+": init")
    for((init_id, init_operation) <- inits) {
      current_operation_id = init_id
      init_operation()
    }
  }
  def exit() {
    log.info(screen_name+": exit")
    for((exit_id, exit_operation) <- exits) {
      current_operation_id = exit_id
      exit_operation()
    }
  }
  def run() {
    if(!is_main_screen) log.info("starting screen "+screen_name+"...")
    init()
    is_running = true
    log.info(screen_name+": run")
    while(is_running && !is_all_screens_stop) {
      controller.checkControls()
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
      renderer.render(current_operation_id = _)
    }
    exit()
    log.info(screen_name+" was stopped")
    if(is_main_screen) {
      renderer.exitRender()
      System.exit(0)
    }
  }
  def stop() {
    is_running = false
    if(is_main_screen) stopApp()
  }

  private val events = new HashMap[String, List[() => Unit]]()
  def onEvent(event_name:String)(event_action: => Unit) {
    if(events.contains(event_name)) events(event_name) = (() => event_action) :: events(event_name)
    else events += (event_name -> List(() => event_action))
  }
  def callEvent(event_name:String) {
    if(events.contains(event_name)) events(event_name).foreach(event_action => event_action())
    else log.warn("event "+event_name+" not found")
  }

  private[ScageScreen] sealed abstract class ActionWaiter(action_func: => Unit) {
    private var last_action_time:Long = 0
    protected def period:Long

    def doAction() {
      if(System.currentTimeMillis - last_action_time > period) {
        action_func
        last_action_time = System.currentTimeMillis
      }
    }
  }
  private[ScageScreen] class ActionWaiterDynamic(action_period: => Long, action_func: => Unit) extends ActionWaiter(action_func) {
    def period = action_period
  }
  private[ScageScreen] class ActionWaiterStatic(val period:Long, action_func: => Unit) extends ActionWaiter(action_func)
}
