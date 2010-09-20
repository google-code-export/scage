package netflight.server

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.support.net.NetServer
import su.msk.dunno.scage.support.{ScageLibrary, Vec}

object ServerFlight extends Application with ScageLibrary {
  var next_plane = 0
  var planes:List[ServerSidePlane] = Nil

  AI.registerAI(() => {
    if(NetServer.hasNewConnection) {
      planes = new ServerSidePlane(next_plane, Vec(100,100), NetServer.lastConnected) :: planes
      next_plane += 1
    }
    if(planes.length > 0) {
      planes = planes.filter(plane => plane.client.isOnline)
      planes.foreach(plane => plane.processInputs)
      NetServer.send
      NetServer.eraseServerData
    }
  })
  
  start
}