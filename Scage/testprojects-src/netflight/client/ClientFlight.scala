package netflight.client

import su.msk.dunno.scage.support.net.NetClient
import su.msk.dunno.scage.handlers.controller.Controller
import org.lwjgl.input.Keyboard
import su.msk.dunno.scage.handlers.{Renderer, AI}
import org.lwjgl.opengl.GL11
import su.msk.dunno.scage.support.{ScageLibrary, Vec}
import su.msk.dunno.scage.support.messages.Message

object ClientFlight extends Application with ScageLibrary {
  AI.registerAI(() => {
    NetClient.send
    NetClient.eraseClientData
  })

  // controls
  Controller.addKeyListener(Keyboard.KEY_LEFT, 10, () => NetClient.clientData.put("left", ""))
  Controller.addKeyListener(Keyboard.KEY_RIGHT, 10, () => NetClient.clientData.put("right", ""))
  Controller.addKeyListener(Keyboard.KEY_UP, 10, () => NetClient.clientData.put("up", ""))

  val PLANE_IMAGE = Renderer.createList("img/plane.png", 60, 60, 0, 0, 122, 121)
  Renderer.addRender(() => {
    if(NetClient.serverData.length != 0) {
      val num_players = NetClient.serverData.getInt("num_players")
      for(i <- 1 to num_players) {
        val plane = NetClient.serverData.getJSONObject(i+"")
        val coord = Vec(plane.getInt("x"), plane.getInt("y"))
        val rotation = plane.getDouble("rotation").toFloat

        GL11.glPushMatrix();
        GL11.glTranslatef(coord.x, coord.y, 0.0f);
        GL11.glRotatef(rotation, 0.0f, 0.0f, 1.0f)
        Renderer.setColor(WHITE)
        GL11.glCallList(PLANE_IMAGE)
        GL11.glPopMatrix()
        Message.print(i, coord)
      }
    }
  })

  start
}