package net.scage.support

import java.util.Properties
import org.newdawn.slick.util.ResourceLoader
import com.weiglewilczek.slf4s.Logger
import collection.mutable.ArrayBuffer

object ScageProperties {
  private val log = Logger(this.getClass.getName)

  private var _file:String = null
  def properties = _file
  def properties_= (f:String) {
    _file = f
    log.info("properties file is "+_file)
    _props = load
  }

  private lazy val defaultPropsWarning = {
    log.warn("warning: no properties file set, using defaults")
    _file = "scage.properties"
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
    new Properties
  }
  private def load:Properties = {
    try {
      val p = new Properties
      p.load(ResourceLoader.getResourceAsStream(file))   // can be loaded as resource from jar
      log.info("loaded properties file "+file)
      props_already_read.clear()
      p
    } catch {
      //case ex:FileNotFoundException =>
      case ex:Exception =>
        if(!file.contains("properties/")) {
          log.warn("failed to load properties: file "+_file+" not found")
          //log.debug("development mode: looking for properties file in the properties folder")
          _file = "properties/" + _file
          load
        } else fileNotFound
    }
  }

  private var props_already_read = ArrayBuffer[String]()
  private def getProperty(key:String) = {
    props.getProperty(key) match {
      case p:String =>
        if(!props_already_read.contains(key)) {
          log.info("read property "+key+": "+p)
          props_already_read += key
        }
        p.trim
      case _ =>
        log.warn("failed to find property "+key)
        null
    }
  }
  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for property "+key+" is "+(if("".equals(default.toString)) "empty string" else default))
    props.put(key, default.toString)
    props_already_read += key
    default
  }

  def property[A : Manifest](key:String, default:A):A = {
    getProperty(key) match {
      case p:String =>
        try {
          manifest[A].toString match {
            case "Int" => p.toInt.asInstanceOf[A]
            case "Long" => p.toLong.asInstanceOf[A]
            case "Float" => p.toFloat.asInstanceOf[A]
            case "Double" => p.toDouble.asInstanceOf[A]
            case "Boolean" =>
              if(p.equalsIgnoreCase("yes")  || p.equalsIgnoreCase("1") ||
                 p.equalsIgnoreCase("true") || p.equalsIgnoreCase("on")) true.asInstanceOf[A]
              else if(p.equalsIgnoreCase("no")    || p.equalsIgnoreCase("0") ||
                      p.equalsIgnoreCase("false") || p.equalsIgnoreCase("off")) false.asInstanceOf[A]
              else {
                log.info("boolean property "+p+" is unsupported")
                log.info("supported boolean properties are: yes/no, 1/0, true/false, on/off")
                defaultValue(key, default)
              }
            case _ => p.asInstanceOf[A]
          }
        }
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A])
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