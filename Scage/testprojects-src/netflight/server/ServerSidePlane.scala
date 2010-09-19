package netflight.server

import org.json.JSONObject
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.handlers.tracer.StandardTracer
import su.msk.dunno.scage.support.net.{NetServer}

class ServerSidePlane(name:String, init_coord:Vec, inputs:() => JSONObject) {
   // parameters
  protected var delta = 5.0f
  protected var rotation = 0.0f
  var coord = init_coord
  protected def step = Vec(-0.4f*delta*Math.sin(Math.toRadians(rotation)).toFloat,
                           0.4f*delta*Math.cos(Math.toRadians(rotation)).toFloat)

  def processInputs = {
    if(inputs().has("left")) rotation -= 0.2f*delta
    if(inputs().has("right")) rotation += 0.2f*delta
    if(inputs().has("up")) if(delta < 15) delta += 0.5f

    coord = StandardTracer.getNewCoord(coord + step)
    NetServer.serverData.put(name, new JSONObject().put("x", coord.x.toInt)
                                                   .put("y", coord.y.toInt)
                                                   .put("rotation", rotation))
  }
}