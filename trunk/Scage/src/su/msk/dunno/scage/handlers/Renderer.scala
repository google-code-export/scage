package su.msk.dunno.scage.handlers

import su.msk.dunno.scage.main.Scage
import org.lwjgl.opengl.{DisplayMode, Display, GL11}
import org.lwjgl.util.glu.GLU
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import java.io.{FileInputStream, InputStream}
import su.msk.dunno.scage.prototypes.{THandler}
import su.msk.dunno.scage.support.{Color, Vec}

object Renderer extends THandler {
  private var render_list:List[() => Unit] = List[() => Unit]()
  def addRender(render: () => Unit) = {render_list = render_list ::: List(render)}

  val CIRCLE = 1
  private var next_displaylist_key = 2
  private def nextDisplayListKey() = {
    val next_key = next_displaylist_key
    next_displaylist_key += 1
    next_key
  }

  var scale:Float = 1.0f
  private var scaleFunc:(Float) => Float = (Float) => 1
  private var isSetScaleFunc = false
  def setScaleFunc(func: (Float) => Float) = {
	  scaleFunc = func
	  isSetScaleFunc = true
  }

  val width = Scage.getIntProperty("width");
  val height = Scage.getIntProperty("height");
  val center = Vec(width/2, height/2)
  private var central_coord = () => Vec(width/2, height/2)
  def setCentral(coord: () => Vec) = {
    central_coord = coord
  }

  Display.setDisplayMode(new DisplayMode(width, height));
  Display.setTitle(Scage.getProperty("name")+" - "+Scage.getProperty("version"));
  Display.setVSyncEnabled(true);
  Display.create();

  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glClearColor(1,1,1,0);
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
			for(i <- 0 to 100)
			{
				val cosine = Math.cos(i*2*Math.Pi/100).toFloat;
				val sine = Math.sin(i*2*Math.Pi/100).toFloat;
				GL11.glVertex2f(cosine, sine);
			}
	  GL11.glEnd();
	GL11.glEndList();

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

  override def actionSequence() = {
	if(Display.isCloseRequested())Scage.stop
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
		GL11.glLoadIdentity();
      GL11.glPushMatrix

      if(isSetScaleFunc && !Scage.onPause)scale = scaleFunc(scale)
      val coord = center - central_coord()*scale
      GL11.glTranslatef(coord.x , coord.y, 0.0f)
      GL11.glScalef(scale, scale, 1)
      render_list.foreach(render => render())
      GL11.glPopMatrix

      interface.foreach(renderFunc => renderFunc())
    Display.update();
    countFPS
  }
  override def exitSequence() = Display.destroy();

  def setBackground(c:Color) = GL11.glClearColor(c.red, c.green, c.blue, 0)

  def setColor(c:Color) = GL11.glColor3f(c.red, c.green, c.blue)
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

  def getTexture(format:String, in:InputStream):Texture = TextureLoader.getTexture(format, in)
  def getTexture(filename:String):Texture = {
    val format:String = filename.substring(filename.length-3)
    getTexture(format, new FileInputStream(filename))
  }

  def createList(texture:Texture, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
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
  def createList(filename:String, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Int = {
    createList(getTexture(filename), game_width, game_height, start_x, start_y, real_width, real_height)
  }

  def createAnimation(filename:String, game_width:Float, game_height:Float, real_width:Float, real_height:Float, num_frames:Int):Array[Int] = {
    val texture = Renderer.getTexture(filename)
    val columns:Int = (texture.getImageWidth/real_width).toInt
    def nextFrame(arr:List[Int], texture:Texture):List[Int] = {
      val x = real_width*(arr.length - arr.length/columns*columns)
      val y = real_height*(arr.length/columns)
      val next_key = Renderer.createList(texture, game_width, game_height, x, y, real_width, real_height)
      val new_arr = arr ::: List(next_key)
      if(new_arr.length == num_frames)new_arr
      else nextFrame(new_arr, texture)
    }
    nextFrame(List[Int](), texture).toArray
  }
}