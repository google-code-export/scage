package su.msk.dunno.blame.screens

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.field.tiles.{Door, Wall, Floor}
import su.msk.dunno.scage.screens.prototypes.ScageRender
import su.msk.dunno.scage.screens.handlers.Renderer
import su.msk.dunno.blame.support.{BottomMessages, TimeUpdater, GenLib}
import su.msk.dunno.blame.livings.{SiliconCreature, Cibo, Killy}
import su.msk.dunno.blame.decisions._

object Blamer extends ScageScreen(
  screen_name = "Blamer",
  is_main_screen = true,
  properties = "blame-properties.txt") {
  val right_messages_width = property("rightmessages.width", 200)
  
  // map
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
  
  // players
  private var is_play_cibo = false
  val killy = FieldTracer.randomPassablePoint() match {
    case Some(point) => new Killy(point)
    case None => {
      log.error("failed to place killy to the field, the programm will exit")
      System.exit(1)
      null
    }
  }
  val cibo = FieldTracer.randomPassablePoint(killy.getPoint - Vec(2,2), killy.getPoint + Vec(2,2)) match {
    case Some(point) => new Cibo(point)
    case None => {
      log.error("failed to place cibo to the field, the programm will exit")
      System.exit(1)
      null
    }
  }
  def currentPlayer = if(is_play_cibo) cibo else killy
  
  // enemies
  (1 to 50).foreach(i => {
    FieldTracer.randomPassablePoint() match {
      case Some(point) => new SiliconCreature(point)
      case None =>
    }
  })

  // controls on main screen
  private var is_key_pressed = false
  private var pressed_start_time:Long = 0
  private def repeatTime = {
    if(is_key_pressed) {
      if(System.currentTimeMillis - pressed_start_time > 600) 100
      else 300
    }
    else 300
  }
  private def move(point:Vec) = {
    if(!is_key_pressed) {
      is_key_pressed = true
      pressed_start_time = System.currentTimeMillis
    }
    TimeUpdater.addDecision(new Move(currentPlayer, point))
  }
  
  keyListener(Keyboard.KEY_NUMPAD9, repeatTime, onKeyDown = move(Vec(1,1)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_UP,      repeatTime, onKeyDown = move(Vec(0,1)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD8, repeatTime, onKeyDown = move(Vec(0,1)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD7, repeatTime, onKeyDown = move(Vec(-1,1)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_RIGHT,   repeatTime, onKeyDown = move(Vec(1,0)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD6, repeatTime, onKeyDown = move(Vec(1,0)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD5, repeatTime, onKeyDown = move(Vec(0,0)),   onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_LEFT,    repeatTime, onKeyDown = move(Vec(-1,0)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD4, repeatTime, onKeyDown = move(Vec(-1,0)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD3, repeatTime, onKeyDown = move(Vec(1,-1)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_DOWN,    repeatTime, onKeyDown = move(Vec(0,-1)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD2, repeatTime, onKeyDown = move(Vec(0,-1)),  onKeyUp = is_key_pressed = false)
  keyListener(Keyboard.KEY_NUMPAD1, repeatTime, onKeyDown = move(Vec(-1,-1)), onKeyUp = is_key_pressed = false)
  
  keyListener(Keyboard.KEY_O,     onKeyDown = TimeUpdater.addDecision(new OpenDoor(currentPlayer)))
  keyListener(Keyboard.KEY_C,     onKeyDown = TimeUpdater.addDecision(new CloseDoor(currentPlayer)))
  keyListener(Keyboard.KEY_F,     onKeyDown = TimeUpdater.addDecision(new Shoot(currentPlayer)))
  keyListener(Keyboard.KEY_I,     onKeyDown = TimeUpdater.addDecision(new OpenInventory(currentPlayer)))
  keyListener(Keyboard.KEY_W,     onKeyDown = TimeUpdater.addDecision(new OpenWeapon(currentPlayer)))
  keyListener(Keyboard.KEY_D,     onKeyDown = TimeUpdater.addDecision(new DropItem(currentPlayer)))
  keyListener(Keyboard.KEY_COMMA, onKeyDown = TimeUpdater.addDecision(new PickUpItem(currentPlayer)))
  
  keyListener(Keyboard.KEY_TAB,    onKeyDown = is_play_cibo = !is_play_cibo)
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)

  private lazy val help_screen = new ScageScreen("Help Screen") {
    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)

    addRender(new ScageRender {
      override def interface = {
        print(xml("helpscreen.tutorial.keys"),           10,  Renderer.height-20)
        print(xml("helpscreen.tutorial.description"),    300, Renderer.height-65)
        print(xml("helpscreen.helpmessage"), 10, row_height, GREEN)
      }
    })
  }
  keyListener(Keyboard.KEY_F1, onKeyDown = help_screen.run)
  keyListener(Keyboard.KEY_T, onKeyDown = TimeUpdater.addDecision(new IssueCommand(currentPlayer)))

  // render on main screen
  windowCenter = Vec((width - right_messages_width)/2, 
  		     BottomMessages.bottom_messages_height + (height - BottomMessages.bottom_messages_height)/2)
  center = FieldTracer.pointCenter(currentPlayer.getPoint)
  
  Renderer.backgroundColor = BLACK

  def drawInterface = {
    def intStat(key:String) = currentPlayer.intStat(key).toString

    //messages on the right side of the screen
    print(currentPlayer.stat("name"),             width - right_messages_width, height-25, WHITE)
    print("FPS: "+Renderer.fps,                   width - right_messages_width, height-45, WHITE)
    print("time: "+TimeUpdater.time,              width - right_messages_width, height-65, WHITE)
    print(xml("mainscreen.stats.health", intStat("health"), intStat("max_health")),
      width - right_messages_width, height-85, WHITE)
    print("Follow: "+currentPlayer.boolStat("follow"), width - right_messages_width, height-105, WHITE)
    print("Attack: "+currentPlayer.boolStat("attack"), width - right_messages_width, height-125, WHITE)
    print("Last Action: "+currentPlayer.lastActionTime, width - right_messages_width, height-145, WHITE)
    print(xml("mainscreen.stats.energy", intStat("energy"), intStat("max_energy"), intStat("energy_increase_rate")),
      width - right_messages_width, height-165, WHITE)
    print(xml("mainscreen.stats.shield", intStat("shield"), intStat("max_shield"), intStat("shield_increase_rate")),
      width - right_messages_width, height-185, WHITE)
  } 

  addRender(new ScageRender {
    override def render = FieldTracer.drawField(currentPlayer.getPoint)

    override def interface = {
      BottomMessages.showBottomMessages(0)
      drawInterface
    }
  })
  
  // initial message
  BottomMessages.addPropMessage("mainscreen.openhelp")
  BottomMessages.addPropMessage("greetings.helloworld", currentPlayer.stat("name"))
  
  def main(args:Array[String]):Unit = run
}
