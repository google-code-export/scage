package su.msk.dunno.blame.screens

import su.msk.dunno.screens.Screen
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer

object FieldScreen extends Screen("Field Screen") {
  override def properties = "blame-properties.txt"

  Renderer.setBackground(BLACK)

  val A = symbol('A')
  val q = symbol('q')
  val G = symbol('G')
  val question = symbol('?')

  interface {
    drawList(A, Vec(width/2, height/2+30), RED)
    drawList(q, Vec(width/2, height/2))
    drawList(G, Vec(width/2, height/2-30))
    Message.print("Press Esc to Exit", 20, height/2, WHITE)
  }
  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = allStop)
  
  def main(args:Array[String]):Unit = run
}