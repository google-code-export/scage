package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.livings.Killy
import su.msk.dunno.blame.field.tiles.{Door, Wall, Floor}
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.blame.prototypes.Decision
import su.msk.dunno.blame.decisions.{CloseDoor, OpenDoor, Move}
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.blame.support.{IngameMessages, TimeUpdater, GenLib}

object FieldScreen extends Screen("Field Screen") {
  override def properties = "blame-properties.txt"

  private val maze = GenLib.CreateStandardDunegon(FieldTracer.N_x, FieldTracer.N_y)
  (0 to FieldTracer.N_x-1).foreachpair(0 to FieldTracer.N_y-1)((i, j) => {
    maze(i)(j) match {
      case '#' => new Wall(i, j)
      case '.' => new Floor(i, j)
      case ',' => new Floor(i, j)
      case '+' => new Door(i, j)
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
  private def press(d:Decision) = {
    if(!is_key_pressed) {
      is_key_pressed = true
      pressed_start_time = System.currentTimeMillis
    }
    TimeUpdater.addDecision(d)
  }

  val killy = new Killy(FieldTracer.getRandomPassablePoint)
  
  keyListener(Keyboard.KEY_NUMPAD9, repeatTime, 
    onKeyDown = press(new Move(Vec(1,1), killy)), onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_UP,      repeatTime, 
    onKeyDown = press(new Move(Vec(0,1), killy)), onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD8, repeatTime, 
    onKeyDown = press(new Move(Vec(0,1), killy)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD7, repeatTime, 
    onKeyDown = press(new Move(Vec(-1,1), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_RIGHT,   repeatTime, 
    onKeyDown = press(new Move(Vec(1,0), killy)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD6, repeatTime, 
    onKeyDown = press(new Move(Vec(1,0), killy)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_LEFT,    repeatTime, 
    onKeyDown = press(new Move(Vec(-1,0), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD4, repeatTime, 
    onKeyDown = press(new Move(Vec(-1,0), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD3, repeatTime, 
    onKeyDown = press(new Move(Vec(1,-1), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_DOWN,    repeatTime, 
    onKeyDown = press(new Move(Vec(0,-1), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD2, repeatTime, 
    onKeyDown = press(new Move(Vec(0,-1), killy)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD1, repeatTime, 
    onKeyDown = press(new Move(Vec(-1,-1), killy)), onKeyUp = is_key_pressed = false)
  
  keyListener(Keyboard.KEY_O, onKeyDown = TimeUpdater.addDecision(new OpenDoor(killy)))
  keyListener(Keyboard.KEY_C, onKeyDown = TimeUpdater.addDecision(new CloseDoor(killy)))
  
  windowCenter = Vec((width - 200)/2, 100 + (height - 100)/2)
  center = FieldTracer.pointCenter(killy.point)
  
  Renderer.backgroundColor(BLACK)
  
  IngameMessages.addBottomPropMessage("greetings.helloworld", killy.name)

  addRender(new Renderable {
    override def render = FieldTracer.draw(killy.point)

    override def interface {
      IngameMessages.showBottomMessages

      Message.print("FPS: "+fps, 600, height-25, WHITE)
      Message.print("time: "+TimeUpdater.time, width - 200, height-45, WHITE)
      //Message.print("decisions: "+TimeUpdater.decisions.length, width - 200, height-65, WHITE)
    }
  })
  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)
  
  def main(args:Array[String]):Unit = run
}
