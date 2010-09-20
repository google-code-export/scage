package netapp

import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.support.net.NetServer
import su.msk.dunno.scage.handlers.AI

object ChatServer extends Application with ScageLibrary {
  AI.registerAI(() => {
    NetServer.clients.foreach(client => {
      if(client.clientData.length != 0) {
        println(client.clientData)
        NetServer.serverData.put(client.id+"", client.clientData)
        client.eraseClientData
      }
    })
    if(NetServer.serverData.length != 0) {
      NetServer.send
      NetServer.eraseServerData
    }
  })

  start
}