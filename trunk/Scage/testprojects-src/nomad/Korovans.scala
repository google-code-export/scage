package nomad

import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.{AI, Idler, Renderer}
import su.msk.dunno.scage.handlers.tracer.{State, Trace, StandardTracer}
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

object Korovans extends ScageLibrary {
  // common images
  val KOROVAN = Renderer.createList("img/korovan.png", 57, 30, 0, 0, 114, 61)
  val ROBBED_KOROVAN = Renderer.createList("img/robbed_korovan.png", 57, 30, 0, 0, 114, 61)
  val NOMAD_ANIMATION = Renderer.createAnimation("img/nomad_animation.png", 24, 37, 24, 37, 2)

  var num_robbed = 0
  var num_passed = 0
  def game_speed = 1.0f + num_robbed*0.2f

  def main(args:Array[String]):Unit = {
    // desert
    val DESERT = Renderer.createList("img/desert.png", 800, 600, 0, 0, 400, 400)
    Renderer.addRender(() => {
      GL11.glPushMatrix();
      Renderer.setColor(WHITE)

      GL11.glTranslatef(width/2, height/2, 0.0f);
      GL11.glCallList(DESERT)

      GL11.glPopMatrix()
    })

    // korovans
    var korovan_period = 0
    AI.registerAI(() => {
      if(num_passed < 10) {
        if(korovan_period <= 0) {
          val x = if(Math.random > 0.5) width-10 else 10
          val y = (Math.random*height).toInt
          new Korovan(Vec(x, y))
          korovan_period = Math.max(50, 200 - game_speed.toInt*20)
        }
        else korovan_period -= 1
      }
    })

    // player
    val nomad = new Nomad(Vec(width/2, height/2))

    // game pause
    Controller.addKeyListener(Keyboard.KEY_P,() => Scage.switchPause)
    Renderer.addInterfaceElement(() => if(on_pause)Message.print("PAUSE", width/2-20, height/2+60))

    // game interface
    Renderer.addInterfaceElement(() => Message.print("fps: "+fps, Renderer.width-80, Renderer.height-20))
    Renderer.addInterfaceElement(() => Message.print("Korovans robbed: "+num_robbed, 1, Renderer.height-20))
    Renderer.addInterfaceElement(() => Message.print("Korovans passed: "+num_passed, 1, Renderer.height-35))
    Renderer.addInterfaceElement(() => if(num_passed >= 10)Message.print("Game Over. HighScore: "+num_robbed, Renderer.width/2-100, Renderer.height/2+75))

    Idler
    Scage.start
  }

  class Korovan(init_coord:Vec) {
    private var coord = init_coord
    private var was_robbed = false
    private var was_passed = false

    // ai
    private val direction = if(width - coord.x > coord.x) 1 else -1
    AI.registerAI(() => {
      if(!was_passed){
        val new_coord = coord + Vec(direction, 0)*Korovans.game_speed
        if(new_coord.x >= width || new_coord.x < 0) {
          was_passed = true
          Korovans.num_passed += 1
        }
        if(!was_robbed) coord = StandardTracer.checkEdges(new_coord)
      }
    })

    // interactions
    StandardTracer.addTrace(new Trace[State]{
      def getCoord = coord
      def getState() = new State("name", "korovan").put("was_robbed", was_robbed)
      def changeState(s:State) = if(s.contains("nomad")) was_robbed = true
    })

    // render
    private var show_robbed = 60
    Renderer.addRender(() => {
       if(!was_robbed && !was_passed) {
          GL11.glPushMatrix();
          GL11.glTranslatef(coord.x, coord.y, 0.0f);
          GL11.glScalef(direction, 1, 1)
          Renderer.setColor(WHITE)
          GL11.glCallList(KOROVAN)
          GL11.glPopMatrix()
       }
       else if(was_robbed && show_robbed > 0) {
          Message.print("Robbed!", coord.x-40, coord.y+20, RED)
          GL11.glPushMatrix();
          GL11.glTranslatef(coord.x, coord.y, 0.0f);
          GL11.glScalef(direction, 1, 1)
          Renderer.setColor(WHITE)
          GL11.glCallList(ROBBED_KOROVAN)
          GL11.glPopMatrix()
          if(!on_pause)show_robbed -= 1
       }
    })
  }

  class Nomad(init_coord:Vec) {
    private var coord = init_coord

    // controls
    private var is_moving = false
    private var dir = 1
    Controller.addKeyListener(Keyboard.KEY_UP, 10, () => {
      coord = StandardTracer.checkEdges(coord + Vec(0, 1.5f)*Korovans.game_speed)
      is_moving = true
    },
    () => is_moving = false)
    Controller.addKeyListener(Keyboard.KEY_DOWN, 10, () => {
      coord = StandardTracer.checkEdges(coord + Vec(0, -1.5f)*Korovans.game_speed)
      is_moving = true
    },
    () => is_moving = false)
    Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => {
      coord = StandardTracer.checkEdges(coord + Vec(1.5f, 0)*Korovans.game_speed)
      dir = 1
      is_moving = true
    },
    () => is_moving = false)
    Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => {
      coord = StandardTracer.checkEdges(coord + Vec(-1.5f, 0)*Korovans.game_speed)
      dir = -1
      is_moving = true
    },
    () => is_moving = false)

    // interactions
    AI.registerAI(() => {
      StandardTracer.getNeighbours(coord, -1 to 1).foreach(korovan => {
        if(coord.dist2(korovan.getCoord) < 57*24 && !korovan.getState.getBool("was_robbed")) {
          korovan.changeState(new State("nomad"))
          Korovans.num_robbed += 1
        }
      })
    })

    // render
    private var next_frame:Float = 0
    Renderer.addRender(() => {
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
       GL11.glScalef(dir, 1, 1)
      Renderer.setColor(WHITE)
      GL11.glCallList(NOMAD_ANIMATION(next_frame.toInt));
      if(!Scage.on_pause && is_moving) next_frame += 0.1f
      if(next_frame >= 2)next_frame = 0
      GL11.glPopMatrix()
    })
  }
}