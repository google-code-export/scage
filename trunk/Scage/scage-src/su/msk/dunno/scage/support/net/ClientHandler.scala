package su.msk.dunno.scage.support.net

import java.net.Socket
import java.util.Scanner
import org.json.{JSONException, JSONObject}
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}
import su.msk.dunno.scage.main.Scage
import org.apache.log4j.Logger

class ClientHandler(val name:Int, val socket:Socket) {
  private val log = Logger.getLogger(this.getClass)

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
  private val in = new Scanner(new InputStreamReader(socket.getInputStream))

  private var cd:JSONObject = new JSONObject
  def clientData = cd
  def eraseClientData = cd = new JSONObject

  def send(data:JSONObject) = {
    out.println(data)
    out.flush
  }

  def disconnect = {
    socket.close
    log.debug(name+" was disconnected")
  }

  private val start_time = System.currentTimeMillis
  def isAlive = (cd.has("pong") && (System.currentTimeMillis - cd.getLong("pong")) < NetServer.check_timeout*1000) ||
                (System.currentTimeMillis - start_time < check_timeout*1000)

  new Thread(new Runnable { // receive data from client
    def run():Unit = {
      while(Scage.isRunning) { 
        if(in.hasNextLine) {
          val message = in.nextLine
          cd = try{new JSONObject(message)}
          catch {
            case e:JSONException => cd.put("raw", message)
          }
        }
        Thread.sleep(10)
       }
    }
  }).start
}