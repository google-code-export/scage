package su.msk.dunno.scage.support

package colors2

import org.lwjgl.input.Keyboard
import messages.ScageMessage._
import su.msk.dunno.screens.ScageScreen
import ScageColors._
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.screens.handlers.Renderer._

object ScageColorTest extends Application {
  val fields = ScageColors.getClass.getDeclaredFields
  val colors = fields.map(f => {
    f.setAccessible(true)
    try{f.get(ScageColors).asInstanceOf[ScageColor]}
    catch {
      case ex:Exception => WHITE
    }
  })

  var color_num = 1
  val main_screen = new ScageScreen("Color Test", is_main_screen = true, properties="colortest-properties.txt") {
    addRender(new ScageRender {
      override def interface = {
        if(color_num >= 0 && color_num < fields.length) {
          print(fields(color_num).getName, width/2, height/2,
            if("BLACK".equalsIgnoreCase(fields(color_num).getName)) WHITE else BLACK)
          try {backgroundColor = (colors(color_num))}
          catch {
            case ex:java.lang.Exception =>
          }
        }
      }
    })

    keyListener(Keyboard.KEY_LEFT, onKeyDown = {
      def nextColorNumInc() {
        if(color_num < fields.length - 1) color_num += 1
        else color_num = 0
        if(colors(color_num) != null && (!WHITE.equals(colors(color_num)) || "WHITE".equalsIgnoreCase(fields(color_num).getName))) color_num
        else nextColorNumInc
      }
      nextColorNumInc
    })
    keyListener(Keyboard.KEY_RIGHT, onKeyDown = {
      def nextColorNumDec() {
        if(color_num > 0) color_num -= 1
        else color_num = fields.length - 1
        if(!WHITE.equals(colors(color_num)) || "WHITE".equalsIgnoreCase(fields(color_num).getName)) color_num
        else nextColorNumDec
      }
      nextColorNumDec
    })
    keyListener(Keyboard.KEY_ESCAPE, onKeyDown = stop)
  }.run
}