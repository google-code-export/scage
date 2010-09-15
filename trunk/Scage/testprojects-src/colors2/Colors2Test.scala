package colors2

import su.msk.dunno.scage2.prototypes.Screen
import su.msk.dunno.scage2.support.ScageLibrary
import su.msk.dunno.scage2.support.Color
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage2.support.messages.Message

object Colors2Test extends ScageLibrary {
  def main(args:Array[String]):Unit = {
    val fields = this.getClass.getDeclaredFields
    val colors = fields.map(f => {
      f.setAccessible(true)
      try{f.get(this).asInstanceOf[Color]}
      catch {
        case ex:Exception => WHITE
      }
    })

    var color_num = 3
    val main_screen = new Screen("Main Screen") {
      renderer.addInterfaceElement(() => if(color_num >= 0 && color_num < fields.length) {
        Message.print(fields(color_num).getName, 400, 300, if("BLACK".equalsIgnoreCase(fields(color_num).getName)) WHITE else BLACK)
        try {renderer.setBackground(colors(color_num))}
        catch {
          case ex:java.lang.ClassCastException =>
        }
      })

      controller.addKeyListener(Keyboard.KEY_LEFT, () => {
        def nextColorNumInc() {
          if(color_num < fields.length - 1) color_num += 1
          else color_num = 0
          if(!WHITE.equals(colors(color_num)) || "WHITE".equalsIgnoreCase(fields(color_num).getName)) color_num
          else nextColorNumInc
        }
        nextColorNumInc
      })
      controller.addKeyListener(Keyboard.KEY_RIGHT, () => {
        def nextColorNumDec() {
          if(color_num > 0) color_num -= 1
          else color_num = fields.length - 1
          if(!WHITE.equals(colors(color_num)) || "WHITE".equalsIgnoreCase(fields(color_num).getName)) color_num
          else nextColorNumDec
        }
        nextColorNumDec
      })
    }.start
  }
}