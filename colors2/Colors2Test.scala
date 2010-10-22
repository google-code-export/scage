package colors2

import su.msk.dunno.scage2.prototypes.Screen
import su.msk.dunno.scage2.support.ScageLibrary
import su.msk.dunno.scage2.support.Color
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage2.support.messages.Message

object Colors2Test extends Application with ScageLibrary {
  val COLOR_MARQUEE_TEXT = new Color(248,212,168)
  val COLOR_TOOLTIP_TEXT = new Color(248,212,168)
  val COLOR_MENU_TEXT = new Color(248,212,168)
  val COLOR_PL_TEXT_NORM = new Color(248,212,168)
  val COLOR_PL_TEXT_PLAYING = new Color(247,148,29)
  val COLOR_PL_TEXT_ERROR = new Color(132,80,16)
  val COLOR_PL_TEXT_SELECTED = new Color(248,212,168)
  val COLOR_PL_BK_SELECTED = new Color(14,70,27)
  val COLOR_PL_FOCUS_RECT = new Color(247,148,29)
  val COLOR_DVR_TEXT_NORM = new Color(248,212,168)
  val COLOR_DVR_TEXT_RECORDING = new Color(247,148,29)
  val COLOR_DVR_TEXT_ERROR = new Color(255,0,0)
  val COLOR_DVR_TEXT_SELECTED = new Color(248,212,168)
  val COLOR_DVR_BK_SELECTED = new Color(14,70,27)

  val fields = this.getClass.getDeclaredFields
  val colors = fields.map(f => {
    f.setAccessible(true)
    try{f.get(this).asInstanceOf[Color]}
    catch {
      case ex:Exception => WHITE
    }
  })

  var color_num = 1
  val main_screen = new Screen("Main Screen") {
    renderer.addInterfaceElement(() => if(color_num >= 0 && color_num < fields.length) {
      Message.print(fields(color_num).getName, 400, 300, if("BLACK".equalsIgnoreCase(fields(color_num).getName)) WHITE else BLACK)
      try {renderer.setBackground(colors(color_num))}
      catch {
        case ex:java.lang.Exception =>
      }
    })

    controller.addKeyListener(Keyboard.KEY_LEFT, () => {
      def nextColorNumInc() {
        if(color_num < fields.length - 1) color_num += 1
        else color_num = 0
        if(colors(color_num) != null && (!WHITE.equals(colors(color_num)) || "WHITE".equalsIgnoreCase(fields(color_num).getName))) color_num
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