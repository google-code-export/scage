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
    _props = load
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
      case ex:FileNotFoundException =>
        if(!file.contains("properties/")) {
          _file = "properties/" + _file
          log.debug("development mode: looking for properties in the respective folder")
          load
        }
        else fileNotFound
    }
  }

  private def getProperty(key:String) = {
    if(props == null) null else props.getProperty(key) match {
      case p:String =>
        log.info("read property "+key+": "+p)
        p.trim
      case _ =>
        log.error("failed to find property "+key)
        null
    }
  }
  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for "+key+" is "+(if("".equals(default.toString)) "empty string" else default))
    default
  }

  def property[A](key:String, default:A)(implicit m:Manifest[A]):A = {
    getProperty(key) match {
      case p:String =>
        try {
          m.toString match {
            case "Int" => p.toInt.asInstanceOf[A]
            case "Float" => p.toFloat.asInstanceOf[A]
            case "Double" => p.toDouble.asInstanceOf[A]
            case "Boolean" =>
              if(p.equalsIgnoreCase("yes")  || p.equalsIgnoreCase("1") ||
                 p.equalsIgnoreCase("true") || p.equalsIgnoreCase("on")) true.asInstanceOf[A]
              else if(p.equalsIgnoreCase("no")    || p.equalsIgnoreCase("0") ||
                      p.equalsIgnoreCase("false") || p.equalsIgnoreCase("off")) false.asInstanceOf[A]
              else {
                log.info("boolean property "+p+" unsupported")
                log.info("supported boolean properties: yes/no, 1/0, true/false, on/off")
                defaultValue(key, default)
              }
            case _ => p.asInstanceOf[A]
          }
        }
        catch {
          case e:Exception =>
            log.error("failed to use property ("+key+" : "+p+") as "+m)
            defaultValue(key, default)
        }
      case _ => defaultValue(key, default)
    }
  }

  def stringProperty(key:String) = property(key, "")
  def intProperty(key:String) = property(key, 0)
  def floatProperty(key:String) = property(key, 0.0f)  
  def booleanProperty(key:String) = property(key, false)
}