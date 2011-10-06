package su.msk.dunno.scage;

import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class MyMavenTest
{
   private static boolean isRunning = false;

   public static void main(String[] args) throws Exception
   {
     int width = 640;
     int height = 480;

     Display.setDisplayMode(new DisplayMode(width, height));
     Display.setTitle("RenderTest");
     Display.setVSyncEnabled(true);
     Display.create();

     GL11.glEnable(GL11.GL_TEXTURE_2D);
     GL11.glClearColor(1,1,1,0);                      // Background color
     GL11.glDisable(GL11.GL_DEPTH_TEST);

     GL11.glEnable(GL11.GL_BLEND);
     GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

     GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
     GL11.glLoadIdentity(); // Reset The Projection Matrix
     GLU.gluOrtho2D(0, width, 0, height);

     GL11.glMatrixMode(GL11.GL_MODELVIEW);
     GL11.glLoadIdentity();

     isRunning = true;
//    long start_time = System.currentTimeMillis();
     while(isRunning)
     {
         if(Display.isCloseRequested()) isRunning = false;
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT /* | GL11.GL_DEPTH_BUFFER_BIT*/);  //Clear Background
         GL11.glLoadIdentity();
         GL11.glColor3f(1.0f,0.4f,1.0f);                                                // color of drawing
         GL11.glPushMatrix();
         GL11.glTranslatef(320, 240, 0.0f);
         GL11.glColor3f(1, 0, 1);
         GL11.glRectf(-10f, -10f, 10f, 10f);                                    //Draw rectangle
         GL11.glPopMatrix();

         GL11.glColor3f (0, 0, 1);
         GL11.glBegin(GL11.GL_POLYGON);
             GL11.glVertex2f (25f, 25f);
             GL11.glVertex2f (75f, 25f);
             GL11.glVertex2f (75f, 75f);
             GL11.glVertex2f (25f, 75f);
         GL11.glEnd();


         Display.sync(75);
         Display.update();
     }
     Display.destroy();
   }
}

