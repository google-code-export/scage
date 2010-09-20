package netflight.server

import org.json.JSONObject
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.support.net.{ClientHandler, NetServer}

class ServerSidePlane(raw_name:Any, init_coord:Vec, val client:ClientHandler) {
   // parameters
  val name = raw_name.toString 
  protected var delta = 5.0f
  protected var rotation = 0.0f
  var coord = init_coord
  protected def step = Vec(-0.4f*delta*Math.sin(Math.toRadians(rotation)).toFloat,
                           0.4f*delta*Math.cos(Math.toRadians(rotation)).toFloat)

  def processInputs = {
    if(client.clientData.has("left")) rotation -= 0.2f*delta
    if(client.clientData.has("right")) rotation += 0.2f*delta
    if(client.clientData.has("up")) if(delta < 15) delta += 0.5f

    coord = StandardTracer.getNewCoord(coord + step)
    if(delta > 5) delta -= 0.1f
    NetServer.serverData.put(name, new JSONObject().put("x", coord.x)
                                                   .put("y", coord.y)
                                                   .put("rotation", rotation))
  }
}