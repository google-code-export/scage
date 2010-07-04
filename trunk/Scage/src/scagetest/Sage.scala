package scagetest

import objects.{Box, Tr0yka}
import su.msk.dunno.scage.handlers.tracer.Tracer
import su.msk.dunno.scage.main.Engine
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.objects.{StaticLine, StaticBox, StaticPolygon, Platform}
import su.msk.dunno.scage.handlers.eventmanager.EventManager
import su.msk.dunno.scage.handlers.Renderer

object Sage {
  def main(args:Array[String]):Unit = {
    Engine.setDefaultHandlers
    Engine.addHandler(Tracer)

    // our level
    val game_width = Engine.getIntProperty("game_width")
    val game_height = Engine.getIntProperty("game_height")
    new StaticLine(Vec(0,0), Vec(game_width,0))
    new StaticLine(Vec(game_width,0), Vec(game_width,game_width))
    new StaticLine(Vec(game_width,game_width), Vec(0,game_width))
    new StaticLine(Vec(0,game_width), Vec(0,0))

    new StaticBox(Vec(0, 240), 60, 240)
    new StaticBox(Vec(400, 80), 200, 80)
    new StaticBox(Vec(0, 350), 800, 20)

    new Box(Vec(600, 40))
    new StaticBox(Vec(1000, 120), 60, 120)
    
    new StaticPolygon(Array(Vec(1500, 140), 
    						Vec(1560, 140), 
    						Vec(1560, 80), 
    						Vec(1620, 80), 
    						Vec(1620, 0), 
    						Vec(1500, 0)))
    new StaticBox(Vec(1000, 350), 620, 20)
    
    new StaticPolygon(Array(Vec(1800, 0), 
    						Vec(1850, 50), 
    						Vec(2000, 50), 
    						Vec(2000, 190), 
    						Vec(2100, 190), 
    						Vec(2100, 0)))
    new Platform(Vec(2120, 80), Vec(2120, 430))
    
    new StaticPolygon(Array(Vec(1800, 350), 
    						Vec(2000, 350), 
    						Vec(2000, 500), 
    						Vec(2040, 500), 
    						Vec(2100, 420), 
    						Vec(2100, 330), 
    						Vec(1800, 330)))

    // objects on level
    val tr0yka = new Tr0yka(Vec(20,270)) {
      addForce(Vec(-100,0))
    }

    //  game interface
    Renderer.setCentral(tr0yka.coord)
    Renderer.addInterfaceElement(() => Message.print("coord: "+tr0yka.coord, 20, Renderer.height-20))
    Renderer.addInterfaceElement(() => Message.print("fps: "+Engine.fps, 20, Renderer.height-35))
    Renderer.addInterfaceElement(() => Message.print("scale: "+Renderer.scale, 20, Renderer.height-50))

    // game pause
    EventManager.addKeyListener(Keyboard.KEY_P,() => Engine.switchPause)
    Renderer.addInterfaceElement(() => if(Engine.onPause)Message.print("PAUSE", Renderer.width/2-20, Renderer.height/2+60))

    // scaling
    val auto_scaling = Engine.getBooleanProperty("auto_scaling")
    if(auto_scaling) {
    	Renderer.setScaleFunc((scale) => {
    		if(EventManager.last_key  != Keyboard.KEY_ADD && EventManager.last_key != Keyboard.KEY_SUBTRACT) {
    			val factor = -3.0f/2000*tr0yka.velocity.norma2 + 2
    			if(factor > scale+0.1f && scale < 2)scale + 0.01f
    			else if(factor < scale-0.1f && scale > 0.5f)scale - 0.01f
    			else scale
    		}
    		else scale
    	})
    }
    EventManager.addKeyListener(Keyboard.KEY_ADD, 10, () => if(Renderer.scale < 2)Renderer.scale += 0.01f)
    EventManager.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => if(Renderer.scale > 0.5f)Renderer.scale -= 0.01f)

    Engine.start
  }
}