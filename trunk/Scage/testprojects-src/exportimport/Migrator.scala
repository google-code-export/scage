package exportimport

import su.msk.dunno.scage.handlers.AI
import org.apache.log4j.Logger
import su.msk.dunno.scage.handlers.net.NetServer
import su.msk.dunno.scage.support.ScageLibrary._

object Migrator {
  private val log = Logger.getLogger(this.getClass)

  properties = "migrator-properties.txt"
  NetServer.greetings = "IVision Export-Import Tool v0.1; " +
                        "Supported commands: " +
                        "pause - pause current process; " +
                        "continue - continue current process; " +
                        "stop - stop programm; " +
                        "commands - this list of commands"

  AI.registerAI(() => {
    NetServer.clients.foreach(client => {
      if(client.hasNewIncomingData) {
        val data = client.incomingData
        if(data.has("raw")) {
          data.getString("raw") match {
            case "pause" =>
              on_pause = true
              client.send("current process is set on idle")
            case "continue" =>
              on_pause = false
              client.send("continue current process")
            case "stop" => stop
            case s:Any => client.send("command "+s+" unsupported")
          }
        }
      }
    })
  })

  private var on_pause = false
  private var count = 0
  new Thread(new Runnable {
    def run():Unit = {
      while(isRunning) {
        if(!on_pause) {
          println(count)
          count += 1
          Thread.sleep(500)
        }
      }
    }
  }).start

  def main(args:Array[String]):Unit = start
}