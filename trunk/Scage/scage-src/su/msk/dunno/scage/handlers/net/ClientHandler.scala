package su.msk.dunno.scage.handlers.net

import org.json.{JSONException, JSONObject}
import su.msk.dunno.scage.main.Scage
import org.apache.log4j.Logger
import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}
import su.msk.dunno.scage.support.ScageProperties
import java.net.{SocketException, Socket}

class ClientHandler(val id:Int, val socket:Socket) {
  private val log = Logger.getLogger(this.getClass)

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private var cd:JSONObject = new JSONObject
  def incomingData = {
    has_new_data = false
    cd
  }

  private var has_new_data = false
  def hasNewIncomingData = has_new_data

  def send(data:JSONObject) = {
    out.println(data)
    out.flush
  }

  def disconnect = {
    socket.close
    log.info("client #"+id+" was disconnected")
  }

  private val check_timeout = ScageProperties.intProperty("check_timeout", 0)
  private var last_answer_time = System.currentTimeMillis
  def isOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  new Thread(new Runnable { // receive data from client
    def run():Unit = {
      while(Scage.isRunning) {
        if(in.ready) {
          last_answer_time = System.currentTimeMillis
          val message = try{in.readLine}
          catch {
            case e:SocketException => return
          }
          cd = try{new JSONObject(message)}
          catch {
            case e:JSONException => cd.put("raw", message)
          }
          if(cd.length > 0) has_new_data = true
        }
        Thread.sleep(10)
       }
    }
  }).start
}