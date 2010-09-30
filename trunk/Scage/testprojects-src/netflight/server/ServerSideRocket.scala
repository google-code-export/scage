package netflight.server

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.net.NetServer
import org.json.JSONObject
import su.msk.dunno.scage.support.tracer.{Trace, State, StandardTracer}
import su.msk.dunno.scage.support.{ScageLibrary, Colors, Vec}

class ServerSideRocket(private val shooter:String, init_coord:Vec, dir:Vec, private val rotation:Float)
extends Colors with ScageLibrary {
  val name = "rocket"+System.nanoTime
  private var fuel = 60
  private val velocity = 10
  private val direction = dir.n
  private var coord = init_coord

  val trace = StandardTracer.addTrace(new Trace[State] {
    override def isActive = fuel > 5
    def getCoord = coord
    def getState() = new State("name", name)
    def changeState(s:State) = {}
  })

  AI.registerAI(() => {
    if(fuel > 5) {
      if(!((coord in trace) --> (coord + direction*velocity, -1 to 1, 10))) {
        fuel = 5
      }
      NetServer.addOutgoingData(name, new JSONObject().put("type", "rocket")
                                                      .put("x", coord.x.toInt)
                                                      .put("y", coord.y.toInt)
                                                      .put("rotation", rotation.toInt))
    }
    else if(fuel > 0) NetServer.addOutgoingData(name, new JSONObject().put("type", "explosion")
                                                                      .put("x", coord.x.toInt)
                                                                      .put("y", coord.y.toInt))
    if(fuel > 0) fuel -= 1
  })
}