package net.scage

import handlers.controller2.{ScageController, MultiController, SingleController}
import handlers.Renderer
import com.weiglewilczek.slf4s.Logger

abstract class Screen(unit_name:String = "Scage App", is_main_unit:Boolean = false, properties:String = "")
extends Scage(unit_name, is_main_unit, properties) with Renderer with ScageController {
  private val log = Logger(this.getClass.getName)

  // I could override del operations instead to not delete action operations from here (checlControls(), render() etc),
  // or I could set new operation type - some kind of 'important' ops...
  override def run() {
    if(!is_main_unit) log.info("starting unit "+unit_name+"...")
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
    exit()
    dispose()
    log.info(unit_name+" was stopped")
    if(is_main_unit/* && !Scage.isAppRunning*/) {
      exitRender()
      System.exit(0)
    }
  }
}

class ScageScreen(unit_name:String = "Scage App", is_main_unit:Boolean = false, properties:String = "")
extends Screen(unit_name, is_main_unit, properties) with SingleController

class MultiControlledScreen(unit_name:String = "Scage App", is_main_unit:Boolean = false, properties:String = "")
extends Screen(unit_name, is_main_unit, properties) with MultiController
