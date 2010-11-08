package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.blame.support.GenLib
import su.msk.dunno.blame.field.tiles.{Wall, Floor}
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.screens.prototypes.Renderable

object FieldScreen extends Screen("Field Screen") {
  override def properties = "blame-properties.txt"

  val game_from_x = property("game_from_x", 0)
  val game_to_x = property("game_to_x", 640)
  val game_from_y = property("game_from_y", 0)
  val game_to_y = property("game_to_y", 480)
  val N_x = property("N_x", 16)
  val N_y = property("N_y", 12)
  val fieldTracer = new FieldTracer(game_from_x, game_to_x, game_from_y, game_to_y, N_x, N_y, true)

  val maze = GenLib.CreateMaze(N_x, N_y)
  (0 to N_x-1).foreachpair(0 to N_y-1)((i, j) => {
    if(maze(i)(j) == '#') new Wall(i, j, fieldTracer)
    else if(maze(i)(j) == '.') new Floor(i, j, fieldTracer)
  })
  
  Renderer.background(BLACK)
  
  addRender(new Renderable {
   override def render {
	    (0 to N_x-1).foreachpair(0 to N_y-1)((i, j) => {
	      val symbol = fieldTracer.matrix(i)(j).last.getSymbol
	      val color = fieldTracer.matrix(i)(j).last.getColor
	      val coord = fieldTracer.matrix(i)(j).last.getCoord

	      Renderer.color(color)
	      Renderer.drawDisplayList(symbol, coord) 
	    })
    }
    
    override def interface {
      Message.print("FPS: "+fps, 20, height-20, WHITE)
    }
  })
  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)
  
  def main(args:Array[String]):Unit = run
}
