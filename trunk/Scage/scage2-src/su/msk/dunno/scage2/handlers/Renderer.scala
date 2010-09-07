package su.msk.dunno.scage2.handlers

import org.lwjgl.opengl.{Display, GL11}
import su.msk.dunno.scage2.support.{ScageGL, Color, Vec}
import su.msk.dunno.scage2.prototypes.{Handler, Screen}

class Renderer(screen:Screen) extends Handler(screen:Screen) {
  private var render_list:List[() => Unit] = List[() => Unit]()
  def addRender(render: () => Unit) = {render_list = render_list ::: List(render)}

  var scale:Float = 1.0f
  private var scaleFunc:(Float) => Float = (Float) => 1
  private var isSetScaleFunc = false
  def setScaleFunc(func: (Float) => Float) = {
	  scaleFunc = func
	  isSetScaleFunc = true
  }
  
  val center = Vec(ScageGL.width/2, ScageGL.height/2)
  private var central_coord = () => Vec(ScageGL.width/2, ScageGL.height/2)
  def setCentral(coord: () => Vec) = {
    central_coord = coord
  }

  var interface:List[() => Unit] = List[() => Unit]()
  def addInterfaceElement(renderFunc: () => Unit) = {
    interface = renderFunc :: interface
  }
  
  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  var fps:Int = 0
  def countFPS() = {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }

  override def initSequence():Unit = ScageGL.init_gl
  override def actionSequence() = {
	if(Display.isCloseRequested()) Screen.stopApp
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
		GL11.glLoadIdentity();
      GL11.glPushMatrix

      if(isSetScaleFunc && !screen.onPause) scale = scaleFunc(scale)
      val coord = center - central_coord()*scale
      GL11.glTranslatef(coord.x , coord.y, 0.0f)
      GL11.glScalef(scale, scale, 1)
      render_list.foreach(render => render())
      GL11.glPopMatrix

      interface.foreach(renderFunc => renderFunc())
    Display.update();
    countFPS
  }
  override def exitSequence() = if(screen.isMain) Display.destroy();

  def setBackground(c:Color) = GL11.glClearColor(c.red, c.green, c.blue, 0)

  def setColor(c:Color) = GL11.glColor3f(c.red, c.green, c.blue)
}