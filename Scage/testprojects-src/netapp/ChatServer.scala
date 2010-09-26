package netapp

import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.handlers.AI
import org.apache.log4j.Logger

object ChatServer extends Application with ScageLibrary {
  private val log = Logger.getLogger(this.getClass)
  //var times:List[Long] = Nil

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

  //val start_time = System.currentTimeMillis
  /*var count = 0
  AI.registerAI(() => {
    if(NetServer.numClients > 0) {
      NetServer.eraseOutgoingData
      NetServer.addOutgoingData("count", count)

      val start_time = System.currentTimeMillis
      NetServer.send
      times = (System.currentTimeMillis - start_time) :: times
      if(times.length >= 1000) {
        log.debug(times.foldLeft(0)((sum, time) => sum + time.toInt)/times.length)
        times = Nil
      }
      count += 1
    }
  })*/

  start
}