package net.scage.support.messages

import net.scage.support.ScageProperties._
import com.weiglewilczek.slf4s.Logger
import collection.mutable.HashMap
import xml.XML
import org.newdawn.slick.util.ResourceLoader

case class InterfaceData(interface_id:String, x:Int = -1, y:Int = -1, xinterval:Int = 0, yinterval:Int = 0, rows:Array[RowData])
case class RowData(message_id:String, x:Int = -1, y:Int = -1, placeholders_before:Int = 0, placeholders_in_row:Int = 0)
case class MessageData(message:String, x:Int = -1, y:Int = -1)

class ScageXML(val lang:String          = property("strings.lang", "en"),
               val messages_base:String = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
               val interfaces_file:String = property("interfaces.file", "resources/"+stringProperty("app.name").toLowerCase+"_interfaces.xml")
) {
  private val log = Logger(this.getClass.getName)
  
  lazy val messages_file = messages_base + "_" + lang + ".xml"
  private lazy val xml_messages = XML.load(ResourceLoader.getResourceAsStream(messages_file)) match {
    case <messages>{messages_list @ _*}</messages> => {
      HashMap((for {
        message @ <message>{_*}</message> <- messages_list
        message_id = (message \ "@id").text
        if message_id != ""
        message_text = (message.text).trim
      } yield (message_id, message_text)):_*)
    }
    case _ => HashMap[String, String]()
  }

  private def mergeMessage(xml_message:String, parameters:Any*) = {
    parameters.foldLeft(xml_message)((message, parameter) => message.replaceFirst("\\?", parameter.toString))
  }

  def xml(message_id:String, parameters:Any*):String = {
    if(message_id == "") ""
    else {
      xml_messages.get(message_id) match {
        case Some(message) => mergeMessage(message, parameters:_*)
        case None => {
          log.warn("failed to find string with code "+message_id)
          xml_messages += (message_id -> message_id)
          message_id
        }
      }
    }
  }

  def xmlOrDefault(message_id:String, parameters:Any*):String = {
    xml_messages.get(message_id) match {
      case Some(message) => mergeMessage(message, parameters.tail:_*)
      case None if parameters.size > 0 => {
        log.info("default value for string code "+message_id+" is "+{
          if("" == parameters.head) "empty string" else parameters.head
        })
        xml_messages += (message_id -> parameters.head.toString)
        mergeMessage(parameters.head.toString, parameters.tail:_*)
      }
      case _ => {
        log.warn("failed to find default message for the code "+message_id)
        xml_messages += (message_id -> message_id)
        message_id
      }
    }
  }
  
  private def placeholdersAmount(message_id:String) = {
    xml_messages.get(message_id) match {
      case Some(message) => message.foldLeft(0)((sum, char) => sum + (if(char == '?') 1 else 0))
      case None => 0
    }
  }

  private lazy val xml_interfaces = XML.load(ResourceLoader.getResourceAsStream(interfaces_file)) match {
    case <interfaces>{interfaces_list @ _*}</interfaces> => {
      HashMap((for {
        interface @ <interface>{rows_list @ _*}</interface> <- interfaces_list
        interface_id = (interface \ "@id").text
        interface_x = try{(interface \ "@x").text.toInt} catch {case ex:Exception => -1}
        interface_y = try{(interface \ "@y").text.toInt} catch {case ex:Exception => -1}
        interface_xinterval = try{(interface \ "@xinterval").text.toInt} catch {case ex:Exception => 0}
        interface_yinterval = try{(interface \ "@yinterval").text.toInt} catch {case ex:Exception => 0}
        if interface_id != ""
      } yield {
        var placeholders_before = 0
        val messages = (for {
          row @ <row>{_*}</row> <- rows_list
          message_id = (row \ "@message_id").text
          message_x = try{(row \ "@x").text.toInt} catch {case ex:Exception => -1}
          message_y = try{(row \ "@y").text.toInt} catch {case ex:Exception => -1}
          placeholders_in_row = placeholdersAmount(message_id)
        } yield {
          val to_yield = RowData(message_id, message_x, message_y, placeholders_before, placeholders_in_row)
          placeholders_before += placeholders_in_row
          to_yield
        }).toArray
        (interface_id, InterfaceData(interface_id, interface_x, interface_y, interface_xinterval, interface_yinterval, messages))
      }):_*)
    }
    case _ => HashMap[String, InterfaceData]()
  }
  
  def xmlInterface(interface_id:String, parameters:Any*):Array[MessageData] = {
    xml_interfaces.get(interface_id) match {
      case Some(InterfaceData(_, interface_x, interface_y, interface_xinterval, interface_yinterval, rows)) => {
        var xpos = interface_x
        var ypos = interface_y
        (for {
          RowData(message_id, message_x, message_y, params_from, params_take) <- rows
        } yield {
          val to_yield_x = if(message_x != -1) x else xpos  // priority to coords in tag row
          val to_yield_y = if(message_y != -1) y else ypos
          val to_yield = MessageData(xml(message_id, (parameters.drop(params_from).take(params_take)):_*), to_yield_x, to_yield_y)
          xpos += interface_xinterval
          ypos += interface_yinterval
          to_yield
        }).toArray
      }
      case None => {
        log.warn("failed to find interface with id "+interface_id)
        Array(MessageData(interface_id))
      }
    }  
  }

  def xmlInterfaceStrings(interface_id:String, parameters:Any*):Array[String] = {
    xml_interfaces.get(interface_id) match {
      case Some(InterfaceData(_, _, _, _, _, rows)) => {
        (for {
          RowData(message_id, _, _, params_from, params_take) <- rows
        } yield xml(message_id, (parameters.drop(params_from).take(params_take)):_*)).toArray
      }
      case None => {
        log.warn("failed to find interface with id "+interface_id)
        Array(interface_id)
      }
    }
  }
}

object ScageXML extends ScageXML(
  lang            = property("strings.lang", "en"),
  messages_base   = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
  interfaces_file = property("interfaces.file", "resources/"+stringProperty("app.name").toLowerCase+"_interfaces.xml")
)