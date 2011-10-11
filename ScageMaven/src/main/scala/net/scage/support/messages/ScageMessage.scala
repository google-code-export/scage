package net.scage.support.messages

import _root_.net.scage.support.{ScageColor}
import _root_.net.scage.handlers.Renderer._
import _root_.net.scage.support.ScageProperties._
import collection.mutable.HashMap
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import javax.xml.parsers.SAXParserFactory
import org.apache.log4j.Logger
import org.newdawn.slick.util.ResourceLoader
import net.scage.support.messages.unicode.UnicodeFont

object ScageMessage extends ScageMessage (
  lang          = property("strings.lang", "en"),
  messages_base = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
  fonts_base    = property("fonts.base", "resources/fonts/"),
  font_file     = property("font.file", "DroidSans.ttf"),
  font_size     = property("font.size", 18),
  glyph_from    = property("glyph.from", 1024),
  glyph_to      = property("glyph.to", 1279)
)

class ScageMessage(
  val lang:String          = property("strings.lang", "en"),
  val messages_base:String = property("strings.base", "resources/strings/" +stringProperty("app.name").toLowerCase+"_strings"),
  val fonts_base:String    = property("fonts.base", "resources/fonts/"),
  val font_file:String     = property("font.file", "DroidSans.ttf"),
  val font_size:Int        = property("font.size", 18),
  val glyph_from:Int       = property("glyph.from", 1024),
  val glyph_to:Int         = property("glyph.to", 1279)
) {
  private val log = Logger.getLogger(this.getClass)

  val messages_file = messages_base + "_" + lang + ".xml"
  private val xmlmh = new XMLMessageHandler
  private val parser = SAXParserFactory.newInstance.newSAXParser
  try {
    val fis = ResourceLoader.getResourceAsStream(messages_file)   // can be loaded as resource from jar
    parser.parse(fis, xmlmh)
    log.info("successfully parsed strings file "+messages_file)
  }
  catch {
    case e:Exception => {
      log.error("failed to parse file "+messages_file+":\n"+e.getLocalizedMessage)
    }
  }

  private val font = try {
    new UnicodeFont(fonts_base+font_file, font_size, glyph_from, glyph_to)
  }
  catch {
    case e:Exception => {
      log.error("failed to create font:\n"+e.getLocalizedMessage)
      log.error("please provide the path to some unicode ttf font")
      System.exit(1)
      null
    }
  }

  private def mergeMessage(xml_message:String, parameters:String*) = {
    parameters.foldLeft(xml_message)((message, parameter) => message.replaceFirst("\\?", parameter))
  }

  def xml(message_code:String, parameters:String*):String = {
    val xml_message = if(xmlmh.xml_messages.contains(message_code)) xmlmh.xml_messages(message_code)
    else {
      log.warn("failed to find string with code "+message_code)
      //val warning_string = xmlOrDefault("error.nomessage","No message provided under the code ?", message_code)
      xmlmh.xml_messages += (message_code -> /*warning_string*/message_code)
      /*warning_string*/message_code
    }
    mergeMessage(xml_message, parameters:_*)
  }

  def xmlOrDefault(message_code:String, parameters:String*):String = {
    if(xmlmh.xml_messages.contains(message_code))
      mergeMessage(xmlmh.xml_messages(message_code), parameters.tail:_*)
    else {
      if(parameters.size > 0) {
        log.info("default value for string code "+message_code+" is "+{
          if("" == parameters.head) "empty string" else parameters.head
        })
        xmlmh.xml_messages += (message_code -> parameters.head)
        mergeMessage(parameters.head, parameters.tail:_*)
      }
      else {
        //xmlOrDefault("error.nomessage","No message provided under the code ?", message_code)
        log.warn("failed to find default message for the code "+message_code)
        xmlmh.xml_messages += (message_code -> message_code)
        message_code
      }
    }
  }

  def print(message:Any, x:Float, y:Float, color:ScageColor = color) {
    font.drawString(x,y,message.toString, new org.newdawn.slick.Color(color.red, color.green, color.blue))
  }

  private[ScageMessage] class XMLMessageHandler extends DefaultHandler {
    var xml_messages = new HashMap[String, String]

    private var current_message_key = ""
    private var current_message_text = new StringBuilder

    override def startElement(uri:String, local_name:String, raw_name:String, amap:Attributes) {
      if("message".equalsIgnoreCase(raw_name)) current_message_key = amap.getValue("code")
    }

    override def characters(ch:Array[Char], start:Int, length:Int) {
      val value = new String(ch, start, length)
      current_message_text.append(value)
    }

    override def endElement(uri:String, local_name:String, raw_name:String) {
      if("message".equalsIgnoreCase(raw_name)) {
        xml_messages += (current_message_key -> current_message_text.toString().trim)
        current_message_text.clear()
      }
    }
  }
}