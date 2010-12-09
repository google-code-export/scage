package su.msk.dunno.blame.support

import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.scage.support.messages.ScageMessage
import su.msk.dunno.scage.support.ScageProperties

object BottomMessages {
  private var bottom_messages:List[String] = Nil
  val bottom_messages_capacity = ScageProperties.property("bottommessages.capacity", 5)
  val bottom_messages_height = 
    ScageProperties.property("bottommessages.height", (ScageMessage.font_size + 5)*bottom_messages_capacity)
  
  def addBottomMessage(message:String, is_on_same_string:Boolean = false) = {
    if(is_on_same_string) bottom_messages = bottom_messages.head+" "+message :: bottom_messages.tail
    else bottom_messages = message :: bottom_messages
    if(bottom_messages.length > bottom_messages_capacity)
      bottom_messages = bottom_messages.init
  }
  
  def addBottomPropMessage(message_code:String, parameters:String*) = {
    addBottomMessage(ScageMessage.xml(message_code, parameters:_*), false)
  }
  def addBottomPropMessageSameString(message_code:String, parameters:String*) = {
    addBottomMessage(ScageMessage.xml(message_code, parameters:_*), true)
  }
  
  def showBottomMessages = {
    var h = bottom_messages_height - (ScageMessage.font_size + 5)
    bottom_messages.foreach(message => {ScageMessage.print(message, 10, h, WHITE); h -= 20})
  }
}
