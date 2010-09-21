package netflight.server

import su.msk.dunno.scage.support.Colors
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.support.net.NetServer
import org.json.JSONObject

class ServerSideRocket(private val shooter:String, init_coord:Vec, dir:Vec, private val rotation:Float) extends Colors {
  private var fuel = 60
  private val velocity = 10
  private val direction = dir.n
  private var coord = init_coord

  AI.registerAI(() => {
    if(fuel > 5) {
      coord = StandardTracer.getNewCoord(coord + direction*velocity)
      NetServer.serverData.put("rocket"+System.currentTimeMillis, new JSONObject().put("type", "rocket")
                                                     .put("x", coord.x)
                                                     .put("y", coord.y)
                                                     .put("rotation", rotation))
    }
    else if(fuel > 0) {
      NetServer.serverData.put("explosion"+System.currentTimeMillis, new JSONObject().put("type", "explosion")
                                                     .put("x", coord.x)
                                                     .put("y", coord.y))
    }
    if(fuel > 0) fuel -= 1
  })
}