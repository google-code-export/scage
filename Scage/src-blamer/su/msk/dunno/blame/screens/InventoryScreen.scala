package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import org.lwjgl.input.Keyboard

class InventoryScreen(val inventory:su.msk.dunno.blame.prototypes.Inventory) extends ScageScreen("InventoryScreen") {  
  def selectedItem = inventory.selectedItem

  addRender(new Renderable {
    override def interface = inventory.show
  })
  
  keyListener(Keyboard.KEY_1, onKeyDown = inventory.itemSelector = 1)
  keyListener(Keyboard.KEY_2, onKeyDown = inventory.itemSelector = 2)
  keyListener(Keyboard.KEY_3, onKeyDown = inventory.itemSelector = 3)
  keyListener(Keyboard.KEY_4, onKeyDown = inventory.itemSelector = 4)
  keyListener(Keyboard.KEY_5, onKeyDown = inventory.itemSelector = 5)
  keyListener(Keyboard.KEY_6, onKeyDown = inventory.itemSelector = 6)
  keyListener(Keyboard.KEY_7, onKeyDown = inventory.itemSelector = 7)    
  keyListener(Keyboard.KEY_8, onKeyDown = inventory.itemSelector = 8)  
  keyListener(Keyboard.KEY_9, onKeyDown = inventory.itemSelector = 9)  
  keyListener(Keyboard.KEY_0, onKeyDown = inventory.itemSelector = 10)  
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    if(inventory.itemSelector != -1) inventory.itemSelector = -1
    else stop
  })

  run
}
