package netapp

import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.handlers.AI

object ChatServer extends Application with ScageLibrary {
  AI.registerAI(() => {
    NetServer.clients.foreach(client => {
      if(client.hasNewIncomingData) {
        val data = client.incomingData
        NetServer.addOutgoingData(client.id, data)
      }
    })
    if(NetServer.hasOutgoingData) {
      NetServer.send
      NetServer.eraseOutgoingData
    }
  })

  /*val start_time = System.currentTimeMillis
  var count = 0
  AI.registerAI(() => {
    if(NetServer.numClients > 0) {
      if(System.currentTimeMillis - start_time  < 10000) {
        NetServer.eraseOutgoingData
        NetServer.addOutgoingData("count", count)
        NetServer.send        
        count += 1
      }
      else stop
    }
  })*/

  start
}