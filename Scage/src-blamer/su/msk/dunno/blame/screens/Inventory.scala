package su.msk.dunno.blame.screens

import su.msk.dunno.screens.ScageScreen
import collection.mutable.HashMap
import su.msk.dunno.screens.prototypes.Renderable
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.handlers.Renderer
import org.lwjgl.input.Keyboard
import su.msk.dunno.blame.prototypes.{Living, Item}

class Inventory(living:Living) extends ScageScreen("Inventory") {
  private var items:HashMap[String, List[Item]] = new HashMap[String, List[Item]]()
  private var item_positions:List[String] = Nil

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
  
  private var selected_num = -1
  
  addRender(new Renderable {
    override def interface = {
      Message.print(Message.xml("inventory.ownership", living.stat("name")), 20, Renderer.height-20)
      if(selected_num == -1 || selected_num >= item_positions.size) {
        var h = Renderer.height-60
        var count = 1
        for(key <- items.keys) {
          Message.print(count + ". " + key + " (" + items(key).size+")", 20, h)
          count += 1          
          h -= 20
        }
      }
      else if(selected_num < item_positions.size) {
        val selected_item = items(keyByNumber(selected_num))(0)
        Message.print(selected_item.stat("name")+":\n"+selected_item.stat("description"), 20, Renderer.height-60)
      }
    }
  })
  
  private def keyByNumber(num:Int) = {
    item_positions(num)
  }
  
  keyListener(Keyboard.KEY_1, onKeyDown = {selected_num = 0})
  keyListener(Keyboard.KEY_2, onKeyDown = {selected_num = 1})
  keyListener(Keyboard.KEY_3, onKeyDown = {selected_num = 2})
  keyListener(Keyboard.KEY_4, onKeyDown = {selected_num = 3})
  keyListener(Keyboard.KEY_5, onKeyDown = {selected_num = 4})
  keyListener(Keyboard.KEY_ESCAPE, onKeyDown = {
    if(selected_num > -1) selected_num = -1
    else stop
  })
}
