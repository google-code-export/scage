package su.msk.dunno.scage.support

import java.io.{FileInputStream, FileNotFoundException}
import java.util.Properties
import org.apache.log4j.Logger

object ScageProperties {
  private val log = Logger.getLogger(this.getClass);

  private def is_loaded = properties != null
  private val properties:Properties = {
    if(properties == null) {
      val p:Properties = new Properties()
      try{p.load(new FileInputStream("options.txt"))}
      catch {
        case ex:FileNotFoundException =>
          log.debug("failed to load properties: options.txt not found")
        null
      }
      p
    }
    else properties
  }
  private def getProperty(key:String) = {
    if(!is_loaded) null
    else {
      val p = properties.getProperty(key)
      if(p == null) log.debug("failed to find property "+key)
      p
    }
  }

  def stringProperty(key:String):String = stringProperty(key, "")
  def stringProperty(key:String, default:String):String = {
    val s = getProperty(key)
    if(s != null) {
      log.debug("read property "+key+": "+s)
      s
    }
    else default
  }

  def intProperty(key:String):Int = intProperty(key, 0)
  def intProperty(key:String, default:Int):Int = {
    val p = getProperty(key)
    if(p != null) {
      try {
        var i = Integer.valueOf(p).intValue
        log.debug("read property "+key+": "+i)
        i
      }
      catch {
        case e:NumberFormatException => {
          log.debug("property "+key+" is not integer: "+p)
          default
        }
      }
    }
    else default
  }

  def floatProperty(key:String):Float = floatProperty(key, 0)
  def floatProperty(key:String, default:Float):Float = {
    val p = getProperty(key)
    if(p != null) {
      try {
        var f = java.lang.Float.valueOf(p).floatValue
        log.debug("read property "+key+": "+f)
        f
      }
      catch {
        case e:NumberFormatException => {
          log.debug("property "+key+" is not float: "+p)
          default
        }
      }
    }
    else default
  }
  
  def booleanProperty(key:String):Boolean = booleanProperty(key, false)
  def booleanProperty(key:String, default:Boolean):Boolean = {
    val s = stringProperty(key)
    if(!"".equals(s)) s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("true")
    else default
  }
}