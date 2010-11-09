package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.blame.tiles.tiles.{Wall, Floor}
import su.msk.dunno.blame.tiles.FieldTracer
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.blame.support.{MyFont, GenLib}

object FieldScreen extends Screen("Field Screen") {
  override def properties = "blame-properties.txt"

  val game_from_x = property("game_from_x", 0)
  val game_to_x = property("game_to_x", 800)
  val game_from_y = property("game_from_y", 0)
  val game_to_y = property("game_to_y", 600)
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
        if(fieldTracer.matrix(i)(j).length > 0) {
          val symbol = fieldTracer.matrix(i)(j).last.getSymbol
          val color = fieldTracer.matrix(i)(j).last.getColor
          val coord = fieldTracer.matrix(i)(j).last.getCoord

          Renderer.drawDisplayList(symbol, coord, color)
        }
	    })
    }

    override def interface {
      Message.print("Message Message Message Message Message ", 10, 80, WHITE)
      Message.print("Message Message Message Message Message ", 10, 60, WHITE)
      Message.print("Message Message Message Message Message ", 10, 40, WHITE)
      Message.print("Message Message Message Message Message ", 10, 20, WHITE)
      Message.print("Message Message Message Message Message ", 10, 0, WHITE)

      Message.print("FPS: "+fps, 600, height-25, WHITE)
    }
  })
  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)
  
  def main(args:Array[String]):Unit = run

  /*override def properties = "blame-properties.txt"

  Renderer.background(BLACK)

  val A = symbol('#')
  val q = symbol('.')
  val G = symbol('G')
  val question = symbol('?')

  addRender(new Renderable {
    override def render {
      Renderer.drawDisplayList(A, Vec(width/2, height/2+30), RED)
      Renderer.drawDisplayList(q, Vec(width/2, height/2))
      Renderer.drawDisplayList(G, Vec(width/2, height/2-30))
    }

    override def interface {
      Message.print("Press Esc to Exit", 20, height/2, WHITE)      
    }
  })

  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)

  def main(args:Array[String]):Unit = run*/
}
