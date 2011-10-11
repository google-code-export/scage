package net.scage

import handlers.controller._
import handlers.Renderer
import org.apache.log4j.Logger
import _root_.net.scage.support.Vec
import _root_.net.scage.support.ScageProperties
import org.lwjgl.input.Mouse
import collection.mutable.HashMap
import net.scage.support.ScageId._

object ScageScreen {
  private var is_al_screens_stop = false
  def isAppRunning = !is_al_screens_stop
  def stopApp() {is_al_screens_stop = true}

  private var current_operation_id = 0
  var currentOperation = current_operation_id
}

import ScageScreen._

class ScageScreen(val screen_name:String = "Scage App", is_main_screen:Boolean = false, properties:String = "") {
  protected val log = Logger.getLogger(this.getClass)

  if(is_main_screen) log.info("starting main screen "+screen_name+"...")
  if(properties != "") ScageProperties.properties = properties
  else if(is_main_screen) ScageProperties.properties = screen_name.toLowerCase+".properties"

  private var inits:List[(Int, () => Any)] = Nil
  def init(init_func: => Any) = {
    val operation_id = /*nextOperationId*/nextId
    inits = (operation_id, () => init_func) :: inits
    if(is_running) init_func
    operation_id
  }
  def delInits(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val old_inits_size = inits.size
        inits = inits.filterNot(_._1 == operation_id)
        val deletion_result = inits.size != old_inits_size
        if(deletion_result) log.debug("deleted init operation with id "+operation_id)
        else log.warn("operation with id "+operation_id+" not found among inits so wasn't deleted")
        overall_result && deletion_result
      })
    } else {
      inits = Nil
      log.info("deleted all init operations")
      true
    }
  }

  // (operation_id, operation, is_pausable)
  private var actions:List[(Int, () => Any, Boolean)] = Nil
  private def addAction(operation: => Any, is_pausable:Boolean) = {
    val operation_id = nextId
    actions = (operation_id, () => operation, is_pausable) :: actions
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
  def actionWithStaticPeriod(action_period:Long)(action_func: => Unit) = {
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
  def actionWithStaticPeriodNoPause(action_period:Long)(action_func: => Unit) = {
    if(action_period > 0) {
      val action_waiter = new ActionWaiterStatic(action_period, action_func)
      addAction(action_waiter.doAction(), false)
    } else addAction(action_func, false)
  }

  def delActions(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val old_actions_size = actions.size
        actions = actions.filterNot(_._1 == operation_id)
        val deletion_result = actions.size != old_actions_size
        if(deletion_result) log.debug("deleted action operation with id "+operation_id)
        else log.warn("operation with id "+operation_id+" not found among actions so wasn't deleted")
        overall_result && deletion_result
      })
    }
    else {
      actions = Nil
      log.info("deleted all action operations")
      true
    }
  }

  private var exits:List[(Int, () => Any)] = Nil
  def exit(exit_func: => Any) = {
    val operation_id = /*nextOperationId*/nextId
    exits = (operation_id, () => exit_func) :: exits
    operation_id
  }
  def delExits(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val old_exits_size = exits.size
        exits = exits.filterNot(_._1 == operation_id)
        val deletion_result = exits.size != old_exits_size
        if(deletion_result) log.debug("deleted exit operation with id "+operation_id)
        else log.warn("operation with id "+operation_id+" not found among exits so wasn't deleted")
        overall_result && deletion_result
      })
    }
    else {
      exits = Nil
      log.info("deleted all exit operations")
      true
    }
  }

  private val controller = new Controller
  def key(key: => Int, repeatTime: => Long = 0, onKeyDown: => Any, onKeyUp: => Any = {}) {
    controller.addListener(new KeyListener(key, repeatTime, onKeyDown, onKeyUp))
    //controller.key(key, repeatTime, onKeyDown, onKeyUp)
  }
  def anykey(onKeyDown: => Any, onKeyUp: => Any = {}) {
    controller.addListener(new AnyKeyListener(onKeyDown, onKeyUp))
  }
  def leftMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    controller.addListener(new MouseButtonsListener(0, repeatTime, onBtnDown, onBtnUp))
    //controller.leftMouse(repeatTime, onBtnDown, onBtnUp)
  }
  def rightMouse(repeatTime: => Long = 0, onBtnDown: Vec => Any, onBtnUp: Vec => Any = Vec => {}) {
    controller.addListener(new MouseButtonsListener(1, repeatTime, onBtnDown, onBtnUp))
    //controller.rightMouse(repeatTime, onBtnDown, onBtnUp)
  }
  def mouseMotion(onMotion: Vec => Any) {
    controller.addListener(new MouseMotionListener(onMotion))
  }
  def leftMouseDrag(onMotion: Vec => Any) {
    controller.addListener(new MouseDragListener(0, onMotion))
  }
  def rightMouseDrag(onMotion: Vec => Any) {
    controller.addListener(new MouseDragListener(1, onMotion))
  }
  def mouseWheelUp(onWheelUp: Vec => Any) {
    controller.addListener(new MouseWheelFactory().wheelUpListener(onWheelUp))
  }
  def mouseWheelDown(onWheelDown: Vec => Any) {
    controller.addListener(new MouseWheelFactory().wheelDownListener(onWheelDown))
  }
  def mouseCoord = Vec(Mouse.getX, Mouse.getY)

  private val renderer = new Renderer
  def render(render_func: => Unit) = {renderer.render(render_func)}
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
      currentOperation = init_id
      init_operation()
    }
  }
  def exit() {
    log.info(screen_name+": exit")
    for((exit_id, exit_operation) <- exits) {
      currentOperation = exit_id
      exit_operation()
    }
  }
  def run() {
    if(!is_main_screen) log.info("starting screen "+screen_name+"...")
    init()
    is_running = true
    log.info(screen_name+": run")
    while(is_running && !is_al_screens_stop) {
      controller.checkControls
      for((action_id, action_operation, is_action_pausable) <- actions) {
        currentOperation = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
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
    if(is_main_screen) stopApp()
    else is_running = false 
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

  private[ScageScreen] abstract class ActionWaiter(action_func: => Unit) {
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
