package net.scage.support

import java.util.Properties
import org.newdawn.slick.util.ResourceLoader
import com.weiglewilczek.slf4s.Logger
import collection.mutable.ArrayBuffer

trait HaveProperties {
  def property[A : Manifest](key:String, default:A):A
  def stringProperty(key:String) = property(key, "")
  def intProperty(key:String) = property(key, 0)
  def floatProperty(key:String) = property(key, 0.0f)
  def booleanProperty(key:String) = property(key, false)
}

object ScageProperties extends HaveProperties {
  private var scage_properties = new ScageProperties {
    val properties = "scage.properties"  
  }
  
  def property[A : Manifest](key:String, default:A):A = scage_properties.property(key, default)
}

trait ScageProperties extends HaveProperties {
  ScageProperties.scage_properties = this
  private val log = Logger(this.getClass.getName)

  def properties:String

  private lazy val props:Properties = load(properties)

  private lazy val fileNotFound = {
    log.error("failed to load properties: file "+properties+" not found")
    new Properties
  }
  private def load(property_filename:String):Properties = {
    if(property_filename == "") {
      log.warn("warning: no properties file set, using defaults")
      load("scage.properties")
    } else {
      try {
        val p = new Properties
        p.load(ResourceLoader.getResourceAsStream(property_filename))   // can be loaded as resource from jar
        log.info("loaded properties file "+property_filename)
        p
      } catch {
        //case ex:FileNotFoundException =>
        case ex:Exception =>
          if(!property_filename.contains("properties/")) {
            log.warn("failed to load properties: file "+property_filename+" not found")
            load("properties/" + property_filename)
          } else fileNotFound
      }
    }
  }

  private val props_already_read = ArrayBuffer[String]()
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
}