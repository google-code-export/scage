package scagetest

import scagetest.objects.Tr0yka
import su.msk.dunno.scage.main.Engine
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.handlers.eventmanager.EventManager
import su.msk.dunno.scage.objects.{StaticBox, DynaBall, StaticLine}
import su.msk.dunno.scage.support.{Vec}
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.{AI, Renderer}
import su.msk.dunno.scage.handlers.tracer.{Tracer, Trace, State}

object ScageTest {
  def main(args:Array[String]):Unit = {
    Engine.setDefaultHandlers
    Engine.addHandler(Tracer)

    // our level
    new StaticLine(Vec(0,0), Vec(Renderer.width,0))
    new StaticLine(Vec(Renderer.width,0), Vec(Renderer.width,Renderer.height))
    new StaticLine(Vec(Renderer.width,Renderer.height), Vec(0,Renderer.height))
    new StaticLine(Vec(0,Renderer.height), Vec(0,0))
    
    new StaticLine(Vec(0,240), Vec(250,140))
    new StaticLine(Vec(390,140), Vec(640,240))
    new StaticLine(Vec(0,65), Vec(640,65))    
    new StaticBox(Vec(320,250), 140, 10){
      var dir = 1
      AI.registerAI(() => {
    	val vec = new Vec(body.getPosition)+Vec(0,1)*dir
    	if(vec.y <= 200)dir = 1
    	else if(vec.y >= 400)dir = -1
    	body.setPosition(vec.x, vec.y)
      })
    }
    
    // objects on level
    new DynaBall(Vec(360,400), 15){
    	val trace = new Trace {
    		def getCoord = coord()
    		def getState = new State("name", "DynaBall")
    		def changeState(s:State) = {}
    	}
    	Tracer.addTrace(trace)
    	
    	private var wasPushed = false;
    	AI.registerAI(() => {
    		Tracer.getNeighbours(coord(), (-2 to 2)).foreach(trace => {
    			if(trace.getState.contains("push")) {
    				addForce((coord - trace.getCoord).n * 1000)
    				wasPushed = true
    			}
    			else wasPushed = false
    		})
    	})
    }.addForce(Vec(100,0))
    val tr0yka = new Tr0yka(Vec(300,400)){
      Renderer.addInterfaceElement(() => Message.print("touching: "+(if(isTouching)"true" else "false"), 20, 420))
      Renderer.addInterfaceElement(() => Message.print("speed: "+velocity.norma2, 20, 400))
      Renderer.addInterfaceElement(() => Message.print("neighbours: "+Tracer.getNeighbours(coord(), (-2 to 2)).size, 20, 380))
      addForce(Vec(-100,0))
      Renderer.setCentral(coord)
    }

    // game interface
    Renderer.addInterfaceElement(() => Message.print("fps: "+Engine.fps, 20, 460))
    Renderer.addInterfaceElement(() => Message.print("last key: "+EventManager.last_key, 20, 440))

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