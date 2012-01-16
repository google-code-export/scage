package net.scage

import com.weiglewilczek.slf4s.Logger
import collection.mutable.{HashMap, ArrayBuffer}
import support.ScageProperties
import support.ScageId._

class ScageApp(val unit_name:String = "Scage App", val properties:String) extends ScageTrait with App {
  override def run() {
    init()
    is_running = true
    scage_log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
    }
    exit()
    dispose()
    scage_log.info(unit_name+" was stopped")
    System.exit(0)
  }
  
  override def stop() {
    is_running = false
    Scage.stopApp()
  }

  protected def preinit() {
    scage_log.info("starting main unit "+unit_name+"...")
    ScageProperties.properties = properties
  }
  
  override def main(args:Array[String]) {
    preinit()
    super.main(args)
    run()
  }
}

class Scage(val unit_name:String = "Scage") extends ScageTrait

trait ScageTrait {
  def unit_name:String

  protected val scage_log = Logger(this.getClass.getName)

  protected var current_operation_id = 0
  def currentOperation = current_operation_id

  object ScageOperations extends Enumeration {
    val Init, Action, Exit, Dispose = Value
  }

  protected val operations_mapping = HashMap[Int, Any]()
  def delOperation(operation_id:Int) = {
    operations_mapping.get(operation_id) match {
      case Some(operation_type) => {
        operation_type match {
          case ScageOperations.Init => delInits(operation_id)
          case ScageOperations.Action => delActions(operation_id)
          case ScageOperations.Exit => delExits(operation_id)
          /*case ScageOperations.Dispose => delDisposes(operation_id)*/
          case _ => {
            scage_log.warn("operation with id "+operation_id+" not found so wasn't deleted")
            false
          }
        }
      }
      case None => {
        scage_log.warn("operation with id "+operation_id+" not found so wasn't deleted")
        false
      }
    }
  }
  def deleteSelf() = delOperation(current_operation_id)
  def delOperations(operation_ids:Int*) = {
    operation_ids.foldLeft(true)((overall_result, operation_id) => {
      val deletion_result = delOperation(operation_id)
      overall_result && deletion_result
    })
  }
  def delAllOperations() {
    dellAllInits()
    delAllActions()
    delAllExits()
    /*delAllDisposes()*/
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
    operation_ids.foldLeft(true)((overall_result, operation_id) => {
      val deletion_result = inits.find(_._1 == operation_id) match {
        case Some(i) => {
          inits -= i
          scage_log.debug("deleted init operation with id "+operation_id)
          true
        }
        case None => {
          scage_log.warn("operation with id "+operation_id+" not found among inits so wasn't deleted")
          false
        }
      }
      overall_result && deletion_result
    })
  }
  def dellAllInits() {
    inits.clear()
    scage_log.info("deleted all init operations")
  }

  // (operation_id, operation, is_pausable)
  protected var actions = ArrayBuffer[(Int, () => Any, Boolean)]()
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
    operation_ids.foldLeft(true)((overall_result, operation_id) => {
      val deletion_result = actions.find(_._1 == operation_id) match {
        case Some(a) => {
          actions -= a
          scage_log.debug("deleted action operation with id "+operation_id)
          true
        }
        case None => {
          scage_log.warn("operation with id "+operation_id+" not found among actions so wasn't deleted")
          false
        }
      }
      overall_result && deletion_result
    })
  }
  def delAllActions() {
    actions.clear()
    scage_log.info("deleted all action operations")
  }

  private var exits = ArrayBuffer[(Int, () => Any)]()
  def exit(exit_func: => Any) = {
    val operation_id = nextId
    exits += (operation_id, () => exit_func)
    operations_mapping += operation_id -> ScageOperations.Exit
    operation_id
  }
  def delExits(operation_ids:Int*) = {
    operation_ids.foldLeft(true)((overall_result, operation_id) => {
      val deletion_result = exits.find(_._1 == operation_id) match {
        case Some(e) => {
          exits -= e
          scage_log.debug("deleted exit operation with id "+operation_id)
          true
        }
        case None => {
          scage_log.warn("operation with id "+operation_id+" not found among exits so wasn't deleted")
          false
        }
      }
      overall_result && deletion_result
    })
  }
  def delAllExits() {
    exits.clear()
    scage_log.info("deleted all exit operations")
  }
  
  private var disposes = ArrayBuffer[(Int, () => Any)]()
    def dispose(dispose_func: => Any) = {
      val operation_id = nextId
      disposes += (operation_id, () => dispose_func)
      operations_mapping += operation_id -> ScageOperations.Dispose
      operation_id
    }
    def delDisposes(operation_ids:Int*) = {
      operation_ids.foldLeft(true)((overall_result, operation_id) => {
        val deletion_result = disposes.find(_._1 == operation_id) match {
          case Some(d) => {
            disposes -= d
            scage_log.debug("deleted dispose operation with id "+operation_id)
            true
          }
          case None => {
            scage_log.warn("operation with id "+operation_id+" not found among disposes so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    }
    def delAllDisposes() {
      disposes.clear()
      scage_log.info("deleted all dispose operations")
    }

  protected var on_pause = false
  private def logPause() {scage_log.info("pause = " + on_pause)}
  def onPause = on_pause
  def switchPause() {on_pause = !on_pause; logPause()}
  def pause() {on_pause = true; logPause()}
  def pauseOff() {on_pause = false; logPause()}

  protected var is_running = false
  def isRunning = is_running
  def init() {
    scage_log.info(unit_name+": init")
    for((init_id, init_operation) <- inits) {
      current_operation_id = init_id
      init_operation()
    }
  }
  def exit() {
    scage_log.info(unit_name+": exit")
    for((exit_id, exit_operation) <- exits) {
      current_operation_id = exit_id
      exit_operation()
    }
  }
  def dispose() {
    scage_log.info(unit_name+": dispose")
    for((dispose_id, dispose_operation) <- disposes) {
      current_operation_id = dispose_id
      dispose_operation()
    }
  }
  def run() {
    scage_log.info("starting unit "+unit_name+"...")
    init()
    is_running = true
    scage_log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
    }
    exit()
    dispose()
    scage_log.info(unit_name+" was stopped")
  }

  def stop() {
    is_running = false
  }

  private val events = new HashMap[String, List[() => Unit]]()
  def onEvent(event_name:String)(event_action: => Unit) {
    if(events.contains(event_name)) events(event_name) = (() => event_action) :: events(event_name)
    else events += (event_name -> List(() => event_action))
  }
  def callEvent(event_name:String) {
    if(events.contains(event_name)) events(event_name).foreach(event_action => event_action())
    else scage_log.warn("event "+event_name+" not found")
  }
}

object Scage {
  private var is_all_units_stop = false
  def isAppRunning = !is_all_units_stop
  def stopApp() {is_all_units_stop = true}
}
