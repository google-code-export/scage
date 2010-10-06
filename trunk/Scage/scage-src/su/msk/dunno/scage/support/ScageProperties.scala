package su.msk.dunno.scage.support

import java.io.{FileInputStream, FileNotFoundException}
import java.util.Properties
import org.apache.log4j.Logger

object ScageProperties {
  private val log = Logger.getLogger(this.getClass);

  private def is_loaded = properties != null

  private var _file = "scage-properties.txt"
  def file = _file
  def file_= (f:String) = {
    _file = f
    properties = load
  }

  private var properties:Properties = {
    try {
      val p = new Properties
      p.load(new FileInputStream(file))
      log.info("using properties file "+file)
      p
    }
    catch {
      case ex:FileNotFoundException => null
    }
  }
  private def load:Properties = {
    try {
      val p = new Properties
      p.load(new FileInputStream(file))
      log.info("loaded properties file "+file)
      p
    }
    catch {
      case ex:FileNotFoundException =>
        log.error("failed to load properties: file "+file+" not found")
        null
    }
  }

  private lazy val noPropertiesWarning = {
    log.error("warning: there is no properties file load, using default values")
  }
  private def getProperty(key:String) = {
    if(!is_loaded) {
      noPropertiesWarning
      null
    }
    else {
      val p = properties.getProperty(key)
      if(p == null) log.error("failed to find property "+key)
      else log.info("read property "+key+": "+p)
      p
    }
  }
  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for "+key+" is "+default)
    default
  }

  def stringProperty(key:String):String = stringProperty(key, "")
  def stringProperty(key:String, default:String):String = {
    val s = getProperty(key)
    if(s != null) s
    else defaultValue[String](key, default)
  }

  def intProperty(key:String):Int = intProperty(key, 0)
  def intProperty(key:String, default:Int):Int = {
    val p = getProperty(key)
    if(p != null) {
      try {
        Integer.valueOf(p).intValue
      }
      catch {
        case e:NumberFormatException => {
          log.error("property "+key+" is not integer: "+p)
          defaultValue[Int](key, default)
        }
      }
    }
    else defaultValue[Int](key, default)
  }

  def floatProperty(key:String):Float = floatProperty(key, 0)
  def floatProperty(key:String, default:Float):Float = {
    val p = getProperty(key)
    if(p != null) {
      try {
        java.lang.Float.valueOf(p).floatValue
      }
      catch {
        case e:NumberFormatException => {
          log.error("property "+key+" is not float: "+p)
          defaultValue[Float](key, default)
        }
      }
    }
    else defaultValue[Float](key, default)
  }
  
  def booleanProperty(key:String):Boolean = booleanProperty(key, false)
  def booleanProperty(key:String, default:Boolean):Boolean = {
    val s = stringProperty(key)
    if(!"".equals(s)) s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("true")
    else defaultValue[Boolean](key, default)
  }
}