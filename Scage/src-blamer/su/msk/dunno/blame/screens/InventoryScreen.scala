package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import collection.mutable.HashMap
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.handlers.Renderer
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.prototypes.{Inventory, Living, Item}

object InventoryScreen extends ScageScreen("InventoryScreen") {  
  private var _inventory:Inventory = null
  def openInventory(inventory:Inventory) {
    _inventory = inventory
    run
  }
  
  private var item_number:Int = -1
  private def changeItemNumber(new_number_tmp:Int) = {
    if(new_number_tmp == -1) item_number = -1
    else {
      val new_number = new_number_tmp - 1
      if(item_number == -1 && new_number >= 0 && new_number < _inventory.items.size) {
        item_number = new_number
      }	    
    } 
  }

  addRender(new Renderable {
    override def interface = {
      Message.print(Message.xml("inventory.ownership", _inventory.owner.stat("name")), 20, Renderer.height-20)
      if(!_inventory.item(item_number).isEmpty) {
        val selected_item = _inventory.item(item_number)(0)
        Message.print(selected_item.stat("name")+":\n"+selected_item.stat("description"), 20, Renderer.height-60)    
      }
      else {
        var h = Renderer.height-60
        var count = 1
        for(key <- _inventory.items.keys) {
          Message.print(count + ". " + key + " (" + _inventory.item(key).size+")", 20, h)
          count += 1
          h -= 20
        }      
      }
    }
  })
  
  keyListener(Keyboard.KEY_1, onKeyDown = {changeItemNumber(1)})
  keyListener(Keyboard.KEY_2, onKeyDown = {changeItemNumber(2)})
  keyListener(Keyboard.KEY_3, onKeyDown = {changeItemNumber(3)})
  keyListener(Keyboard.KEY_4, onKeyDown = {changeItemNumber(4)})
  keyListener(Keyboard.KEY_5, onKeyDown = {changeItemNumber(5)})
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    if(selected_num > -1) selected_num = -1
    else stop
  })

  private def run = super.run
}
