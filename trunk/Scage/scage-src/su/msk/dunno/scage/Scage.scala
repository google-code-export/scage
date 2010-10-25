package su.msk.dunno.scage

import su.msk.dunno.scage.handlers.Idler
import org.apache.log4j.Logger
import su.msk.dunno.scage.support.ScageProperties

object Scage {
  private val log = Logger.getLogger(Scage.getClass);
  log.info("starting scage...")

  private var init_list:List[() => Unit] = Nil
  def init(init_func: => Unit) = if(is_running) init_func else init_list = (() => init_func) :: init_list

  private var action_list:List[() => Unit] = Nil
  def action(action_func: => Unit) = action_list = (() => action_func) :: action_list
  def action(action_period:Long)(action_func: => Unit) =
    action_list = new ActionWaiter(action_period, action_func).doAction :: action_list

  private var exit_list:List[() => Unit] = Nil
  def exit(exit_func: => Unit) = exit_list = (() => exit_func) :: exit_list

  var on_pause:Boolean = false
  def switchPause = on_pause = !on_pause
  
  private var is_running = false
  def isRunning = is_running
  def start = {
    Idler
    run
  }
  private def run = {
    init_list.foreach(init_func => init_func())
    is_running = true
    while(is_running) action_list.foreach(action_func => action_func())
    exit_list.foreach(exit_func => exit_func())
    log.info("app was stopped")
    System.exit(0)
  }
  def stop = is_running = false

  def main(args:Array[String]):Unit = {
    val app_classname = ScageProperties.stringProperty("app")
    log.info("starting app "+app_classname)
    Class.forName(app_classname).getField("MODULE$").get(null)
    start
  }

  private[Scage] class ActionWaiter(period:Long, action_func: => Unit) {
    private var last_action_time:Long = 0

    def doAction = () => {
      if(System.currentTimeMillis - last_action_time > period) {
        action_func
        last_action_time = System.currentTimeMillis
      }
    }
  }
}