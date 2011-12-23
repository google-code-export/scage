package net.scage.support.messages

import net.scage.support.ScageProperties._
import com.weiglewilczek.slf4s.Logger
import collection.mutable.HashMap
import xml.XML
import org.newdawn.slick.util.ResourceLoader

class ScageXML(val lang:String          = property("strings.lang", "en"),
               val messages_base:String = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings")
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
    val xml_message = if(xml_messages.contains(message_code)) xml_messages(message_code)
    else {
      log.warn("failed to find string with code "+message_code)
      //val warning_string = xmlOrDefault("error.nomessage","No message provided under the code ?", message_code)
      xml_messages += (message_code -> /*warning_string*/message_code)
      /*warning_string*/message_code
    }
    mergeMessage(xml_message, parameters:_*)
  }

  def xmlOrDefault(message_code:String, parameters:Any*):String = {
    if(xml_messages.contains(message_code)) mergeMessage(xml_messages(message_code), parameters.tail:_*)
    else {
      if(parameters.size > 0) {
        log.info("default value for string code "+message_code+" is "+{
          if("" == parameters.head) "empty string" else parameters.head
        })
        xml_messages += (message_code -> parameters.head.toString)
        mergeMessage(parameters.head.toString, parameters.tail:_*)
      } else {
        //xmlOrDefault("error.nomessage","No message provided under the code ?", message_code)
        log.warn("failed to find default message for the code "+message_code)
        xml_messages += (message_code -> message_code)
        message_code
      }
    }
  }
}

object ScageXML extends ScageXML(
  lang          = property("strings.lang", "en"),
  messages_base = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings")
)