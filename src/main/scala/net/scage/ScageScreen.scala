package net.scage

import handlers.controller2.{ScageController, SingleController}
import com.weiglewilczek.slf4s.Logger
import handlers.{RendererInitializer, Renderer}
import support.ScageProperties._

// abstract classes instead of traits to make it easy to use with MultiController
abstract class Screen(val unit_name:String = "Scage Screen") extends Scage with Renderer with ScageController {
  private val log = Logger(this.getClass.getName)

  override def run() {
    log.info("starting screen "+unit_name+"...")
    preinit()
    init()
    is_running = true
    prepareRender()
    log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      checkControls()
      executeActions()
      performRendering()
    }
    clear()
    dispose()
    scage_log.info(unit_name+" was stopped")
  }
}
abstract class ScreenApp(
  val unit_name:String = "Scage App",
  val window_width:Int  = property("screen.width", 800),
  val window_height:Int = property("screen.height", 600),
  val title:String = property("app.name", "Scage App")
) extends Scage with ScageMain with Renderer with RendererInitializer with ScageController with App {
  override def run() {
    preinit()
    init()
    is_running = true
    prepareRender()
    scage_log.info(unit_name+": run")
    while(is_running && Scage.isAppRunning) {
      checkControls()
      executeActions()
      performRendering()
    }
    renderExitMessage()
    clear()
    dispose()
  }

  override def main(args:Array[String]) {
    scage_log.info("starting main screen "+unit_name+"...")
    initgl()
    super.main(args)
    run()
    destroygl()
    scage_log.info(unit_name+" was stopped")
    System.exit(0)
  }
}

class ScageScreen(unit_name:String = "Scage Screen") extends Screen(unit_name) with SingleController

class ScageScreenApp(unit_name:String = "Scage App",
                     window_width:Int  = property("screen.width", 800),
                     window_height:Int = property("screen.height", 600),
                     title:String = property("app.name", "Scage App")) extends ScreenApp with SingleController

/*class MultiControlledScreen(unit_name:String = "Scage App", is_main_unit:Boolean = false, properties:String = "")
extends Screen(unit_name, is_main_unit, properties) with MultiController*/
