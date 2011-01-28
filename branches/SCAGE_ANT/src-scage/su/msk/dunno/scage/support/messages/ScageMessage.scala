package su.msk.dunno.scage.support.messages

import su.msk.dunno.scage.support.{ScageProperties, ScageColors, ScageColor}
import collection.mutable.HashMap
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.FileInputStream
import javax.xml.parsers.SAXParserFactory
import org.apache.log4j.Logger
import unicode.UnicodeFont

object ScageMessage {
  private val log = Logger.getLogger(this.getClass)
  
  val lang = ScageProperties.property("strings.lang", "en")
  val messages_base = ScageProperties.property("strings.base", "/resources/strings/strings")
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

  val font_path = ScageProperties.property("font.file", "resources/fonts/DroidSans.ttf")
  val font_size = ScageProperties.property("font.size", 18)
  val row_height = ScageProperties.property("font.row.height", font_size+2)
  val glyph_from = ScageProperties.property("glyph.from", 1024)
  val glyph_to = ScageProperties.property("glyph.to", 1279)
  private val font = try {
    new UnicodeFont(font_path, font_size, glyph_from, glyph_to)
  }
  catch {
    case e:Exception => {
      log.error("failed to create font:\n"+e.getLocalizedMessage)
      log.error("please provide the path to some unicode ttf font")
      System.exit(1)
      null
    }
  }

  def xml(message_code:String, parameters:String*):String = {
    val xml_message = try {
      xmlmh.xml_messages(message_code)
    }
    catch {
      case e:Exception => {
        log.warn("failed to find string with code "+message_code)
        xmlmh.xml_messages += (message_code -> "")
        ""
      }
    }
    parameters.foldLeft(xml_message)((message, parameter) =>
      message.replaceFirst("\\?", parameter))
  }

  def xmlOrDefault(message_code:String, parameters:String*):String = {
    xml(message_code, parameters.tail:_*) match {
      case "" => {
        if(parameters.size > 0) {
          log.info("default value for string code "+message_code+" is "+parameters.head)
          xmlmh.xml_messages += (message_code -> parameters.head)
          parameters.tail.foldLeft(parameters.head)((message, parameter) => message.replaceFirst("\\?", parameter))
        }
        else ""
      }
      case s:String => s
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