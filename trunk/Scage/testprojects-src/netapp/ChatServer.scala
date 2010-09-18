package netapp

import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.handlers.AI

object ChatServer extends Application with ScageLibrary {
  AI.registerAI(() => {
    NetServer.clients.foreach(client => {
      if(!"".equals(client.clientData)) {
        println(client.clientData)
        NetServer.send(client.clientData)
        client.eraseClientData
      }
    })
  })

  start
}