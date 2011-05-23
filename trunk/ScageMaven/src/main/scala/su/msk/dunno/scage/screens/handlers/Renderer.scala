package su.msk.dunno.scage.screens.handlers

import su.msk.dunno.scage.screens.ScageScreen._
import java.io.{InputStream, FileInputStream}
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import org.lwjgl.opengl.{DisplayMode, GL11, Display}
import org.lwjgl.util.glu.GLU
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.{ScageColor, Vec}
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import org.lwjgl.BufferUtils
import org.apache.log4j.Logger

object Renderer {
  protected val log = Logger.getLogger(this.getClass);

  val width = property("screen.width", 800)
  val height = property("screen.height", 600)
  
  val framerate = property("framerate", 100)

  private var _fps:Int = 0
  def fps = _fps

  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  private def countFPS() {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      _fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }
  
  def update() {
    Display.sync(framerate)
    Display.update()
    countFPS()
  }

  private var next_displaylist_key = 10000
  def nextDisplayListKey = {
    val next_key = next_displaylist_key
    next_displaylist_key += 1
    next_key
  }

  lazy val initgl = {
    Display.setDisplayMode(new DisplayMode(width, height));
    Display.setVSyncEnabled(true);
    Display.create();
    Display.setTitle(property("app.name", "Scage")+" - "+property("app.version", "Release"));

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glClearColor(0,0,0,0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
    GL11.glLoadIdentity(); // Reset The Projection Matrix
    GLU.gluOrtho2D(0, width, 0, height);
    //GL11.glOrtho(0, width, height, 0, 1, -1);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();

    print(xmlOrDefault("renderer.loading", "Loading..."), 20, Renderer.height-25, GREEN)
    stringProperty("screen.splash") match {
      case "" =>
      case screen_splash_path:String =>
        try {
          val splash_texture = getTexture(screen_splash_path)
          drawDisplayList(image(splash_texture, width, height, 0, 0, splash_texture.getImageWidth, splash_texture.getImageHeight), Vec(width/2, height/2))
        }
        catch {
          case e:Exception => log.error("failed to render splash image: "+e.getLocalizedMessage)
        }
    }
    update()
    Thread.sleep(1000)

    log.info("initialized opengl system")
  }
  initgl

  def backgroundColor = {
    val background_color = BufferUtils.createFloatBuffer(16)    
    GL11.glGetFloat(GL11.GL_COLOR_CLEAR_VALUE, background_color)
    new ScageColor(background_color.get(0), background_color.get(1), background_color.get(2))
  }
  def backgroundColor_=(c:ScageColor) {GL11.glClearColor(c.red, c.green, c.blue, 0)}
  
  def color = {
    val _color = BufferUtils.createFloatBuffer(16)
    GL11.glGetFloat(GL11.GL_CURRENT_COLOR, _color)
    new ScageColor(_color.get(0), _color.get(1), _color.get(2))
  }
  def color_=(c:ScageColor) {GL11.glColor3f(c.red, c.green, c.blue)}

  def displayList(func: => Unit) = {
    val list_code = nextDisplayListKey
    GL11.glNewList(list_code, GL11.GL_COMPILE);
    func
    GL11.glEndList();
    list_code
  }

  private val CIRCLE = displayList {
    GL11.glBegin(GL11.GL_LINE_LOOP);
      for(i <- 0 to 100) {
        val cosine = math.cos(i*2*math.Pi/100).toFloat;
        val sine = math.sin(i*2*math.Pi/100).toFloat;
        GL11.glVertex2f(cosine, sine);
      }
    GL11.glEnd();
  }
  def drawCircle(coord:Vec, radius:Float, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glScalef(radius,radius,1)
     	  GL11.glCallList(CIRCLE);
      GL11.glPopMatrix()
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  private val FILLED_CIRCLE = displayList {
    GL11.glBegin(GL11.GL_TRIANGLE_FAN);
      for(i <- 0 to 100) {
        val cosine = math.cos(i*2*math.Pi/100).toFloat;
        val sine = math.sin(i*2*math.Pi/100).toFloat;
        GL11.glVertex2f(cosine, sine);
      }
    GL11.glEnd();
  }
  def drawFilledCircle(coord:Vec, radius:Float, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glScalef(radius,radius,1)
     	  GL11.glCallList(FILLED_CIRCLE);
      GL11.glPopMatrix()
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  def drawLine(v1:Vec, v2:Vec, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINES);
    		GL11.glVertex2f(v1.x, v1.y);
    		GL11.glVertex2f(v2.x, v2.y);
    	GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  def drawRect(coord:Vec, width:Float, height:Float, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINE_LOOP);
          GL11.glVertex2f(coord.x - width/2, coord.y - height/2)
          GL11.glVertex2f(coord.x - width/2, coord.y + height/2)
          GL11.glVertex2f(coord.x + width/2, coord.y + height/2)
          GL11.glVertex2f(coord.x + width/2, coord.y - height/2)
        GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawFilledRect(coord:Vec, width:Float, height:Float, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(coord.x - width/2, coord.y - height/2)
        GL11.glVertex2f(coord.x - width/2, coord.y + height/2)
        GL11.glVertex2f(coord.x + width/2, coord.y + height/2)
        GL11.glVertex2f(coord.x + width/2, coord.y - height/2)
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  def drawPolygon(coords:Vec*) {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glBegin(GL11.GL_LINE_LOOP);
        for(coord <- coords) GL11.glVertex2f(coord.x, coord.y)
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawPolygon(coords:Array[Vec], _color:ScageColor = color) {
    color = _color
    drawPolygon(coords:_*)
  }
  def drawFilledPolygon(coords:Vec*) {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      /*GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
      GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_LINE);*/
      GL11.glBegin(GL11.GL_POLYGON);
        for(coord <- coords) GL11.glVertex2f(coord.x, coord.y)
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawFilledPolygon(coords:Array[Vec], _color:ScageColor = color) {
    color = _color
    drawFilledPolygon(coords:_*)
  }

  def drawPoint(coord:Vec, _color:ScageColor = color) {
    color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D)
      GL11.glBegin(GL11.GL_POINTS)
        GL11.glVertex2f(coord.x, coord.y)
      GL11.glEnd()
    GL11.glEnable(GL11.GL_TEXTURE_2D)
  }

  def drawDisplayList(list_code:Int, coord:Vec = Vec(0,0), _color:ScageColor = WHITE) {
    color = _color
    GL11.glPushMatrix();
	  GL11.glTranslatef(coord.x, coord.y, 0.0f);
	  GL11.glCallList(list_code)
	  GL11.glPopMatrix()
  }

  private def getTexture(format:String, in:InputStream):Texture = TextureLoader.getTexture(format, in)
  private def getTexture(filename:String):Texture = {
    val format:String = filename.substring(filename.length-3)
    getTexture(format, new FileInputStream(filename))
  }

  def image(texture:Texture, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
	  val list_name = nextDisplayListKey

		val t_width:Float = texture.getTextureWidth
		val t_height:Float = texture.getTextureHeight

		GL11.glNewList(list_name, GL11.GL_COMPILE);
		//texture.bind
	  	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID)
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(start_x/t_width, start_y/t_height);
	    GL11.glVertex2f(-game_width/2, game_height/2);

			GL11.glTexCoord2f((start_x+real_width)/t_width, start_y/t_height);
			GL11.glVertex2f(game_width/2, game_height/2);

			GL11.glTexCoord2f((start_x+real_width)/t_width, (start_y+real_height)/t_height);
			GL11.glVertex2f(game_width/2, -game_height/2);

	    GL11.glTexCoord2f(start_x/t_width, (start_y+real_height)/t_height);
			GL11.glVertex2f(-game_width/2, -game_height/2);
		GL11.glEnd();
		GL11.glEndList();

		list_name
	}

  val images_base = property("images.base", "resources/images/")
  def image(filename:String, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
    image(getTexture(images_base+filename), game_width, game_height, start_x, start_y, real_width, real_height)
  }

  // TODO : rewrite this using for-statement
  def animation(filename:String, game_width:Float, game_height:Float, real_width:Float, real_height:Float, num_frames:Int):Array[Int] = {
    val texture = getTexture(images_base+filename)
    val columns:Int = (texture.getImageWidth/real_width).toInt
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val x = real_width*(arr.length - arr.length/columns*columns)
      val y = real_height*(arr.length/columns)
      val next_key = Renderer.image(texture, game_width, game_height, x, y, real_width, real_height)
      val new_arr = arr ::: List(next_key)
      if(new_arr.length == num_frames)new_arr
      else nextFrame(new_arr, texture)
    }
    nextFrame(List[Int](), texture).toArray
  }
}

import Renderer._

class Renderer {
  initgl

  protected val log = Logger.getLogger(this.getClass);

  private var _scale:Float = 1.0f
  def scale = _scale
  def scale_= (value:Float) {_scale = value}

  private var window_center = () => Vec(Renderer.width/2, Renderer.height/2)
  def windowCenter = window_center()
  def windowCenter_= (coord: => Vec) {window_center = () => coord}
  
  private var central_coord = window_center
  def center = central_coord()
  def center_= (coord: => Vec) {central_coord = () => coord}

  private var renders:List[(Int, () => Unit)] = Nil
  def render(render_func: => Unit) = {
    val operation_id = nextOperationId
    renders = renders ::: List((operation_id, () => render_func))
    operation_id
  }
  def delRender(render_id:Int) = {
    val old_renders_size = renders.size
    renders = renders.filterNot(_._1 == render_id)
    val deletion_result = renders.size != old_renders_size
    if(deletion_result) log.debug("deleted render with id "+render_id)
    else log.warn("render with id "+render_id+" not found among renders so wasn't deleted")
    deletion_result
  }

  private var interfaces:List[(Int, () => Unit)] = Nil
  def interface(interface_func: => Unit) = {
    val operation_id = nextOperationId
    interfaces = (operation_id, () => interface_func) :: interfaces
    operation_id
  }
  def delInterface(interface_id:Int) = {
    val old_interfaces_size = interfaces.size
    interfaces = interfaces.filterNot(_._1 == interface_id)
    val deletion_result = interfaces.size != old_interfaces_size
    if(deletion_result) log.debug("deleted interface with id "+interface_id)
    else log.warn("interface with id "+interface_id+" not found among interfaces so wasn't deleted")
    deletion_result
  }

  def dellAll() {
    renders = Nil
    interfaces = Nil
    log.info("deleted all drawing operations")
  }

  def render() {
    if(Display.isCloseRequested) allStop()
    else {
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
      GL11.glLoadIdentity();
      GL11.glPushMatrix()
        val coord = window_center() - central_coord()*_scale
        GL11.glTranslatef(coord.x , coord.y, 0.0f)
        GL11.glScalef(_scale, _scale, 1)
        renders.foreach(render_func => render_func._2())
      GL11.glPopMatrix()

      interfaces.foreach(interface_func => interface_func._2())

      update()
    }
  }

  def exitRender() {
    backgroundColor = BLACK
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
    print(xmlOrDefault("renderer.exiting", "Exiting..."), 20, height-25, GREEN)
    update()

    Thread.sleep(1000)
    Display.destroy()
  }
}
