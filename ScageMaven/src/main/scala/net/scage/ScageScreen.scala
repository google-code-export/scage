package net.scage

import handlers.controller2.{ScageController, MultiController, SingleController}
import handlers.Renderer

trait Screen extends Scage with Renderer with ScageController {
  actionNoPause {
    checkControls()
  }

  actionNoPause {
    render()
  }

  if(is_main_unit) {            // There is a problem here if no main screen set. We suppose that client app is starting
    dispose {                   // from the point of some main screen (is_main_unit=true and its a first screen
      if(!Scage.isAppRunning) { // to start and the last to exit) but for now we have no ways to control it
        exitRender()
      }
    }
  }
}

class ScageScreen(val unit_name:String = "Scage App", val is_main_unit:Boolean = false, val properties:String = "")
extends Screen with SingleController

class MultiControlledScreen(val unit_name:String = "Scage App", val is_main_unit:Boolean = false, val properties:String = "")
extends Screen with MultiController
