package netapp

import su.msk.dunno.scage.handlers.net.NetClient
import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.support.ScageLibrary

object ChatClient extends Application with ScageLibrary {
  AI.registerAI(() => NetClient.clientData.put("Greeting", "Hello Worldddd!"))
  start
}