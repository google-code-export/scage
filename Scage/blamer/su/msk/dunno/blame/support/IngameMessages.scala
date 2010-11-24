package su.msk.dunno.blame.support

import org.xml.sax.Attributes
import javax.xml.parsers.SAXParserFactory
import java.io.FileInputStream
import collection.mutable.HashMap
import org.xml.sax.helpers.DefaultHandler
import su.msk.dunno.scage.support.ScageProperties._
import su.msk.dunno.scage.support.Colors._
import su.msk.dunno.scage.support.messages.Message

object IngameMessages {
  val lang = property("lang", "en")
  val messages_file = "messages_"+lang+".xml"
  
  private val imh = new IngameMessageHandler
  private val parser = SAXParserFactory.newInstance.newSAXParser
  private val fis = new FileInputStream(messages_file)
  parser.parse(fis, imh)
  
  private var bottom_messages:List[String] = Nil
  private val message_capacity = 5
  
  def addBottomMessage(message:String) = {
    bottom_messages = message :: bottom_messages
    if(bottom_messages.length > message_capacity) 
      bottom_messages = bottom_messages.tail
  }
  
  def addBottomPropMessage(message_code:String, parameters:String*) = {
    var message = imh.XMLMessages(message_code)
    parameters.foreach(parameter => message = message.replaceFirst("\\?", parameter))
    addBottomMessage(message)
  }
  
  def showBottomMessages = {
    var h = 80
    bottom_messages.foreach(message => {Message.print(message, 10, h, WHITE); h -= 20})
  }

  private[IngameMessages] class IngameMessageHandler extends DefaultHandler {
    private var xml_messages = new HashMap[String,String]
    def XMLMessages = xml_messages
    
    private var current_message_key = ""
    private var current_message_text = new StringBuilder
  
    override def startElement(uri:String, local_name:String, raw_name:String, amap:Attributes) = {
      if("message".equalsIgnoreCase(raw_name)) current_message_key = amap.getValue("code")
    }
    
    override def characters(ch:Array[Char], start:Int, length:Int) = {
      val value = new String(ch, start, length)
      current_message_text.append(value)
    }
    
    override def endElement(uri:String, local_name:String, raw_name:String) = {
      if("message".equalsIgnoreCase(raw_name)) {
        xml_messages += (current_message_key -> current_message_text.toString.trim)
        current_message_text.clear
      }
    }
  }
}
