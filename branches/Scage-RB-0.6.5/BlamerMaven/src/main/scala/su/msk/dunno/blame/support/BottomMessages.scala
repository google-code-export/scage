package su.msk.dunno.blame.support

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.ScageProperties._

object BottomMessages {
  private var bottom_messages:List[String] = Nil
  val bottom_messages_capacity = property("bottommessages.capacity", 5)
  val bottom_messages_height   = (row_height+1)*bottom_messages_capacity
  
  def addMessage(message:String, is_on_same_string:Boolean = false) = {
    if(is_on_same_string) bottom_messages = bottom_messages.head+" "+message :: bottom_messages.tail
    else bottom_messages = message :: bottom_messages
    if(bottom_messages.length > bottom_messages_capacity)
      bottom_messages = bottom_messages.init
  }
  
  def addPropMessage(message_code:String, parameters:String*) = {
    addMessage(xml(message_code, parameters:_*), is_on_same_string = false)
  }
  def addPropMessageSameString(message_code:String, parameters:String*) = {
    addMessage(xml(message_code, parameters:_*), is_on_same_string = true)
  }
  
  def showBottomMessages(skip_lines:Int) = {
    var h = bottom_messages_height - (skip_lines + 1)*row_height
    if(skip_lines == 0 || skip_lines+bottom_messages.size <= bottom_messages_capacity)
      bottom_messages.foreach(message => {print(message, 10, h, WHITE); h -= row_height})
    else bottom_messages.dropRight(skip_lines).foreach(message => {print(message, 10, h, WHITE); h -= row_height})
  }
}
