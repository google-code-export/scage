package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.main.Scage
import org.json.{JSONException, JSONObject}
import su.msk.dunno.scage.prototypes.Handler
import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}
import su.msk.dunno.scage.support.ScageProperties
import java.net.{SocketException, Socket}

object NetClient extends Handler {
  val server_url =  ScageProperties.stringProperty("server", "127.0.0.1")
  val port =  ScageProperties.intProperty("port", 9800)

  private var is_connected = false
  def isConnected = is_connected
  private var socket:Socket = null
  private var out:PrintWriter = null
  private var in:BufferedReader = null
  def connect = {
    log.info("start connecting to server "+server_url+" at port "+port)
    socket = try {new Socket(server_url, port)}
    catch {
      case e:java.io.IOException => {
        log.error("failed to connect to server "+server_url+" at port "+port);
        null
      }
    }
    if(socket != null) {
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
      in = new BufferedReader(new InputStreamReader(socket.getInputStream))
      is_connected = true
      last_answer_time = System.currentTimeMillis
      log.info("connected")
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
  def hasNewIncomingData = has_new_data

  private var cd:JSONObject = new JSONObject
  def outgoingData = cd
  def eraseOutgoingData = cd = new JSONObject
  def addOutgoingData(key:Any, data:Any) = cd.put(key.toString, data)
  def addOutgoingData(key:Any) = cd.put(key.toString, "")  

  private val check_timeout =  ScageProperties.intProperty("check_timeout")
  private var last_answer_time = System.currentTimeMillis
  def isServerOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  override def initSequence = {
    new Thread(new Runnable { // receive data from server
      def run():Unit = {
        while(Scage.isRunning) {
          if(is_connected) {
            if(in.ready) {
              last_answer_time = System.currentTimeMillis
              val message = try{in.readLine}
              catch {
                case e:SocketException => return
              }
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
  }


  override def exitSequence = if(is_connected) disconnect

  def disconnect = {
    if(socket != null) socket.close
    is_connected = false
    log.info("disconnected from server "+server_url+":"+port)
  }
}