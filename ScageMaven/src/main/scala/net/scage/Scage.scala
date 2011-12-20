package net.scage

import com.weiglewilczek.slf4s.Logger
import collection.mutable.{HashMap, ArrayBuffer}
import support.ScageProperties
import support.ScageId._

trait Scage {
  def unit_name:String      // those three values _must_ be constructor parameters in any non-virtual child!!!
  def is_main_unit:Boolean
  def properties:String

  private val log = Logger(this.getClass.getName)
  if(properties != "") ScageProperties.properties = properties
  else if(is_main_unit) ScageProperties.properties = unit_name.replaceAll(" ", "").toLowerCase+".properties"

  protected var current_operation_id = 0
  def currentOperation = current_operation_id

  object ScageOperations extends Enumeration {
    val Init, Action, Exit = Value
  }

  protected val operations_mapping = HashMap[Int, Any]()
  def delOperation(operation_id:Int) = {
    operations_mapping.get(operation_id) match {
      case Some(operation_type) => {
        operation_type match {
          case ScageOperations.Init => delInits(operation_id)
          case ScageOperations.Action => delActions(operation_id)
          case ScageOperations.Exit => delExits(operation_id)
          case _ => {
            log.warn("operation with id "+operation_id+" not found so wasn't deleted")
            false
          }
        }
      }
      case None => {
        log.warn("operation with id "+operation_id+" not found so wasn't deleted")
        false
      }
    }
  }
  def deleteSelf() = delOperation(current_operation_id)
  def delOperations(operation_ids:Int*) = {
    if(operation_ids.size > 0) {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val deletion_result = delOperation(operation_id)
        overall_result && deletion_result
      })
    } else {
      delInits() &&
      delActions() &&
      delExits()
    }
  }

  private val inits = ArrayBuffer[(Int, () => Any)]()
  def init(init_func: => Any) = {
    val operation_id = nextId
    inits += (operation_id, () => init_func)
    operations_mapping += operation_id -> ScageOperations.Init
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
    operations_mapping += operation_id -> ScageOperations.Action
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
  def actionStaticPeriod(action_period:Long)(action_func: => Unit) = {  // TODO: мб ActionStaticPeriod
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
  def actionStaticPeriodNoPause(action_period:Long)(action_func: => Unit) = { // TODO: мб ActionStaticPeriodNoPause
    if(action_period > 0) {
      val action_waiter = new ActionWaiterStatic(action_period, action_func)
      addAction(action_waiter.doAction(), false)
    } else addAction(action_func, false)
  }

  private[this] sealed abstract class ActionWaiter(action_func: => Unit) {
    private var last_action_time:Long = 0
    protected def period:Long

    def doAction() {
      if(System.currentTimeMillis - last_action_time > period) {
        action_func
        last_action_time = System.currentTimeMillis
      }
    }
  }
  private[this] class ActionWaiterDynamic(action_period: => Long, action_func: => Unit) extends ActionWaiter(action_func) {
    def period = action_period
  }
  private[this] class ActionWaiterStatic(val period:Long, action_func: => Unit) extends ActionWaiter(action_func)

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
    operations_mapping += operation_id -> ScageOperations.Exit
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

  private var on_pause = false
  private def logPause() {log.info("pause = " + on_pause)}
  def onPause = on_pause
  def switchPause() {on_pause = !on_pause; logPause()}
  def pause() {on_pause = true; logPause()}
  def pauseOff() {on_pause = false; logPause()}

  private var is_running = false
  def isRunning = is_running
  def init() {
    log.info(unit_name+": init")
    for((init_id, init_operation) <- inits) {
      current_operation_id = init_id
      init_operation()
    }
  }
  def exit() {
    log.info(unit_name+": exit")
    for((exit_id, exit_operation) <- exits) {
      current_operation_id = exit_id
      exit_operation()
    }
  }
  def run() {
    log.info("starting "+(if(is_main_unit) "main " else "")+"unit "+unit_name+"...")
    init()
    is_running = true
    log.info(unit_name+": run")
    while(is_running && !Scage.is_all_units_stop) {
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
    }
    exit()
    log.info(unit_name+" was stopped")
    if(is_main_unit) {
      System.exit(0)
    }
  }
  def stop() {
    is_running = false
    if(is_main_unit) Scage.stopApp()
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
}

object Scage {
  private var is_all_units_stop = false
  def isAppRunning = !is_all_units_stop
  def stopApp() {is_all_units_stop = true}
}