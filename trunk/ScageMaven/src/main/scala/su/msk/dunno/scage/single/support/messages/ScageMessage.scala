package su.msk.dunno.scage.single.support.messages

import su.msk.dunno.scage.single.support.{ScageColors, ScageColor}
import su.msk.dunno.scage.single.support.ScageProperties._
import collection.mutable.HashMap
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.FileInputStream
import javax.xml.parsers.SAXParserFactory
import org.apache.log4j.Logger
import unicode.UnicodeFont

object ScageMessage {
  private val log = Logger.getLogger(this.getClass)
  
  val lang = property("strings.lang", "en")
  val messages_base = property("strings.base", "resources/strings/"+stringProperty("app.name").toLowerCase+"_strings")
  val messages_file = messages_base + "_" + lang + ".xml"

  private val xmlmh = new XMLMessageHandler
  private val parser = SAXParserFactory.newInstance.newSAXParser
  try {
    val fis = new FileInputStream(messages_file)
    parser.parse(fis, xmlmh)
    log.info("successfully parsed strings file "+messages_file)
  }
  catch {
    case e:Exception => {
      log.error("failed to parse file "+messages_file+":\n"+e.getLocalizedMessage)
    }
  }

  val fonts_base = property("fonts.base", "resources/fonts/")
  val font_file = property("font.file", "DroidSans.ttf")
  val font_size = property("font.size", 18)
  val row_height = property("font.row.height", font_size+2)
  val glyph_from = property("glyph.from", 1024)
  val glyph_to = property("glyph.to", 1279)
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
        log.info("default value for string code "+message_code+" is "+parameters.head)
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

  def print(message:Any, x:Float, y:Float, color:ScageColor = ScageColors.WHITE) = {
    font.drawString(x,y,message.toString, new org.newdawn.slick.Color(color.red, color.green, color.blue))
  }

  private[ScageMessage] class XMLMessageHandler extends DefaultHandler {
    var xml_messages = new HashMap[String, String]

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