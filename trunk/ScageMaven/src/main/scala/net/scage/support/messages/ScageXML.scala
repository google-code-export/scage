package net.scage.support.messages

import net.scage.support.ScageProperties._
import com.weiglewilczek.slf4s.Logger
import collection.mutable.HashMap
import xml.XML
import org.newdawn.slick.util.ResourceLoader

case class InterfaceData(interface_id:String, x:Int = -1, y:Int = -1, xinterval:Int = 0, yinterval:Int = 0, rows:Array[RowData])
case class RowData(message_id:String, x:Int = -1, y:Int = -1, placeholders_in_row:Int, overall_placeholder_position:Int)
case class MessageData(message:String, x:Int = -1, y:Int = -1)

class ScageXML(val lang:String          = property("strings.lang", "en"),
               val messages_base:String = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
               val interfaces_file:String = property("interfaces.file", "resources/"+stringProperty("app.name").toLowerCase+"_interfaces.xml")
) {
  private val log = Logger(this.getClass.getName)
  
  val messages_file = messages_base + "_" + lang + ".xml"
  private val xml_messages = XML.load(ResourceLoader.getResourceAsStream(messages_file)) match {
    case <messages>{messages_list @ _*}</messages> => {
      HashMap((for {
        message @ <message>{_*}</message> <- messages_list
        message_code = (message \ "@code").text
        if message_code != ""
        message_text = (message.text).trim
      } yield (message_code, message_text)):_*)
    }
    case _ => HashMap[String, String]()
  }

  private def mergeMessage(xml_message:String, parameters:Any*) = {
    parameters.foldLeft(xml_message)((message, parameter) => message.replaceFirst("\\?", parameter.toString))
  }

  def xml(message_code:String, parameters:Any*):String = {
    if(message_code == "") ""
    else {
      xml_messages.get(message_code) match {
        case Some(message) => mergeMessage(message, parameters:_*)
        case None => {
          log.warn("failed to find string with code "+message_code)
          xml_messages += (message_code -> message_code)
          message_code
        }
      }
    }
  }

  def xmlOrDefault(message_code:String, parameters:Any*):String = {
    xml_messages.get(message_code) match {
      case Some(message) => mergeMessage(message, parameters.tail:_*)
      case None if parameters.size > 0 => {
        log.info("default value for string code "+message_code+" is "+{
          if("" == parameters.head) "empty string" else parameters.head
        })
        xml_messages += (message_code -> parameters.head.toString)
        mergeMessage(parameters.head.toString, parameters.tail:_*)
      }
      case _ => {
        log.warn("failed to find default message for the code "+message_code)
        xml_messages += (message_code -> message_code)
        message_code
      }
    }
  }
  
  private def placeholdersAmount(message_id:String) = {
    xml_messages.get(message_id) match {
      case Some(message) => message.foldLeft(0)((sum, char) => sum + (if(char == '?') 1 else 0))
      case None => 0
    }
  }

  private val xml_interfaces = XML.load(ResourceLoader.getResourceAsStream(interfaces_file)) match {
    case <interfaces>{interfaces_list @ _*}</interfaces> => {
      HashMap((for {
        interface @ <interface>{rows_list @ _*}</interface> <- interfaces_list
        interface_id = (interface \ "@id").text
        x = try{(interface \ "@x").text.toInt} catch {case ex:Exception => -1}
        y = try{(interface \ "@y").text.toInt} catch {case ex:Exception => -1}
        xinterval = try{(interface \ "@xinterval").text.toInt} catch {case ex:Exception => 0}
        yinterval = try{(interface \ "@yinterval").text.toInt} catch {case ex:Exception => 0}
        if interface_id != ""
      } yield {
        var overall_placeholder_position = 0
        val messages = (for {
          row @ <row>{_*}</row> <- rows_list
          message_id = (row \ "@message_id").text
          x = try{(row \ "@x").text.toInt} catch {case ex:Exception => -1}
          y = try{(row \ "@y").text.toInt} catch {case ex:Exception => -1}
          placeholders_in_row = placeholdersAmount(message_id)
        } yield {
          val to_yield = RowData(message_id, x, y, placeholders_in_row, overall_placeholder_position)
          overall_placeholder_position += placeholders_in_row
          to_yield
        }).toArray
        (interface_id, InterfaceData(interface_id, x, y, xinterval, yinterval, messages))
      }):_*)
    }
    case _ => HashMap[String, InterfaceData]()
  }
  
  def xmlInterface(interface_id:String, parameters:Any*):Array[MessageData] = {
    xml_interfaces.get(interface_id) match {
      case Some(interface) => {
        var xpos = interface.x
        var ypos = interface.y
        (for {
          RowData(message_id, x, y, params_from, params_take) <- interface.rows
        } yield {
          val to_yield = MessageData(xml(message_id, (parameters.drop(params_from).take(params_take)):_*), if(x != -1) x else xpos, if(y != -1) y else ypos)
          xpos += interface.xinterval
          ypos += interface.yinterval
          to_yield
        }).toArray
      }
      case None => {
        log.warn("failed to find interface with id "+interface_id)
        Array(MessageData(interface_id))
      }
    }  
  }
}

object ScageXML extends ScageXML(
  lang            = property("strings.lang", "en"),
  messages_base   = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
  interfaces_file = property("interfaces.file", "resources/"+stringProperty("app.name").toLowerCase+"_interfaces.xml")
)