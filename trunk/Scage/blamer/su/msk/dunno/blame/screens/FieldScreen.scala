package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.blame.support.GenLib
import su.msk.dunno.blame.livings.Killy
import su.msk.dunno.blame.field.tiles.{Door, Wall, Floor}

object FieldScreen extends Screen("Field Screen") {
  override def properties = "blame-properties.txt"

  val game_from_x = property("game_from_x", 0)
  val game_to_x = property("game_to_x", 800)
  val game_from_y = property("game_from_y", 0)
  val game_to_y = property("game_to_y", 600)
  val N_x = property("N_x", 16)
  val N_y = property("N_y", 12)
  val fieldTracer = new FieldTracer(game_from_x, game_to_x, game_from_y, game_to_y, N_x, N_y, true)

  val maze = GenLib.createRDM(N_x, N_y, 5)
  (0 to N_x-1).foreachpair(0 to N_y-1)((i, j) => {
    maze(i)(j) match {
      case '#' => new Wall(i, j, fieldTracer)
      case '.' => new Floor(i, j, fieldTracer)
      case ',' => new Floor(i, j, fieldTracer)
      case '+' => new Door(i, j, fieldTracer)
      case _ =>
    }
  })

  val killy = new Killy(fieldTracer.getRandomPassablePoint, fieldTracer)
  keyListener(Keyboard.KEY_NUMPAD9, 300, onKeyDown = killy.move(Vec(1,1)))  
  keyListener(Keyboard.KEY_UP, 300, onKeyDown = killy.move(Vec(0,1)))
  keyListener(Keyboard.KEY_NUMPAD8, 300, onKeyDown = killy.move(Vec(0,1)))
  keyListener(Keyboard.KEY_NUMPAD7, 300, onKeyDown = killy.move(Vec(-1,1)))  
  keyListener(Keyboard.KEY_RIGHT, 300, onKeyDown = killy.move(Vec(1,0)))
  keyListener(Keyboard.KEY_NUMPAD6, 300, onKeyDown = killy.move(Vec(1,0)))
  keyListener(Keyboard.KEY_LEFT, 300, onKeyDown = killy.move(Vec(-1,0)))
  keyListener(Keyboard.KEY_NUMPAD4, 300, onKeyDown = killy.move(Vec(-1,0)))  
  keyListener(Keyboard.KEY_NUMPAD3, 300, onKeyDown = killy.move(Vec(1,-1)))  
  keyListener(Keyboard.KEY_DOWN, 300, onKeyDown = killy.move(Vec(0,-1)))
  keyListener(Keyboard.KEY_NUMPAD2, 300, onKeyDown = killy.move(Vec(0,-1)))  
  keyListener(Keyboard.KEY_NUMPAD1, 300, onKeyDown = killy.move(Vec(-1,-1)))
  
  keyListener(Keyboard.KEY_O, onKeyDown = killy.openDoor)    
  keyListener(Keyboard.KEY_C, onKeyDown = killy.closeDoor)
  
  
  center = fieldTracer.pointCenter(killy.point)
  
  Renderer.background(BLACK)

  addRender(new Renderable {
    override def render {
	    fieldTracer.drawField(killy.point)
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
}
