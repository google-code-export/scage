package netflight.server

import org.json.JSONObject
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.handlers.net.{ClientHandler, NetServer}
import su.msk.dunno.scage.handlers.AI

class ServerSidePlane(val name:String, init_coord:Vec, val client:ClientHandler) {
   // parameters
  protected var delta = 5.0f
  protected var rotation = 0.0f
  var coord = init_coord
  protected def step = Vec(-0.4f*delta*Math.sin(Math.toRadians(rotation)).toFloat,
                           0.4f*delta*Math.cos(Math.toRadians(rotation)).toFloat)

  AI.registerAI(() => if(client.isOnline) processInputs)

  private var plane_side = 1
  private var shoot_cooldown = 0
  private def processInputs = {
    val client_data = client.incomingData
    if(client_data.has("left")) rotation -= 0.2f*delta
    if(client_data.has("right")) rotation += 0.2f*delta
    if(client_data.has("up")) if(delta < 15) delta += 0.5f
    if(client_data.has("space") && shoot_cooldown == 0) {
      new ServerSideRocket(name, coord + step.n.rotate(Math.Pi/2 * plane_side)*10, step, rotation);
      plane_side *= -1
      shoot_cooldown = 10
    }
    coord = StandardTracer.checkEdges(coord + step)
    if(delta > 5) delta -= 0.1f
    if(shoot_cooldown > 0)shoot_cooldown -= 1
    
    NetServer.addOutgoingData(name, new JSONObject().put("type", "plane")
                                            .put("x", coord.x.toInt)
                                            .put("y", coord.y.toInt)
                                            .put("rotation", rotation.toInt))
  }
}