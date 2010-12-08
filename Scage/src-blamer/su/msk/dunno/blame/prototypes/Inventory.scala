package su.msk.dunno.blame.prototypes

import collection.mutable.HashMap
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.messages.Message

class Inventory(val owner:Living) {
  private var items:HashMap[String, List[Item]] = new HashMap[String, List[Item]]()
  private var item_positions:List[String] = Nil
    
  private var item_selector = -1
  def itemSelector = item_selector
  def itemSelector_= (new_num:Int) = {
    if((new_num >= 1 && new_num <= item_positions.size) || new_num == -1)
      item_selector = new_num
  }
  
  def selectedItem:Item = {
    if(item_selector >= 1 && item_selector <= item_positions.size &&
       !items(item_positions(item_selector-1)).isEmpty) items(item_positions(item_selector-1)).head
    else null
  }

  def addItem(item:Item) = {
    if(items.keys.size < 10) {
      val name = item.stat("name")
      if(items.contains(name)) items(name) = item :: items(name)
      else items += (name -> List(item))
      if(!item_positions.contains(name)) item_positions = name :: item_positions
    }
  }

  def removeItem(item:Item) = {
    val name = item.stat("name")
    if(items.contains(name)) {
      items(name) = items(name).tail
      if(items(name).size == 0) item_positions = item_positions.filterNot(_ == name)
    }
  }

  def show = {
    Message.print(Message.xml("inventory.ownership", owner.stat("name")), 20, Renderer.height-20)
    if(item_selector == -1) {
      var h = Renderer.height-60
      var count = 1
      for(key <- items.keys) {
        Message.print(count + ". " + key + " (" + items(key).size+")", 20, h)
        count += 1
        h -= 20
      }
    }
    else if(item_selector >= 1 && item_selector <= item_positions.size &&
            !items(item_positions(item_selector-1)).isEmpty) {
      var selected_item = items(item_positions(item_selector-1)).head
      Message.print(selected_item.stat("name")+":\n"+selected_item.stat("description"), 20, Renderer.height-60)
    }
  }
}
