package netapp

import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.handlers.AI

object ChatServer extends Application with ScageLibrary {
  AI.registerAI(() => {
    if(NetServer.clientData.length != 0 && !NetServer.clientData.equals(NetServer.serverData)) {
      println(NetServer.clientData)
      NetServer.serverData = NetServer.clientData
      if(NetServer.clientData.has("quit")) stop
    }
  })
  NetServer.serverData.put("Greeting", "Hello world!")
  start
}