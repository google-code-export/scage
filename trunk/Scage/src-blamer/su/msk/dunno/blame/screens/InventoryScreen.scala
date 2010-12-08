package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.prototypes.Renderable
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.prototypes.Inventory

object InventoryScreen extends ScageScreen("InventoryScreen") {  
  private var _inventory:Inventory = null
  def show(inventory:Inventory) {
    _inventory = inventory
    super.run
  }

  def pickItem(inventory:Inventory) {
    _inventory = inventory
    super.run
    _inventory.selectedItem
  }

  addRender(new Renderable {
    override def interface = _inventory.show      
  })
  
  keyListener(Keyboard.KEY_1, onKeyDown = _inventory.itemSelector = 1)
  keyListener(Keyboard.KEY_2, onKeyDown = _inventory.itemSelector = 2)
  keyListener(Keyboard.KEY_3, onKeyDown = _inventory.itemSelector = 3)
  keyListener(Keyboard.KEY_4, onKeyDown = _inventory.itemSelector = 4)
  keyListener(Keyboard.KEY_5, onKeyDown = _inventory.itemSelector = 5)
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    if(_inventory.itemSelector != -1) _inventory.itemSelector = -1
    else stop
  })

  override def run = {}
}
