package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.{IngameMessages, TimeUpdater}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.support.ScageLibrary._
import su.msk.dunno.blame.support.MyFont._

class SelectTarget(stop_key:Int) extends ScageScreen("Target Selector") {
  IngameMessages.addBottomPropMessage("selecttarget.helpmessage", FieldScreen.currentPlayer.stat("name"))
  
  private var select_line = List(FieldScreen.currentPlayer.point)
  
  private var target_point:Vec = FieldScreen.currentPlayer.point
  def targetPoint = target_point
  
  def clearSelectLine = {
    select_line.foreach(FieldTracer.objectsAtPoint(_).foreach(_.allowDraw))
    select_line = Nil
  }
  def buildSelectLine = {
    clearSelectLine
    select_line = FieldTracer.line(FieldScreen.currentPlayer.point, target_point)
    select_line.foreach(FieldTracer.objectsAtPoint(_).foreach(_.preventDraw))
  }
  def drawSelectLine = {
    if(!select_line.isEmpty) {
      select_line.tail.foreach(point => {
        Renderer.drawDisplayList(MINOR_SELECTOR, FieldTracer.pointCenter(point), WHITE)
      })
      Renderer.drawDisplayList(MAIN_SELECTOR, FieldTracer.pointCenter(select_line.head), WHITE)
    }
  }
  
  
  keyListener(Keyboard.KEY_UP, 500, onKeyDown = {
    target_point += Vec(0,1)
    buildSelectLine
  })
  keyListener(Keyboard.KEY_DOWN, 500, onKeyDown = {
    target_point += Vec(0,-1)
    buildSelectLine
  })
  keyListener(Keyboard.KEY_RIGHT, 500, onKeyDown = {
    target_point += Vec(1,0)
    buildSelectLine
  })
  keyListener(Keyboard.KEY_LEFT, 500, onKeyDown = {
    target_point += Vec(-1,0)
    buildSelectLine
  })    
  keyListener(stop_key, 500, onKeyDown = {
    clearSelectLine
    stop
  })  

  // render on main screen
  windowCenter = Vec((width - 200)/2, 100 + (height - 100)/2)
  center = FieldTracer.pointCenter(FieldScreen.currentPlayer.point)
  
  Renderer.backgroundColor(BLACK)  
  
  addRender(new Renderable {
    override def render = {
      FieldTracer.draw(FieldScreen.currentPlayer.point)
      drawSelectLine
    }
    
    override def interface {
      IngameMessages.showBottomMessages
      FieldScreen.drawInterface
    }
  })
  
  run
}
