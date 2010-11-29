package su.msk.dunno.screens.handlers

import su.msk.dunno.screens.ScageScreen
import java.io.{InputStream, FileInputStream}
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import org.lwjgl.opengl.{DisplayMode, GL11, Display}
import org.lwjgl.util.glu.GLU
import su.msk.dunno.scage.support.ScageProperties._
import su.msk.dunno.scage.support.{Color, Colors, Vec}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.screens.prototypes.{ActionHandler, Renderable}

object Renderer {
  val width = property("width", 800);
  val height = property("height", 600);

  val framerate = property("framerate", 100)

  lazy val initgl = {
    Display.setDisplayMode(new DisplayMode(width, height));
    Display.setTitle(property("name", "Scage")+" - "+stringProperty("version"));
    Display.setVSyncEnabled(true);
    Display.create();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glClearColor(0,0,0,0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
    GL11.glLoadIdentity(); // Reset The Projection Matrix
    GLU.gluOrtho2D(0, width, 0, height);

    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();

    GL11.glNewList(CIRCLE, GL11.GL_COMPILE);
      GL11.glBegin(GL11.GL_LINE_LOOP);
        for(i <- 0 to 100) {
          val cosine = math.cos(i*2*math.Pi/100).toFloat;
          val sine = math.sin(i*2*math.Pi/100).toFloat;
          GL11.glVertex2f(cosine, sine);
        }
      GL11.glEnd();
    GL11.glEndList();

    Message.print("Loading...", 20, Renderer.height-25, Colors.GREEN)
    Display.sync(Renderer.framerate)
    Display.update
  }

  val CIRCLE = 1
  private var next_displaylist_key = 2
  private def nextDisplayListKey() = {
    val next_key = next_displaylist_key
    next_displaylist_key += 1
    next_key
  }

  def backgroundColor(c:Color) = GL11.glClearColor(c.red, c.green, c.blue, 0)
  def color(c:Color) = GL11.glColor3f(c.red, c.green, c.blue)

  def drawLine(v1:Vec, v2:Vec) = {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    	GL11.glBegin(GL11.GL_LINES);
    		GL11.glVertex2f(v1.x, v1.y);
    		GL11.glVertex2f(v2.x, v2.y);
    	GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  
  def drawCircle(coord:Vec, radius:Float) = {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glPushMatrix();
      GL11.glTranslatef(coord.x, coord.y, 0.0f);
      GL11.glScalef(radius,radius,1)
     	  GL11.glCallList(CIRCLE);
      GL11.glPopMatrix()
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  def drawDisplayList(list_code:Int, coord:Vec):Unit = drawDisplayList(list_code:Int, coord:Vec, Colors.WHITE)
  def drawDisplayList(list_code:Int, coord:Vec, color:Color):Unit = {
    GL11.glPushMatrix();
	  GL11.glTranslatef(coord.x, coord.y, 0.0f);
	  Renderer.color(color)
	  GL11.glCallList(list_code)
	  GL11.glPopMatrix()
  }

  private def getTexture(format:String, in:InputStream):Texture = TextureLoader.getTexture(format, in)
  private def getTexture(filename:String):Texture = {
    val format:String = filename.substring(filename.length-3)
    getTexture(format, new FileInputStream(filename))
  }

  def createDisplayList(texture:Texture, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
	  	val list_name = nextDisplayListKey()

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
  def createDisplayList(filename:String, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
    createDisplayList(getTexture(filename), game_width, game_height, start_x, start_y, real_width, real_height)
  }

  def createAnimation(filename:String, game_width:Float, game_height:Float, real_width:Float, real_height:Float, num_frames:Int):Array[Int] = {
    val texture = Renderer.getTexture(filename)
    val columns:Int = (texture.getImageWidth/real_width).toInt
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val x = real_width*(arr.length - arr.length/columns*columns)
      val y = real_height*(arr.length/columns)
      val next_key = Renderer.createDisplayList(texture, game_width, game_height, x, y, real_width, real_height)
      val new_arr = arr ::: List(next_key)
      if(new_arr.length == num_frames)new_arr
      else nextFrame(new_arr, texture)
    }
    nextFrame(List[Int](), texture).toArray
  }
}

class Renderer {
  def this(main_screen:ScageScreen) = {
    this()
    main_screen.addHandler(new ActionHandler {
      override def exit = {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
        Message.print("Exiting...", 20, Renderer.height-25, Colors.GREEN)
        Display.sync(Renderer.framerate)
        Display.update

        Thread.sleep(1000)
        Display.destroy
      }
    })
  }
  Renderer.initgl

  private var _scale:Float = 1.0f
  def scale = _scale
  def scale_= (value:Float) = _scale = value

  private var window_center = () => Vec(Renderer.width/2, Renderer.height/2)
  def windowCenter = window_center()
  def windowCenter_= (coord: => Vec) = window_center = () => coord
  
  private var central_coord = window_center
  def center = central_coord()
  def center_= (coord: => Vec) = central_coord = () => coord
  
  private var render_list:List[Renderable] = Nil
  def addRender(render:Renderable) = render_list = render :: render_list

  private[screens] var fps:Int = 0
  private var msek = System.currentTimeMillis
  private var frames:Int = 0
  private def countFPS() = {
    frames += 1
    if(System.currentTimeMillis - msek >= 1000) {
      fps = frames
      frames = 0
      msek = System.currentTimeMillis
    }
  }

  def render = {
    if(Display.isCloseRequested()) ScageScreen.allStop
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
		GL11.glLoadIdentity();
    GL11.glPushMatrix
      val coord = window_center() - central_coord()*_scale
      GL11.glTranslatef(coord.x , coord.y, 0.0f)
      GL11.glScalef(_scale, _scale, 1)
      render_list.foreach(renderable => renderable.render)
    GL11.glPopMatrix

    render_list.foreach(renderable => renderable.interface)

    Display.sync(Renderer.framerate)
    Display.update();

    countFPS
  }
}
