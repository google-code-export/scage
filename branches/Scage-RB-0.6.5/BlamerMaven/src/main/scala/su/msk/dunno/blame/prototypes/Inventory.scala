package su.msk.dunno.blame.prototypes

import collection.mutable.HashMap
import su.msk.dunno.scage.screens.handlers.Renderer
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.ScageColors._
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.screens.prototypes.ScageRender
import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.blame.support.BottomMessages
import su.msk.dunno.scage.screens.support.tracer.State
import su.msk.dunno.blame.field.{FieldTracer, FieldObject}

class Inventory(val owner:Living) {
  private var items:HashMap[String, List[FieldObject]] = new HashMap[String, List[FieldObject]]()
  private var item_positions:List[(String)] = Nil
    
  private var item_selector:Int = -1
  def itemSelector:Int = item_selector
  def itemSelector_= (new_num:Int):Unit = {
    if((new_num >= 1 && new_num <= item_positions.size) || new_num == -1) {
      item_selector = new_num
      if(is_item_selection) inventory_screen.stop
    }
  }
  
  def selectedItem:Option[FieldObject] = {
    if(item_selector >= 1 && item_selector <= item_positions.size &&
       !items(item_positions(item_selector-1)).isEmpty) Some(items(item_positions(item_selector-1)).head)
    else None
  }
  
  private var is_item_selection = false
  private var purpose = ""
  def selectItem(_purpose:String, condition: FieldObject => Boolean = fo => true):Option[FieldObject] = {
    item_selector = -1
    is_item_selection = true
    purpose = _purpose
    generateItemPositions(condition)
    inventory_screen.run
    selectedItem
  }
  
  def showInventory = {
    item_selector = -1
    is_item_selection = false
    generateItemPositions(fo => true)
    inventory_screen.run
  }

  def addItem(item:FieldObject) = {
    if(items.keys.size < 10) {
      val name = item.getState.getString("name")
      if(items.contains(name)) items(name) = item :: items(name)
      else items += (name -> List(item))
    }
    else {
      item.changeState(new State("point", owner.getPoint))
      FieldTracer.addTraceSecondToLast(item)
      BottomMessages.addPropMessage("decision.drop", owner.stat("name"), item.getState.getString("name"))
    }
  }

  def removeItem(item:FieldObject) = {
    val name = item.getState.getString("name")
    if(items.contains(name)) {
      items(name) = items(name).tail
    }
  }

  private def generateItemPositions(condition: FieldObject => Boolean) = {
    item_positions = items.keys.filter(items(_).size > 0).foldLeft(List[String]())((positions, key) => {
      val item = items(key).head
      if(condition(item)) key :: positions
      else positions
    })
  }

  private lazy val inventory_screen = new ScageScreen("Inventory Screen") {
    private implicit def toObjectWithForeachI[A](l:List[A]) = new ScalaObject {
      def foreachi(func:(A, Int) => Unit) = {
        var index = 0
        l.foreach(value => {
          func(value, index)
          index += 1
        })
      }
    }

    addRender(new ScageRender {
      override def interface = {
        print(xml("inventory.ownership", owner.stat("name")), 10, Renderer.height-20)
        if(item_selector == -1) {
          item_positions.foreachi((key, i) => {
            print((i+1) + ". " + key + " (" + items(key).size+")",
              10, Renderer.height-row_height*4-i*row_height, items(key).head.getColor)
          })
          if(is_item_selection) {
            print(purpose, 10, Renderer.height-20-row_height)
            print(xml("inventory.selection.helpmessage"), 10, row_height, GREEN)
          }
          else print(xml("inventory.show.helpmessage"), 10, row_height, GREEN)
        }
        else if(item_selector >= 1 && item_selector <= item_positions.size &&
                !items(item_positions(item_selector-1)).isEmpty) {
          val selected_item = items(item_positions(item_selector-1)).head
          print(selected_item.getState.getString("name")+":\n"+
                selected_item.getState.getString("description"), 10, Renderer.height-60, selected_item.getColor)
          print(xml("inventory.description.helpmessage"), 10, row_height, GREEN)
        }
      }
    })

    keyListener(Keyboard.KEY_1, onKeyDown = itemSelector = 1)
    keyListener(Keyboard.KEY_2, onKeyDown = itemSelector = 2)
    keyListener(Keyboard.KEY_3, onKeyDown = itemSelector = 3)
    keyListener(Keyboard.KEY_4, onKeyDown = itemSelector = 4)
    keyListener(Keyboard.KEY_5, onKeyDown = itemSelector = 5)
    keyListener(Keyboard.KEY_6, onKeyDown = itemSelector = 6)
    keyListener(Keyboard.KEY_7, onKeyDown = itemSelector = 7)
    keyListener(Keyboard.KEY_8, onKeyDown = itemSelector = 8)
    keyListener(Keyboard.KEY_9, onKeyDown = itemSelector = 9)
    keyListener(Keyboard.KEY_0, onKeyDown = itemSelector = 10)

    keyListener(Keyboard.KEY_NUMPAD1, onKeyDown = itemSelector = 1)
    keyListener(Keyboard.KEY_NUMPAD2, onKeyDown = itemSelector = 2)
    keyListener(Keyboard.KEY_NUMPAD3, onKeyDown = itemSelector = 3)
    keyListener(Keyboard.KEY_NUMPAD4, onKeyDown = itemSelector = 4)
    keyListener(Keyboard.KEY_NUMPAD5, onKeyDown = itemSelector = 5)
    keyListener(Keyboard.KEY_NUMPAD6, onKeyDown = itemSelector = 6)
    keyListener(Keyboard.KEY_NUMPAD7, onKeyDown = itemSelector = 7)
    keyListener(Keyboard.KEY_NUMPAD8, onKeyDown = itemSelector = 8)
    keyListener(Keyboard.KEY_NUMPAD9, onKeyDown = itemSelector = 9)
    keyListener(Keyboard.KEY_NUMPAD0, onKeyDown = itemSelector = 10)

    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
      if(itemSelector != -1) itemSelector = -1
      else stop
    })
  }
}
