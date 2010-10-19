package netflight.server

import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.support.Vec
import su.msk.dunno.scage.support.ScageLibrary._

object ServerFlight extends Application {
  properties = "scage-properties.txt"

  var next_plane = 0

  AI.registerAI(() => {
    if(NetServer.hasNewConnection) {
      new ServerSidePlane("plane"+next_plane, Vec(100,100), NetServer.lastConnected)
      next_plane += 1
    }
    NetServer.send
    NetServer.eraseOutgoingData
  })
  
  start
}