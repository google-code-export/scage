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

  private var is_sending_data = false
  def send = is_sending_data = true
  def send(data:JSONObject):Unit = {
    while(is_sending_data) Thread.sleep(10)
    cd = data
    is_sending_data = true
  }
  def send(data:String):Unit = {
    val json = new JSONObject
    json.put("data", data)
    send(json)
  }

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = sd

  private var cd:JSONObject = new JSONObject
  def clientData = cd
  def eraseClientData = {
    while(is_sending_data) Thread.sleep(10)
    cd = new JSONObject
  }

  new Thread(new Runnable { // send data to server
    def run():Unit = {
      while(is_connected) {
        if(is_sending_data) {
          out.println(cd)
          out.flush
          is_sending_data = false
        }
        Thread.sleep(10)
      }
    }
  }).start

  new Thread(new Runnable { // receive data from server
    def run():Unit = {
      while(is_connected) {
        if(in.hasNextLine) {
          val message = in.nextLine
          sd = try{new JSONObject(message)}
          catch {
            case e:JSONException => sd.put("raw", message)
          }
        }
        Thread.sleep(10)
      }
    }
  }).start
}