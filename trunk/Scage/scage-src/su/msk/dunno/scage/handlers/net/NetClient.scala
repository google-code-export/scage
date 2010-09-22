package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.main.Scage
import java.net.Socket
import java.util.Scanner
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}
import org.json.{JSONException, JSONObject}
import su.msk.dunno.scage.prototypes.THandler

object NetClient extends THandler {  
  val server_url = Scage.getStringProperty("server")
  val port = Scage.getIntProperty("port")

  private var is_connected = false
  def isConnected = is_connected
  private var socket:Socket = null
  private var out:PrintWriter = null
  private var in:Scanner = null
  def connect = {
    log.debug("start connecting to server "+server_url+" at port "+port)
    socket = try {new Socket(server_url, port)}
    catch {
      case e:java.io.IOException => {
        log.debug("failed to connect to server "+server_url+" at port "+port);
        null
      }
    }
    if(socket != null) {
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
      in = new Scanner(new InputStreamReader(socket.getInputStream))
      is_connected = true
      last_answer_time = System.currentTimeMillis
      log.debug("connected")
    }
  }

  def send = {
    if(is_connected) {
      out.println(cd)
      out.flush      
    }
  }
  def send(data:JSONObject):Unit = {
    cd = data
    send
  }
  def send(data:String):Unit = send(new JSONObject().put("raw", data))

  private var sd:JSONObject = new JSONObject
  def incomingData:JSONObject = {
    has_new_data = false
    sd
  }

  private var has_new_data = false
  def hasNewData = has_new_data

  private var cd:JSONObject = new JSONObject
  def outgoingData = cd
  def eraseOutgoingData = cd = new JSONObject
  def addOutgoingData(key:Any, data:Any) = cd.put(key.toString, data)
  def addOutgoingData(key:Any) = cd.put(key.toString, "")  

  private val check_timeout = Scage.getIntProperty("check_timeout")
  private var last_answer_time = System.currentTimeMillis
  def isServerOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  new Thread(new Runnable { // receive data from server
    def run():Unit = {
      while(Scage.isRunning) {
        if(is_connected) {
          if(in.hasNextLine) {
            last_answer_time = System.currentTimeMillis
            val message = in.nextLine
            sd = try{new JSONObject(message)}
            catch {
              case e:JSONException => sd.put("raw", message)
            }
            if(sd.length > 0) has_new_data = true
          }
        }
        Thread.sleep(10)
      }
    }
  }).start

  new Thread(new Runnable { // connection checker
    def run():Unit = {
      while(Scage.isRunning) {
        if(!isServerOnline) {
          if(is_connected) disconnect
          connect
        }
        Thread.sleep(1000)
      }
    }
  }).start

  override def exitSequence = if(is_connected) disconnect

  def disconnect = {
    if(socket != null) socket.close
    is_connected = false
    log.debug("disconnected from server "+server_url+":"+port)
  }
}