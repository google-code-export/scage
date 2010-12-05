package su.msk.dunno.blame.support

import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.scage.support.messages.Message

object IngameMessages {
  private var bottom_messages:List[String] = Nil
  private val message_capacity = 5
  
  def addBottomMessage(message:String, is_same_string:Boolean = false) = {
    if(is_same_string) bottom_messages = bottom_messages.head+" "+message :: bottom_messages.tail
    else bottom_messages = message :: bottom_messages
    if(bottom_messages.length > message_capacity) 
      bottom_messages = bottom_messages.init
  }
  
  def addBottomPropMessage(message_code:String, parameters:String*) = {
    addBottomMessage(Message.xml(message_code, parameters:_*), false)
  }
  def addBottomPropMessageSameString(message_code:String, parameters:String*) = {
    addBottomMessage(Message.xml(message_code, parameters:_*), true)
  }
  
  def showBottomMessages = {
    var h = 80
    bottom_messages.foreach(message => {Message.print(message, 10, h, WHITE); h -= 20})
  }
}
