package netflight.client

import su.msk.dunno.scage.handlers.net.NetClient
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.{Renderer, AI}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}
import su.msk.dunno.scage.support.messages.Message
import su.msk.dunno.scage.support.ScageLibrary._

object ClientFlight extends Application {
  properties = "scage-properties.txt"

  AI.registerAI(() => {
    NetClient.send
    NetClient.eraseOutgoingData
  })

  // controls
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => NetClient.addOutgoingData("left"))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => NetClient.addOutgoingData("right"))
  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => NetClient.addOutgoingData("up"))
  Controller.addKeyListener(Keyboard.KEY_SPACE, 10, () => NetClient.addOutgoingData("space"))

  // background
  val LAND = Renderer.createList("img/land.png", 800, 600, 0, 0, 800, 600)
    Renderer.addRender(() => {
      GL11.glPushMatrix();
      Renderer.setColor(WHITE)

      GL11.glTranslatef(Renderer.width/2, Renderer.height/2, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(-Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(0, -Renderer.height, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)
      GL11.glTranslatef(Renderer.width, 0, 0.0f);
      GL11.glCallList(LAND)

      GL11.glPopMatrix()
    })

  // fps
  Renderer.addInterfaceElement(() => Message.print("fps: "+fps, 20, Renderer.height-20, YELLOW))

  // game objects
  val PLANE_IMAGE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
  val ROCKET_ANIMATION = Renderer.createAnimation("img/rocket_animation.png", 10, 29, 14, 44, 3)
  val EXPLOSION_ANIMATION = Renderer.createAnimation("img/explosion_animation.png", 36, 35, 72, 69, 3)
  Renderer.addRender(() => {
    if(NetClient.hasNewIncomingData) {
      val server_data = NetClient.incomingData
      val game_objects = server_data.names
      println(game_objects)
      for(i <- 0 to game_objects.length-1) {
        val object_name = game_objects.getString(i)
        val game_object = server_data.getJSONObject(object_name)
        val object_type = game_object.getString("type")
        val coord = Vec(game_object.getInt("x"), game_object.getInt("y"))
        GL11.glPushMatrix();
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        Renderer.setColor(WHITE)
        if("plane".equals(object_type) || "rocket".equals(object_type))
          GL11.glRotatef(game_object.getInt("rotation"), 0.0f, 0.0f, 1.0f)
        object_type match {
          case "plane" => GL11.glCallList(PLANE_IMAGE)
          case "rocket" => GL11.glCallList(ROCKET_ANIMATION(0))
          case "explosion" => GL11.glCallList(EXPLOSION_ANIMATION(0))
          case _ =>
        }
        GL11.glPopMatrix()
      }
    }
  })

  start
}