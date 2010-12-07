package su.msk.dunno.blame.prototypes

import collection.mutable.HashMap
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.messages.Message

class Inventory(val owner:Living) {
  private var _items:HashMap[String, List[Item]] = new HashMap[String, List[Item]]()
  private var item_positions:List[String] = Nil
    
  private def keyByNumber(num:Int) = item_positions(num)  
  
  def items = _items
  def item(key:String) = items(key)
  def item(number:Int) = items(keyByNumber(number))

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
}
