package netapp

import su.msk.dunno.scage.handlers.net.NetClient
import su.msk.dunno.scage.handlers.AI
import org.apache.log4j.Logger
import su.msk.dunno.scage.support.ScageLibrary._

object ChatClient extends Application {
  private val log = Logger.getLogger(this.getClass)

  var prev_number = 0
  AI.registerAI(() => {
    if(NetClient.hasNewIncomingData) {
      val next_number = NetClient.incomingData.getInt("count")
      if(next_number - prev_number > 1) log.debug(prev_number + " - " + next_number)
      prev_number = next_number
    }

    NetClient.addOutgoingData("online")
    NetClient.send
  })

  start
}