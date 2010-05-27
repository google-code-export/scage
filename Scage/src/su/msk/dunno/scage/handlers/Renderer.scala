package su.msk.dunno.scage.handlers

import eventmanager.EventManager
import su.msk.dunno.scage.main.Engine
import org.lwjgl.opengl.{DisplayMode, Display, GL11}
import org.lwjgl.util.glu.GLU
import su.msk.dunno.scage.support.{Vec, Color}
import org.newdawn.slick.opengl.{TextureLoader, Texture}
import java.io.{FileInputStream, InputStream}
import su.msk.dunno.scage.prototypes.{Physical, THandler}
import su.msk.dunno.scage.objects.StaticBall
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.support.messages.Message

object Renderer extends THandler {
  val CIRCLE = 1
  private var next_displaylist_key = 2
  def nextDisplayListKey() = {
    val next_key = next_displaylist_key
    next_displaylist_key += 1
    next_key
  }

  val width = Engine.getIntProperty("width");
  val height = Engine.getIntProperty("height");
  val center = Vec(width/2, height/2)
  private var central_object:Physical = new StaticBall(center)
  def setCentral(obj:Physical) = {
    central_object = obj
  }

  Display.setDisplayMode(new DisplayMode(width, height));
  Display.setTitle(Engine.getProperty("name")+" - "+Engine.getProperty("version"));
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
  addInterfaceElement(() => if(Engine.onPause)Message.print("PAUSE", Vec(width/2-20, height/2+60)))  

  val auto_scaling = Engine.getBooleanProperty("auto_scaling")
  private var scale:Float = 2
  EventManager.addKeyListener(Keyboard.KEY_ADD, 10, () => if(scale < 2)scale += 0.01f)
  EventManager.addKeyListener(Keyboard.KEY_SUBTRACT, 10, () => if(scale > 0.5f)scale -= 0.01f)
  override def actionSequence() = {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
		GL11.glLoadIdentity();
      GL11.glPushMatrix

      if(auto_scaling && EventManager.last_key != Keyboard.KEY_ADD && EventManager.last_key != Keyboard.KEY_SUBTRACT) {
        val factor = -3.0f/2000*central_object.velocity.norma2 + 2
        if(factor > scale+0.1f && scale < 2)scale += 0.01f
        else if(factor < scale-0.1f && scale > 0.5f)scale -=0.01f
      }

      val coord = center - central_object.coord*scale
      GL11.glTranslatef(coord.x, coord.y, 0.0f)
      GL11.glScalef(scale, scale, 1)
      Engine.getObjects.foreach(o => o.render)
      GL11.glPopMatrix

      interface.foreach(renderFunc => renderFunc())
    Display.update();
  }

  def setColor(c:Color) = GL11.glColor3f(c.getRed, c.getGreen, c.getBlue)
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

  override def exitSequence() = Display.destroy();

  def getTexture(format:String, in:InputStream):Texture = TextureLoader.getTexture(format, in)

  def getTexture(filename:String):Texture = {
    val format:String = filename.substring(filename.length-3)
    getTexture(format, new FileInputStream(filename))
  }

  def createList(list_name:Int, texture:Texture, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Unit = {
		val t_width:Float = texture.getImageWidth
		val t_height:Float = texture.getImageHeight

		GL11.glNewList(list_name, GL11.GL_COMPILE);
		//texture.bind
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID)
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(start_x/t_width, start_y/t_height);
	    GL11.glVertex2f(-game_width, game_height);

			GL11.glTexCoord2f((start_x+real_width)/t_width, start_y/t_height);
			GL11.glVertex2f(game_width, game_height);

			GL11.glTexCoord2f((start_x+real_width)/t_width, (start_y+real_height)/t_height);
			GL11.glVertex2f(game_width, -game_height);

	    GL11.glTexCoord2f(start_x/t_width, (start_y+real_height)/t_height);
			GL11.glVertex2f(-game_width, -game_height);
		GL11.glEnd();
		GL11.glEndList();
	}

  def createList(list_name:Int, filename:String, game_width:Float, game_height:Float, start_x:Float, start_y:Float, real_width:Float, real_height:Float):Unit = {
    val format:String = filename.substring(filename.length-3)
    createList(list_name, getTexture(filename), game_width, game_height, start_x, start_y, real_width, real_height)
  }
}