package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.GenLib
import su.msk.dunno.blame.livings.Killy
import su.msk.dunno.blame.field.tiles.{Door, Wall, Floor}
import su.msk.dunno.screens.prototypes.{Handler, Renderable}

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

  private var is_key_pressed = false
  private var pressed_start_time:Long = 0
  private def repeatTime = {
    if(is_key_pressed) {
      if(System.currentTimeMillis - pressed_start_time > 600) 100
      else 300
    }
    else 300
  }
  def press = if(!is_key_pressed) {
    is_key_pressed = true
    pressed_start_time = System.currentTimeMillis
  }

  val killy = new Killy(fieldTracer.getRandomPassablePoint, fieldTracer)
  keyListener(Keyboard.KEY_NUMPAD9, repeatTime, onKeyDown = {killy.move(Vec(1,1)); press},   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_UP,      repeatTime, onKeyDown = {killy.move(Vec(0,1)); press},   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD8, repeatTime, onKeyDown = {killy.move(Vec(0,1)); press},   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD7, repeatTime, onKeyDown = {killy.move(Vec(-1,1)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_RIGHT,   repeatTime, onKeyDown = {killy.move(Vec(1,0)); press},   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD6, repeatTime, onKeyDown = {killy.move(Vec(1,0)); press},   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_LEFT,    repeatTime, onKeyDown = {killy.move(Vec(-1,0)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD4, repeatTime, onKeyDown = {killy.move(Vec(-1,0)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD3, repeatTime, onKeyDown = {killy.move(Vec(1,-1)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_DOWN,    repeatTime, onKeyDown = {killy.move(Vec(0,-1)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD2, repeatTime, onKeyDown = {killy.move(Vec(0,-1)); press},  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD1, repeatTime, onKeyDown = {killy.move(Vec(-1,-1)); press}, onKeyUp = is_key_pressed = false)
  
  keyListener(Keyboard.KEY_O, onKeyDown = killy.openDoor)    
  keyListener(Keyboard.KEY_C, onKeyDown = killy.closeDoor)
  
  windowCenter = Vec(fieldTracer.game_from_x + fieldTracer.game_width/2, game_from_y + fieldTracer.game_height/2)
  center = fieldTracer.pointCenter(killy.point)
  
  Renderer.background(BLACK)

  addRender(new Renderable {
    override def render = fieldTracer.draw(killy.point)

    override def interface {
      Message.print("Message Message Message Message Message ", 10, 80, WHITE)
      Message.print("Message Message Message Message Message ", 10, 60, WHITE)
      Message.print("Message Message Message Message Message ", 10, 40, WHITE)
      Message.print("Message Message Message Message Message ", 10, 20, WHITE)
      Message.print("Message Message Message Message Message ", 10, 0, WHITE)

      Message.print("FPS: "+fps, 600, height-25, WHITE)
    }
  })

  /*addHandler(new Handler {
    override def action = println(is_pressed)
  })*/
  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)
  
  def main(args:Array[String]):Unit = run
}
