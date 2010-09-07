package mob

import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.handlers.Renderer
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.{Vec, ScageLibrary}
import org.lwjgl.opengl.GL11

object Game extends ScageLibrary {
  // game logic
  var player_health = 100
  var mob_health = 100
  var is_end = false
  
  def startSequence() = {
    println("Welcome to \"Kill the mob\" v0.1!")
    println()
    println("While you where wandering in deep dark tunnels, a mob appeared in your way.")
    println("It appeared to be very agressive, so a fight started.");
    println();
  }

  def gameSequence(player_move:Int, mob_move:Int):(Int, Int) = {
    player_move match {
      case 1 => println("You chose to attack, while mob chose to ")
      case 2 => println("You chose to defend, while mob chose to ")
      case 3 => println("You chose to regenerate, while mob chose to ")
    }
    (player_move, mob_move) match {
      case (1, 1) => {
        println("attack too!")
        println("You both lose 20 HP.")
        (-20, -20)
      }
      case (1, 2) => {
        println("defend.")
        println("You did not manage to hurt him.")
        (0, 0)
      }
      case (1, 3) => {
        println("regenerate.")
        println("He lost 30 HP.")
        (0, -30)
      }
      case (2, 1) => {
        println("attack.")
        println("You did not let him hurt you.")
        (0, 0)
      }
      case (2, 2) => {
        println("defend too!")
        println("You standed like two retards.")
        (0, 0)
      }
      case (2, 3) => {
        println("regenerate.")
        println("He gained 20 HP.")
        (0, 20)
      }
      case (3, 1) => {
        println("attack.")
        println("You was completely protectless. you you've lost 30 HP.")
        (-30 ,0)
      }
      case (3, 2) => {
        println("defend.")
        println("He standed like a retard, while you gained 20 HP.")
        (20, 0)
      }
      case (3, 3) => {
        println("regenerate too!")
        println("Your energies helped each other, so you both gained 30 HP.")
        (30, 30)
      }
    }
  }

  def checkVictory(player_health:Int, mob_health:Int) = {
    val player_death = player_health <= 0
    val mob_death = mob_health <= 0
    (player_death, mob_death) match {
      case (true, true) => println("Its a draw!")
      case (true, false) => println("You lose.")
      case (false, true) => println("You won!")
      case (false, false) => {
        println("Your HP: " + player_health);
        println("Mob\'s HP: " + mob_health);
        println()
        println("Your opponent is ready to act. You need to do something. What are you going to do?");
        println("Press \"1\" to attack, \"2\" to defend yourself and \"3\" to regenerate: ");
      }
    }
    is_end = player_death || mob_death
  }

  def mob_move():Int = (Math.random*3).toInt + 1
  def nextMove(player_move:Int) = {
    val next_move = gameSequence(player_move, mob_move)
    player_health += next_move._1; mob_health += next_move._2
    checkVictory(player_health, mob_health)
  }

  // controls
  Controller.addKeyListener(Keyboard.KEY_1, () => if(!is_end) nextMove(1))
  Controller.addKeyListener(Keyboard.KEY_2, () => if(!is_end) nextMove(2))
  Controller.addKeyListener(Keyboard.KEY_3, () => if(!is_end) nextMove(3))

  // backgound
  Renderer.setBackground(BLACK)
  val MOB = Renderer.createList("img/bibop2.png", 468, 462, 0, 0, 468, 462)
  Renderer.addRender(() => {
    GL11.glPushMatrix();
    GL11.glLoadIdentity
    Renderer.setColor(WHITE)

    GL11.glTranslatef(width/2, height/2, 0.0f);
    GL11.glCallList(MOB)

    GL11.glPopMatrix()
  })

  // render system
  var message_buffer = new StringBuilder()
  var num_rows = 0
  def println(str:String = "") = {
    message_buffer.append(str+"\n")
    num_rows += 1
    if(num_rows > 22) Renderer.setCentral(() => Vec(width/2, height/2 - 27*(num_rows - 22)))
  }
  Renderer.addRender(() => Message.print(message_buffer.toString, 10, height-30, WHITE))

  def main(args:Array[String]):Unit = {
    startSequence
    checkVictory(player_health, mob_health)
    Scage.start
  }
}