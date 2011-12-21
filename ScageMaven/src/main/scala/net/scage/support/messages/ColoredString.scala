package net.scage.support.messages

import collection.mutable.HashMap
import collection.mutable.ArrayBuffer
import collection.mutable.Stack
import collection.JavaConversions._

class ColoredString(original_text:String) {
  def colorSwitches():java.util.Map[Int, String] = color_switches
  def originalText() = original_text
  def text() = new_text.mkString

  private val color_switches = HashMap[Int, String]()
  private val new_text = ArrayBuffer[Char]()
  private val previous_colors = Stack[String]()
  private var pos_offset = 0

  private def findColorSwitches(text_arr:Array[Char], pos:Int, current_color:String) {
    if(pos < text_arr.length) {
      text_arr(pos) match {
        case '[' if pos < text_arr.length-1 => {
          val color_char = text_arr(pos+1) match {
            case 'r' => Some("Red")
            case 'g' => Some("Green")
            case 'b' => Some("Blue")
            case 'y' => Some("Yellow")
            case _ => None
          }
          color_char match {
            case Some(color) => {
              color_switches += (pos - pos_offset) -> color
              pos_offset += 2
              previous_colors.push(current_color)
              if(pos < text_arr.length-2) findColorSwitches(text_arr, pos+2, color)
            }
            case None => {
              new_text += text_arr(pos)
              findColorSwitches(text_arr, pos+1, current_color)
            }
          }
        }
        case ']' => {
          val previous_color = if(previous_colors.length > 0) previous_colors.pop() else "DefaultColor"
          color_switches += (pos - pos_offset) -> previous_color
          pos_offset += 1
          if(pos < text_arr.length-1) findColorSwitches(text_arr, pos+1, previous_color)
        }
        case _ => {
          new_text += text_arr(pos)
          if(pos < text_arr.length-1) findColorSwitches(text_arr, pos+1, current_color)
        }
      }
    }
  }
  findColorSwitches(original_text.toCharArray, 0, "DefaultColor")

  override def toString = "ColoredString("+original_text+", "+text+", "+color_switches+")"
}