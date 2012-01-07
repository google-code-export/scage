package net.scage.support.messages

import net.scage.support.ScageProperties._
import com.weiglewilczek.slf4s.Logger
import collection.mutable.HashMap
import xml.XML
import org.newdawn.slick.util.ResourceLoader
import net.scage.support.ScageColor
import net.scage.support.ScageColors._

case class InterfaceData(interface_id:String, x:Int = -1, y:Int = -1, xinterval:Int = 0, yinterval:Int = 0, rows:Array[RowData], color:ScageColor = DEFAULT_COLOR)
case class RowData(message_id:String, x:Int = -1, y:Int = -1, placeholders_before:Int = 0, placeholders_in_row:Int = 0, color:ScageColor = DEFAULT_COLOR)
case class MessageData(message:String, x:Int = -1, y:Int = -1, color:ScageColor = DEFAULT_COLOR)

class ScageXML(val lang:String          = property("strings.lang", "en"),
               val messages_base:String = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
               val interfaces_base:String = property("interfaces.base", "resources/interfaces/"+stringProperty("app.name").toLowerCase+"_interfaces")
) {
  private val log = Logger(this.getClass.getName)
  
  lazy val messages_file = messages_base + "_" + lang + ".xml"
  private lazy val xml_messages = try {
    XML.load(ResourceLoader.getResourceAsStream(messages_file)) match {
      case <messages>{messages_list @ _*}</messages> => {
        log.debug("parsing xml messages...")
        HashMap((for {
          message @ <message>{_*}</message> <- messages_list
          message_id = (message \ "@id").text
          if message_id != ""
          message_text = (message.text).trim
        } yield {
          log.debug("added message "+message_id)
          (message_id, message_text)
        }):_*)
      }
      case _ => HashMap[String, String]()  // TODO: log messages
    }
  } catch {
    case ex:Exception => HashMap[String, String]()  // TODO: log messages
  }

  private def mergeMessage(xml_message:String, parameters:Any*) = {
    parameters.zipWithIndex.foldLeft(xml_message)((message, param) => {
      message.replaceAll("\\$"+param._2, param._1.toString)
    })
  }

  def xml(message_id:String, parameters:Any*):String = {
    if(message_id == "") ""
    else {
      xml_messages.get(message_id) match {
        case Some(message) => mergeMessage(message, parameters:_*)
        case None => {
          log.warn("failed to find string with id "+message_id)
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
        log.info("default value for string id "+message_id+" is "+{
          if("" == parameters.head) "empty string" else parameters.head
        })
        xml_messages += (message_id -> parameters.head.toString)
        mergeMessage(parameters.head.toString, parameters.tail:_*)
      }
      case _ => {
        log.warn("failed to find default message for the id "+message_id)
        xml_messages += (message_id -> message_id)
        message_id
      }
    }
  }
  
  private def placeholdersAmount(message:String) = {
    message.foldLeft(0)((sum, char) => sum + (if(char == '$') 1 else 0))
  }

  lazy val interfaces_file = interfaces_base + "_" + lang + ".xml"
  private lazy val xml_interfaces = try {
    XML.load(ResourceLoader.getResourceAsStream(interfaces_file)) match {
      case <interfaces>{interfaces_list @ _*}</interfaces> => {
        log.debug("parsing xml interfaces...")
        HashMap((for {
          interface @ <interface>{rows_list @ _*}</interface> <- interfaces_list
          interface_id = (interface \ "@id").text
          if interface_id != ""
          interface_x = try{(interface \ "@x").text.toInt} catch {case ex:Exception => -1}
          interface_y = try{(interface \ "@y").text.toInt} catch {case ex:Exception => -1}
          interface_xinterval = try{(interface \ "@xinterval").text.toInt} catch {case ex:Exception => 0}
          interface_yinterval = try{(interface \ "@yinterval").text.toInt} catch {case ex:Exception => 0}
          interface_color_str = (interface \ "@color").text
        } yield {
          var placeholders_before = 0
          val messages = (for {
            row @ <row>{_*}</row> <- rows_list
            message_id = (row \ "@id").text
            message = row.text.trim
            message_x = try{(row \ "@x").text.toInt} catch {case ex:Exception => -1}
            message_y = try{(row \ "@y").text.toInt} catch {case ex:Exception => -1}
            placeholders_in_row = placeholdersAmount(message)
            message_color_str = (row \ "@color").text
          } yield {
            if(message != "") {
              xml_messages += (message_id -> message)
              log.debug("added message "+message_id)
            }
            val to_yield = RowData(message_id, message_x, message_y, placeholders_before, placeholders_in_row, colorFromString(message_color_str))
            placeholders_before += placeholders_in_row
            to_yield
          }).toArray
          log.debug("added interface "+interface_id)
          (interface_id, InterfaceData(interface_id, interface_x, interface_y, interface_xinterval, interface_yinterval, messages, colorFromString(interface_color_str)))
        }):_*)
      }
      case _ => HashMap[String, InterfaceData]()  // TODO: log messages
    }
  } catch {
    case ex:Exception => HashMap[String, InterfaceData]()  // TODO: log messages
  }
  
  def xmlInterface(interface_id:String, parameters:Any*):Array[MessageData] = {
    xml_interfaces.get(interface_id) match {
      case Some(InterfaceData(_, interface_x, interface_y, interface_xinterval, interface_yinterval, rows, interface_color)) => {
        var xpos = interface_x
        var ypos = interface_y
        var curcolor = interface_color
        (for {
          RowData(message_id, message_x, message_y, params_from, params_take, message_color) <- rows
        } yield {
          // TODO: we can resolve this while loading xml!
          val to_yield_x = if(message_x != -1) message_x else xpos  // priority to coords and color in tag row
          val to_yield_y = if(message_y != -1) message_y else ypos
          if(message_color != DEFAULT_COLOR) curcolor = message_color

          val to_yield = MessageData(xml(message_id, (parameters.drop(params_from).take(params_take)):_*), to_yield_x, to_yield_y, curcolor)
          if(message_x == -1) xpos += interface_xinterval
          if(message_y == -1) ypos += interface_yinterval
          to_yield
        }).toArray
      }
      case None => {
        log.warn("failed to find interface with id "+interface_id)
        xml_interfaces += (interface_id -> InterfaceData(interface_id, rows = Array(RowData(interface_id))))
        xml_messages += (interface_id -> interface_id)
        Array(MessageData(interface_id))
      }
    }  
  }

  def xmlInterfaceStrings(interface_id:String, parameters:Any*):Array[String] = {
    xml_interfaces.get(interface_id) match {
      case Some(InterfaceData(_, _, _, _, _, rows, _)) => {
        (for {
          RowData(message_id, _, _, params_from, params_take, _) <- rows
        } yield xml(message_id, (parameters.drop(params_from).take(params_take)):_*)).toArray
      }
      case None => {
        log.warn("failed to find interface with id "+interface_id)
        xml_interfaces += (interface_id -> InterfaceData(interface_id, rows = Array(RowData(interface_id))))
        xml_messages += (interface_id -> interface_id)
        Array(interface_id)
      }
    }
  }
}

object ScageXML extends ScageXML(
  lang            = property("strings.lang", "en"),
  messages_base   = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
  interfaces_base = property("interfaces.base", "resources/interfaces/"+stringProperty("app.name").toLowerCase+"_interfaces")
)