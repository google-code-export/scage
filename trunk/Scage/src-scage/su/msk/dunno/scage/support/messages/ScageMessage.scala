package su.msk.dunno.scage.support.messages

import su.msk.dunno.scage.support.{ScageProperties, ScageColors, ScageColor}
import collection.mutable.HashMap
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.FileInputStream
import javax.xml.parsers.SAXParserFactory
import org.apache.log4j.Logger

object ScageMessage extends ScageColors {
  private val log = Logger.getLogger(this.getClass)
  
  val lang = ScageProperties.property("strings.lang", "en")
  val messages_base = ScageProperties.property("strings.base", "strings")
  val messages_file = messages_base + "_" + lang + ".xml"

  private val xmlmh = new XMLMessageHandler
  private val parser = SAXParserFactory.newInstance.newSAXParser
  private val fis = new FileInputStream(messages_file)
  parser.parse(fis, xmlmh)

  val font_path = ScageProperties.property("font.name", "res/fonts/DroidSans.ttf")
  val font_size = ScageProperties.property("font.size", 18)
  val glyph_from = ScageProperties.property("glyph.from", 1024)
  val glyph_to = ScageProperties.property("glyph.to", 1279)
  private val font = new SlickUnicodeFont(font_path, font_size, glyph_from, glyph_to)

  def xml(message_code:String, parameters:String*):String = {
    val xml_message = try {
      xmlmh.xml_messages(message_code)
    }
    catch {
      case e:Exception => {
        log.error("failed to find string with code "+message_code+" in file "+messages_file)
        val default = if(!parameters.isEmpty) {
          log.error("using string \""+parameters(0)+"\" as default for code "+message_code)
          parameters(0)
        }
        else ""
        xmlmh.xml_messages += (message_code -> default)
        return default
      }
    }
    return parameters.foldLeft(xml_message)((message, parameter) =>
      message.replaceFirst("\\?", parameter))
  }

  def print(message:Any, x:Float, y:Float, color:ScageColor = WHITE) = {
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
