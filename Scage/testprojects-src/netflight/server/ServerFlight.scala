package netflight.server

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.support.net.NetServer
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

object ServerFlight extends Application with ScageLibrary {
  var next_plane = 0
  var planes:List[ServerSidePlane] = Nil

  AI.registerAI(() => {
    val num_clients = NetServer.clients.length
    if(num_clients  > next_plane) {
      planes = new ServerSidePlane(num_clients+"", Vec(100,100), NetServer.clients(num_clients - 1)) :: planes
    }
    if(planes.length > 0) {
      planes = planes.filter(plane => plane.client.isOnline)
      next_plane = planes.length
      planes.foreach(plane => plane.processInputs)
      //NetServer.serverData.put("num_players", planes.length)
      NetServer.send
      NetServer.eraseServerData
    }
  })
  
  start
}