package net.scage.support

import java.util.Properties
import org.newdawn.slick.util.ResourceLoader
import com.weiglewilczek.slf4s.Logger
import parsers.FormulaParser

trait ScagePropertiesTrait {
  def property[A : Manifest](key:String, default:A):A
  def stringProperty(key:String) = property(key, "")
  def intProperty(key:String) = property(key, 0)
  def floatProperty(key:String) = property(key, 0.0f)
  def booleanProperty(key:String) = property(key, false)

  def property[A : Manifest](key:String, default:A, condition:(A => (Boolean,  String))):A
  def stringProperty(key:String, condition:(String => (Boolean,  String))) = property(key, "", (value:String) => (true, ""))
  def intProperty(key:String, condition:(Int => (Boolean,  String))) = property(key, 0, (value:Int) => (true, ""))
  def floatProperty(key:String, condition:(Float => (Boolean,  String))) = property(key, 0.0f, (value:Float) => (true, ""))
  def booleanProperty(key:String, condition:(Boolean => (Boolean,  String))) = property(key, false, (value:Boolean) => (true, ""))
}

object ScageProperties extends ScagePropertiesTrait {
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
  private def parsedProperty[A : Manifest](key:String, p:String):A = {
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
          throw new Exception("supported boolean properties are: yes/no, 1/0, true/false, on/off")
        }
      case "net.scage.support.ScageColor" =>
        ScageColor.fromString(p) match {
          case Some(c) => c.asInstanceOf[A]
          case None => throw new Exception(p+" not found among colors")
        }
      case "net.scage.support.Vec" =>
        Vec.fromString(p) match {
          case Some(v) => v.asInstanceOf[A]
          case None =>  throw new Exception("correct vec format: [x, y]")
        }
      case _ => p.asInstanceOf[A] // assuming A is String here. If not - we throw exception
    }
  }
  def property[A : Manifest](key:String, default:A):A = {
    getProperty(key) match {
      case p:String =>
        try {
          parsedProperty(key, p)
        }
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            defaultValue(key, default)
        }
      case _ => defaultValue(key, default)
    }
  }

  def property[A : Manifest](key:String, default:A, condition:(A => (Boolean,  String))):A = {
    getProperty(key) match {
      case p:String =>
        try {
          val value = parsedProperty(key, p)
          val (is_value_accepted, reason) = condition(value)
          if(!is_value_accepted) {
            log.warn("value "+value+" is unaccepted: "+reason+"; using default")
            defaultValue(key, default)
          } else value
        }
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            defaultValue(key, default)
        }
      case _ => defaultValue(key, default)
    }
  }
}