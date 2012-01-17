package net.scage

import handlers.controller2.{ScageController, MultiController, SingleController}
import handlers.Renderer
import com.weiglewilczek.slf4s.Logger
import support.ScageProperties

abstract class Screen(unit_name:String = "Scage Screen")
extends Scage(unit_name) with Renderer with ScageController {
  private val log = Logger(this.getClass.getName)

  // I could override del operations instead to not delete action operations from here (checkControls(), render() etc),
  // or I could set new operation type - some kind of 'important' ops...
  override def run() {
    log.info("starting screen "+unit_name+"...")
    init()
    is_running = true
    log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      checkControls()
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
      render()
    }
    clear()
    dispose()
    log.info(unit_name+" was stopped")
  }
}
abstract class ScreenApp(unit_name:String = "Scage App", properties:String)
extends ScageApp(unit_name, properties) with Renderer with ScageController {
  override def run() {
    init()
    is_running = true
    scage_log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      checkControls()
      for((action_id, action_operation, is_action_pausable) <- actions) {
        current_operation_id = action_id
        if(!on_pause || !is_action_pausable) action_operation()
      }
      render()
    }
    clear()
    dispose()
    scage_log.info(unit_name+" was stopped")
    exitRender()
    System.exit(0)
  }

  override protected def preinit() {
    scage_log.info("starting main screen "+unit_name+"...")
    ScageProperties.properties = properties
    Renderer.initgl
  }
}

class ScageScreen(unit_name:String = "Scage Screen") extends Screen(unit_name) with SingleController

class ScageScreenApp(unit_name:String = "Scage App", properties:String) 
extends ScreenApp(unit_name, properties) with SingleController

/*class MultiControlledScreen(unit_name:String = "Scage App", is_main_unit:Boolean = false, properties:String = "")
extends Screen(unit_name, is_main_unit, properties) with MultiController*/
