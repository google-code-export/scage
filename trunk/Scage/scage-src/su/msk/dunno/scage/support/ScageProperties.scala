package su.msk.dunno.scage.support

import java.io.{FileInputStream, FileNotFoundException}
import java.util.Properties
import org.apache.log4j.Logger

object ScageProperties {
  private val log = Logger.getLogger(this.getClass)

  private var _file:String = null
  def properties = _file
  def properties_= (f:String) = {
    _file = f
    log.info("new properties file is "+_file)
  }

  private lazy val defaultPropsWarning = {
    log.warn("warning: no properties file set, using defaults")
    _file = "scage-properties.txt"
  }
  def file = {
    if(_file == null) defaultPropsWarning
    _file
  }

  private var _props:Properties = null
  private def props = {
    if(_props == null) _props = load
    _props
  }

  private lazy val fileNotFound = {
    log.error("failed to load properties: file "+_file+" not found")
    null
  }
  private def load:Properties = {
    try {
      val p = new Properties
      p.load(new FileInputStream(file))
      log.info("loaded properties file "+file)
      p
    }
    catch {
      case ex:FileNotFoundException => fileNotFound
    }
  }

  private def getProperty(key:String) = {
    val p = if(props == null) null else props.getProperty(key)
    if(p == null) log.error("failed to find property "+key)
    else log.info("read property "+key+": "+p)
    p
  }
  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for "+key+" is "+default)
    default
  }

  def property[A](key:String, default:A)(implicit m:Manifest[A]):A = {
    val p = getProperty(key)
    if(p != null) {
      try {
        m.erasure.asInstanceOf[Class[A]].cast(p)
      }
      catch {
        case e:Exception =>
          log.error("failed to use property ("+key+" : "+p+") as "+m)
          defaultValue(key, default)
      }
    }
    else defaultValue(key, default)
  }

  def stringProperty(key:String):String = stringProperty(key, "")
  def stringProperty(key:String, default:String):String = {
    val s = getProperty(key)
    if(s != null) s
    else defaultValue(key, default)
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
          defaultValue(key, default)
        }
      }
    }
    else defaultValue(key, default)
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
          defaultValue(key, default)
        }
      }
    }
    else defaultValue(key, default)
  }
  
  def booleanProperty(key:String):Boolean = booleanProperty(key, false)
  def booleanProperty(key:String, default:Boolean):Boolean = {
    val s = stringProperty(key)
    if(!"".equals(s)) s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("true")
    else defaultValue(key, default)
  }
}