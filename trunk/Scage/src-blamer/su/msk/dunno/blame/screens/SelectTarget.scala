package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.blame.field.FieldTracer
import su.msk.dunno.blame.support.IngameMessages
import su.msk.dunno.screens.support.ScageLibrary._
import su.msk.dunno.blame.support.MyFont._
import su.msk.dunno.blame.prototypes.Living

class SelectTarget(stop_key:Int, living:Living) extends ScageScreen("Target Selector") {
  private var select_line = List(living.point)
  
  private var target_point:Vec = living.point
  def targetPoint = target_point
  
  def clearSelectLine = {
    select_line.foreach(FieldTracer.allowDraw(_))
    select_line = Nil
  }
  def buildSelectLine(delta:Vec) = {
    target_point += delta
    clearSelectLine
    select_line = FieldTracer.line(living.point, target_point)
    select_line.foreach(FieldTracer.preventDraw(_))
    target_point = select_line.head
  }
  def drawSelectLine = {
    if(!select_line.isEmpty) {
      select_line.tail.foreach(point => {
        Renderer.drawDisplayList(MINOR_SELECTOR, FieldTracer.pointCenter(point), WHITE)
      })
      Renderer.drawDisplayList(MAIN_SELECTOR, FieldTracer.pointCenter(select_line.head), WHITE)
    }
  }  
  
  keyListener(Keyboard.KEY_NUMPAD9, 100, onKeyDown = buildSelectLine(Vec(1,1)))
  keyListener(Keyboard.KEY_NUMPAD8, 100, onKeyDown = buildSelectLine(Vec(0,1)))
  keyListener(Keyboard.KEY_NUMPAD7, 100, onKeyDown = buildSelectLine(Vec(-1,1)))
  keyListener(Keyboard.KEY_NUMPAD6, 100, onKeyDown = buildSelectLine(Vec(1,0)))
  keyListener(Keyboard.KEY_NUMPAD4, 100, onKeyDown = buildSelectLine(Vec(-1,0)))
  keyListener(Keyboard.KEY_NUMPAD3, 100, onKeyDown = buildSelectLine(Vec(1,-1)))
  keyListener(Keyboard.KEY_NUMPAD2, 100, onKeyDown = buildSelectLine(Vec(0,-1)))
  keyListener(Keyboard.KEY_NUMPAD1, 100, onKeyDown = buildSelectLine(Vec(-1,-1)))

  keyListener(Keyboard.KEY_UP,    100, onKeyDown = buildSelectLine(Vec(0,1)))
  keyListener(Keyboard.KEY_RIGHT, 100, onKeyDown = buildSelectLine(Vec(1,0)))
  keyListener(Keyboard.KEY_LEFT,  100, onKeyDown = buildSelectLine(Vec(-1,0)))
  keyListener(Keyboard.KEY_DOWN,  100, onKeyDown = buildSelectLine(Vec(0,-1)))

  keyListener(stop_key, onKeyDown = {
    clearSelectLine
    stop
  })
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    target_point = living.point
    clearSelectLine
    stop
  })

  // render on main screen
  windowCenter = Vec((width - 200)/2, 100 + (height - 100)/2)
  center = FieldTracer.pointCenter(Blamer.currentPlayer.point)
  
  Renderer.backgroundColor(BLACK)  
  
  addRender(new Renderable {
    override def render = {
      FieldTracer.draw(Blamer.currentPlayer.point)
      drawSelectLine
    }
    
    override def interface {
      IngameMessages.showBottomMessages
      Blamer.drawInterface
    }
  })
  
  run
}
