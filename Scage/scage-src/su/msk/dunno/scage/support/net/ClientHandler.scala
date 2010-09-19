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

  private val check_timeout = Scage.getIntProperty("check_timeout")
  private var last_answer_time = System.currentTimeMillis
  def isOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  new Thread(new Runnable { // receive data from client
    def run():Unit = {
      while(Scage.isRunning) {
        if(in.hasNextLine) {
          last_answer_time = System.currentTimeMillis
          val message = in.nextLine
          println(message)
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