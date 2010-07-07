package scagetest

import su.msk.dunno.scage.handlers.{Renderer, Idler}
import su.msk.dunno.scage.handlers.controller.Controller
import su.msk.dunno.scage.main.Engine
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{Color, Vec}
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.messages.Message

object PlaneFlight {
  def main(args: Array[String]): Unit = {
	 def delta = 10
	 val PLANE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
     var rotation = 0.0f
     var x:Float = 400;
	 var y:Float = 300;
     Controller.addKeyListener(Keyboard.KEY_A, 10, () => {rotation -= 0.2f*delta})
     Controller.addKeyListener(Keyboard.KEY_D, 10, () => {rotation += 0.2f*delta})
     Controller.addKeyListener(Keyboard.KEY_W, 10, () => {
    	 val hip = 0.4f*delta
    	 x -= hip*Math.sin(Math.toRadians(rotation)).toFloat
    	 y += hip*Math.cos(Math.toRadians(rotation)).toFloat    	 
     })
     Controller.addKeyListener(Keyboard.KEY_1, 10, () => {Renderer.scale -= (if(Renderer.scale <= 1.0f)0 else 0.1f)})
     Controller.addKeyListener(Keyboard.KEY_2, 10, () => {Renderer.scale += (if(Renderer.scale >= 5.0f)0 else 0.1f)})
     Renderer.addRender(() => {
    	 GL11.glPushMatrix();
    	 GL11.glTranslatef(x, y, 0.0f);
    	 GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
    	 Renderer.setColor(Color.WHITE)
    	 GL11.glCallList(PLANE)
    	 GL11.glPopMatrix()
     })
     Renderer.setCentral(() => if(Renderer.scale == 1)Vec(Renderer.width/2, Renderer.height/2) else Vec(x, y))
     
     val LAND = Renderer.createList("img/land.png", 800, 600, 0, 0, 800, 600)
     Renderer.addRender(() => {
    	GL11.glPushMatrix();
    	GL11.glTranslatef(Renderer.width/2, Renderer.height/2, 0.0f);
    	Renderer.setColor(Color.WHITE)
    	GL11.glCallList(LAND)
    	GL11.glPopMatrix()
     })
     
     Renderer.addInterfaceElement(() => Message.print("fps: "+Renderer.fps, 20, Renderer.height-20, Color.YELLOW))
     Idler
     Engine.start
  }
}