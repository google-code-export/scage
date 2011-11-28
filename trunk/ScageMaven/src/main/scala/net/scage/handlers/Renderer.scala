package net.scage.handlers

import _root_.net.scage.ScageScreen._
import java.io.InputStream
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import org.lwjgl.opengl.{DisplayMode, GL11, Display}
import org.lwjgl.util.glu.GLU
import _root_.net.scage.support.ScageProperties._
import _root_.net.scage.support.ScageColors._
import _root_.net.scage.support.messages.ScageMessage._
import org.lwjgl.BufferUtils
import org.apache.log4j.Logger
import net.scage.support.ScageId._
import org.newdawn.slick.util.ResourceLoader
import java.awt.Toolkit
import net.scage.support.{SortedBuffer, ScageColor, Vec}
import collection.mutable.ArrayBuffer

object Renderer {
  protected val log = Logger.getLogger(this.getClass);

  val screen_width = property("screen.width", 800)
  val screen_height = property("screen.height", 600)
  
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

  /*private var next_displaylist_key = 10000
  def nextDisplayListKey = {
    val next_key = next_displaylist_key
    next_displaylist_key += 1
    next_key
  }*/

  lazy val initgl = {
    Display.setDisplayMode(new DisplayMode(screen_width, screen_height));
    Display.setVSyncEnabled(true);
    Display.setTitle(property("app.name", "Scage")+" - "+property("app.version", "Release"));
    Display.create();

    val (monitor_width, monitor_height) = {
      /*val gd_arr = GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices
      if(gd_arr.length > 0) {
        val dm = {
          val preferred_monitor_num = {
            if(gd_arr.length > 1 && property("screen.monitor", 0) < gd_arr.length) property("screen.monitor", 0)
            else 0
          }
          gd_arr(preferred_monitor_num).getDisplayMode
        }
        (dm.getWidth, dm.getHeight)
      } else {*/
        val d = Toolkit.getDefaultToolkit.getScreenSize
        (d.width, d.height)
      /*}*/
    }

    Display.setLocation((monitor_width - screen_width)/2, (monitor_height - screen_height)/2)

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glClearColor(0,0,0,0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
    GL11.glLoadIdentity(); // Reset The Projection Matrix
    GLU.gluOrtho2D(0, screen_width, 0, screen_height);
    //GL11.glOrtho(0, width, height, 0, 1, -1);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();

    // printing "Loading..." message. It is also necessary to properly initialize our main font (I guess)
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
    print(xmlOrDefault("renderer.loading", "Loading..."), 20, Renderer.screen_height-25, GREEN)
    update()
    Thread.sleep(1000)

    // drawing scage logo
    if(property("screen.scagelogo", true)) {
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
      val logo_texture = getTexture("resources/images/scage-logo.png")
      drawDisplayList(image(logo_texture, screen_width, screen_height, 0, 0, logo_texture.getImageWidth, logo_texture.getImageHeight), Vec(screen_width/2, screen_height/2))
      update()
      Thread.sleep(1000)
    }

    // drawing app logo or welcome message
    stringProperty("screen.splash") match {
      case screen_splash_path if "" != screen_splash_path =>
        try {
          GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
          val splash_texture = getTexture(screen_splash_path)
          drawDisplayList(image(splash_texture, screen_width, screen_height, 0, 0, splash_texture.getImageWidth, splash_texture.getImageHeight), Vec(screen_width/2, screen_height/2))
          update()
          Thread.sleep(1000)  // TODO: custom pause
        }
        catch {
          case e:Exception => log.error("failed to render splash image: "+e.getLocalizedMessage)
        }
      case _ => xmlOrDefault("app.welcome", "") match {
        case welcome_message if "" != welcome_message => {
          GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
          print(welcome_message, 20, Renderer.screen_height-25, GREEN) // TODO: custom color and position
          update()
          Thread.sleep(1000)  // TODO: custom pause
        }
        case _ =>
      }
    }

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
    val list_code = /*nextDisplayListKey*/nextId
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
  def drawCircle(coord:Vec, radius:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
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
  def drawFilledCircle(coord:Vec, radius:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glScalef(radius,radius,1)
     	  GL11.glCallList(FILLED_CIRCLE);
      GL11.glPopMatrix()
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  def drawLine(v1:Vec, v2:Vec, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINES);
    		GL11.glVertex2f(v1.x, v1.y);
    		GL11.glVertex2f(v2.x, v2.y);
    	GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawLines(edges:Vec*) {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINES);
    		edges.foreach(edge => GL11.glVertex2f(edge.x, edge.y))
    	GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawLines(edges:Array[Vec], _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    drawLines(edges:_*)
  }
  def drawLines(edges:List[Vec], _color:ScageColor) {
    if(_color != DEFAULT_COLOR) color = _color
    drawLines(edges:_*)
  }

  def drawRect(coord:Vec, width:Float, height:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINE_LOOP);
          GL11.glVertex2f(coord.x, coord.y)
          GL11.glVertex2f(coord.x + width, coord.y)
          GL11.glVertex2f(coord.x + width, coord.y - height)
          GL11.glVertex2f(coord.x, coord.y - height)
        GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawFilledRect(coord:Vec, width:Float, height:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(coord.x, coord.y)
        GL11.glVertex2f(coord.x + width, coord.y)
        GL11.glVertex2f(coord.x + width, coord.y - height)
        GL11.glVertex2f(coord.x, coord.y - height)
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawRectCentered(coord:Vec, width:Float, height:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINE_LOOP);
          GL11.glVertex2f(coord.x - width/2, coord.y - height/2)
          GL11.glVertex2f(coord.x - width/2, coord.y + height/2)
          GL11.glVertex2f(coord.x + width/2, coord.y + height/2)
          GL11.glVertex2f(coord.x + width/2, coord.y - height/2)
        GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  def drawFilledRectCentered(coord:Vec, width:Float, height:Float, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
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
  def drawPolygon(coords:Array[Vec], _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    drawPolygon(coords:_*)
  }
  def drawPolygon(coords:List[Vec], _color:ScageColor) {
    if(_color != DEFAULT_COLOR) color = _color
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
  def drawFilledPolygon(coords:Array[Vec], _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    drawFilledPolygon(coords:_*)
  }
  def drawFilledPolygon(coords:List[Vec], _color:ScageColor) {
    if(_color != DEFAULT_COLOR) color = _color
    drawFilledPolygon(coords:_*)
  }

  def drawPoint(coord:Vec, _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glDisable(GL11.GL_TEXTURE_2D)
      GL11.glBegin(GL11.GL_POINTS)
        GL11.glVertex2f(coord.x, coord.y)
      GL11.glEnd()
    GL11.glEnable(GL11.GL_TEXTURE_2D)
  }
  def drawPoints(coords:Vec*) {
    GL11.glDisable(GL11.GL_TEXTURE_2D)
      GL11.glBegin(GL11.GL_POINTS)
        coords.foreach(coord => GL11.glVertex2f(coord.x, coord.y))
      GL11.glEnd()
    GL11.glEnable(GL11.GL_TEXTURE_2D)
  }
  def drawPoints(coords:Array[Vec], _color:ScageColor = DEFAULT_COLOR) {
    if(_color != DEFAULT_COLOR) color = _color
    drawPoints(coords:_*)
  }
  def drawPoints(coords:List[Vec], _color:ScageColor) {
    if(_color != DEFAULT_COLOR) color = _color
    drawPoints(coords:_*)
  }

  // white color by default for display lists to draw in natural colors
  def drawDisplayList(list_code:Int, coord:Vec = Vec(0,0), _color:ScageColor = WHITE) {
    if(_color != DEFAULT_COLOR) color = _color
    GL11.glPushMatrix();
	  GL11.glTranslatef(coord.x, coord.y, 0.0f);
	  GL11.glCallList(list_code)
	  GL11.glPopMatrix()
  }

  private def getTexture(format:String, in:InputStream):Texture = TextureLoader.getTexture(format, in)
  private def getTexture(filename:String):Texture = {
    val format:String = filename.substring(filename.length-3)
    getTexture(format, ResourceLoader.getResourceAsStream(filename))    // can be loaded as resource from jar, hell yeah!!
  }

  def image(texture:Texture, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
	  val list_name = /*nextDisplayListKey*/nextId

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

  // TODO: is Array[Int] preferable or IndexedSeq[Int] is fine??
  //private var animations = new HashMap[Int, (IndexedSeq[Int], Int)]
  def animation(filename:String, game_width:Float, game_height:Float, real_width:Float, real_height:Float, num_frames:Int) = {
    val texture = getTexture(images_base+filename)
    val columns:Int = (texture.getImageWidth/real_width).toInt
    /*val frames = */for {
      frame <- 0 until num_frames
      x = real_width*(frame - frame/columns*columns)
      y = real_height*(frame/columns)
    } yield image(texture, game_width, game_height, x, y, real_width, real_height)
    /*val animation_id = nextId
    animations += (animation_id -> (frames, 0))
    animation_id*/
  }

  /*def drawAnimation(animation_id:Int, coord:Vec) {
    if(animations.contains(animation_id)) {
      val (frames, current_frame) = animations(animation_id)
      drawDisplayList(frames(current_frame), coord)
      val next_frame = if(current_frame >= frames.length-1) 0 else current_frame + 1
      animations(animation_id) = (frames, next_frame)
    }
  }*/
}

import Renderer._

class Renderer {
  initgl

  protected val log = Logger.getLogger(this.getClass);

  private var _scale:Float = 1.0f
  def scale = _scale
  def scale_= (value:Float) {_scale = value}

  private var window_center = () => Vec(Renderer.screen_width/2, Renderer.screen_height/2)
  def windowCenter = window_center()
  def windowCenter_= (coord: => Vec) {window_center = () => coord}
  
  private var central_coord = window_center
  def center = central_coord()
  def center_= (coord: => Vec) {central_coord = () => coord}

  case class RenderElement(operation_id:Int, render_func:() => Unit, position:Int = 0) extends Ordered[RenderElement] {
    def compare(that:RenderElement) = this.position - that.position
  }
  private val renders = SortedBuffer[RenderElement]()
  private def addRender(render_func: => Unit, position:Int = 0) = {
    val operation_id = /*nextOperationId*/nextId
    renders +=  RenderElement(operation_id, () => render_func, position)
    operation_id
  }

  def render(render_func: => Unit) = addRender(render_func)
  def render(position:Int = 0)(render_func: => Unit) = addRender(render_func, position)
  def delRenders(render_ids:Int*) = {
    if(render_ids.size > 0) {
      render_ids.foldLeft(true)((overall_result, render_id) => {
        val deletion_result = renders.find(_.operation_id == render_id) match {
          case Some(r) => {
            renders -= r
            log.debug("deleted render with id "+render_id)
            true
          }
          case None => {
            log.warn("render with id "+render_id+" not found among renders so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    } else {
      renders.clear()
      log.info("deleted all render operations")
      true
    }
  }

  private val interfaces = ArrayBuffer[(Int, () => Unit)]()
  def interface(interface_func: => Unit) = {
    val operation_id = /*nextOperationId*/nextId
    interfaces += (operation_id, () => interface_func)
    operation_id
  }
  def delInterfaces(interface_ids:Int*) = {
    if(interface_ids.size > 0) {
      interface_ids.foldLeft(true)((overall_result, interface_id) => {
        val deletion_result = interfaces.find(_._1 == interface_id) match {
          case Some(i) => {
            interfaces -= i
            log.debug("deleted interface with id "+interface_id)
            true
          }
          case None => {
            log.warn("interface with id "+interface_id+" not found among interfaces so wasn't deleted")
            false
          }
        }
        overall_result && deletion_result
      })
    } else {
      interfaces.clear()
      log.info("deleted all interface operations")
      true
    }
  }

  def render() {
    if(Display.isCloseRequested) stopApp()
    else {
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
      GL11.glLoadIdentity();
      GL11.glPushMatrix()
        val coord = window_center() - central_coord()*_scale
        GL11.glTranslatef(coord.x , coord.y, 0.0f)
        GL11.glScalef(_scale, _scale, 1)
        for(RenderElement(render_id, render_operation, _) <- renders) {
          currentOperation = render_id
          render_operation()
        }
      GL11.glPopMatrix()

      for((interface_id, interface_operation) <- interfaces) {
        currentOperation = interface_id
        interface_operation()
      }

      update()
    }
  }

  def exitRender() {
    backgroundColor = BLACK
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
    print(xmlOrDefault("renderer.exiting", "Exiting..."), 20, screen_height-25, GREEN)
    update()

    Thread.sleep(1000)
    Display.destroy()
  }
}
