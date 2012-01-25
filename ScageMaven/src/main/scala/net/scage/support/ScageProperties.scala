package net.scage.support

import java.util.Properties
import org.newdawn.slick.util.ResourceLoader
import com.weiglewilczek.slf4s.Logger
import parsers.FormulaParser
import parsers.FormulaParser._
import collection.mutable.{HashMap, ArrayBuffer}

object ScageProperties {
  private val log = Logger(this.getClass.getName)

  val properties:String = {
    val system_property = System.getProperty("scage.properties")
    if(system_property == null || "" == system_property) "scage.properties"
    else system_property
  }

  private lazy val props:Properties = load(properties)

  private lazy val fileNotFound = {
    log.error("failed to load properties: file "+properties+" not found")
    new Properties
  }
  private def load(property_filename:String):Properties = {
    try {
      val p = new Properties
      p.load(ResourceLoader.getResourceAsStream(property_filename))   // can be loaded as resource from jar
      log.info("loaded properties file "+property_filename)
      p
    } catch {
      //case ex:FileNotFoundException =>
      case ex:Exception =>
        if(!property_filename.contains("properties/")) {
          load("properties/" + property_filename)
        } else fileNotFound
    }
  }

  private def getProperty(key:String) = {
    props.getProperty(key) match {
      case p:String =>
        log.debug("read property "+key+": "+p)
        p.trim
      case _ =>
        log.warn("failed to find property "+key)
        null
    }
  }
  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for property "+key+" is "+(if("".equals(default.toString)) "empty string" else default))
    props.put(key, default.toString)
    default
  }

  // will make it non-private on real purpose appeared
  private val formula_parser = new FormulaParser()

  def property[A : Manifest](key:String, default:A):A = {
    getProperty(key) match {
      case p:String =>
        try {
          manifest[A].toString match {
            case manifest_type @ ("Int" | "Long" | "Float" | "Double") =>
              val result = formula_parser.calculate(p)
              formula_parser.constants += (key -> result)
              manifest_type match {
                case "Int" => result.toInt.asInstanceOf[A]
                case "Long" => result.toLong.asInstanceOf[A]
                case "Float" => result.toFloat.asInstanceOf[A]
                case _ => result.asInstanceOf[A]  // I believe its 'Double' here =)
              }
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
            case "net.scage.support.ScageColor" =>
              ScageColor.fromString(p) match {
                case Some(c) => c.asInstanceOf[A]
                case None => defaultValue(key, default) 
              }
            case "net.scage.support.Vec" =>
              Vec.fromString(p) match {
                case Some(v) => v.asInstanceOf[A]
                case None =>  defaultValue(key, default)
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