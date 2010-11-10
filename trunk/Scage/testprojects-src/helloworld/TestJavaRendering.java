package helloworld;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import su.msk.dunno.scage.support.messages.TrueTypeFont;

import java.io.*;
import java.util.Properties;

public class TestJavaRendering
{
  private static boolean isRunning = false;

  public static void main(String[] args) throws Exception
  {
    Properties p = new Properties();
    p.load(new FileInputStream("resources/scatris-properties.txt"));
    
    int width = Integer.parseInt(p.getProperty("width"));
    int height = Integer.parseInt(p.getProperty("height"));
    
    int N_x = Integer.parseInt(p.getProperty("N_x"));
    int N_y = Integer.parseInt(p.getProperty("N_y"));    
    
    float h_x = width/N_x*1.0f;
    float h_y = height/N_y*1.0f;
    
    Display.setDisplayMode(new DisplayMode(width, height));
    Display.setTitle("RenderTest");
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
    
    int BOX = createList("img/Crate.png", h_x, h_y, 0, 0, 256, 256);
    isRunning = true;
//    long start_time = System.currentTimeMillis();
    while(isRunning)
    {
        long start_time = System.currentTimeMillis();
      countFPS();
      if(Display.isCloseRequested()) isRunning = false;
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT/* | GL11.GL_DEPTH_BUFFER_BIT*/);
      GL11.glLoadIdentity();
      for(int i = 0; i <= N_x-1; i++)
      {
        for(int j = 0; j <= N_y-10; j++)
        {
          float x = i*h_x + h_x/2;
          float y = j*h_y + h_y/2;
     	    GL11.glPushMatrix();
	    GL11.glTranslatef(x, y, 0.0f);
	    GL11.glColor3f(1, 1, 1);
	    GL11.glCallList(BOX);
	    GL11.glPopMatrix();
        }
      }

      GL11.glColor3f(0,0,0);
      TrueTypeFont.instance().drawString("FPS: "+fps, 200, height-25);
      TrueTypeFont.instance().drawString("Time: "+(System.currentTimeMillis() - start_time), 200, height-40);
      start_time = System.currentTimeMillis();
      Display.sync(75);
      Display.update();
    }
    Display.destroy();
  }
  
  private static Texture getTexture(String format, InputStream in) throws IOException {
    return TextureLoader.getTexture(format, in);
  }
  
  private static Texture getTexture(String filename) throws IOException {
    String format = filename.substring(filename.length()-3);
    return getTexture(format, new FileInputStream(filename));
  }
  
  private static int next_displaylist_key = 2;
  private static int nextDisplayListKey()
  {
    int next_key = next_displaylist_key;
    next_displaylist_key += 1;
    return next_key;
  }

  private static int createList(Texture texture, float game_width, float game_height, float start_x, float start_y, float real_width, float real_height)
  {
    int list_name = nextDisplayListKey();
	 	
    float t_width = texture.getTextureWidth();
    float t_height = texture.getTextureHeight();

    GL11.glNewList(list_name, GL11.GL_COMPILE);
    //texture.bind
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
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
		
    return list_name;
  }
  private static int createList(String filename, float game_width, float game_height, float start_x, float start_y, float real_width, float real_height) throws IOException {
    return createList(getTexture(filename), game_width, game_height, start_x, start_y, real_width, real_height);
  }
  
  private static long msek = System.currentTimeMillis();
  private static int frames = 0;
  private static int fps = 0;
  private static void countFPS()
  {
    frames += 1;
    if(System.currentTimeMillis() - msek >= 1000) 
    {
      fps = frames;
      frames = 0;
      msek = System.currentTimeMillis();
    }
  }
}
