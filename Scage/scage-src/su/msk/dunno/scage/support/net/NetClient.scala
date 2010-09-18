package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Scage
import java.net.Socket
import java.util.Scanner
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}
import org.json.{JSONException, JSONObject}
import org.apache.log4j.Logger
import su.msk.dunno.scage.handlers.Idler

object NetClient {
  private val log = Logger.getLogger(this.getClass)
  
  val server = Scage.getProperty("server")
  val port = Scage.getIntProperty("port")

  var is_connected = false
  log.debug("start connecting to server "+server+" at port "+port)
  val socket:Socket = try {
    is_connected = true
    log.debug("connected")
    new Socket(server, port)    
  }
  catch {
    case e:java.io.IOException => {
      log.debug("failed to connect to server "+server+" at port "+port);
      null
    }
  }
  val out:PrintWriter = if(is_connected) new PrintWriter(new OutputStreamWriter(socket.getOutputStream)) else null
  val in:Scanner = if(is_connected) new Scanner(new InputStreamReader(socket.getInputStream)) else null

  private var is_send_data = false
  def send = is_send_data = true
  def send(data:String) = {
    cd = data
    is_send_data = true
  }

  private var sd:String = ""
  def serverData:String = sd

  private var cd:String = ""
  def clientData = cd
  def clientData_= (new_cd:String):Unit = cd = new_cd

  new Thread(new Runnable { // send data to server
    def run():Unit = {
      while(is_connected) {
        if(is_send_data) {
          out.println(cd)
          out.flush
          is_send_data = false
        }
        Thread.sleep(500/Idler.framerate)
      }
    }
  }).start

  new Thread(new Runnable { // receive data from server
    def run():Unit = {
      while(is_connected) {
        if(in.hasNextLine) sd = in.nextLine
        Thread.sleep(500/Idler.framerate)
      }
    }
  }).start
}