package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.main.Scage
import java.net.Socket
import java.util.Scanner
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}
import org.json.{JSONException, JSONObject}
import su.msk.dunno.scage.prototypes.THandler

object NetClient extends THandler {  
  val server_url = Scage.getProperty("server")
  val port = Scage.getIntProperty("port")

  private var is_connected = false
  def isConnected = is_connected
  log.debug("start connecting to server "+server_url+" at port "+port)
  val socket:Socket = try {new Socket(server_url, port)}
  catch {
    case e:java.io.IOException => {
      log.debug("failed to connect to server "+server_url+" at port "+port);
      null
    }
  }
  if(socket != null) {
    is_connected = true
    log.debug("connected")
  }
  val out:PrintWriter = if(is_connected) new PrintWriter(new OutputStreamWriter(socket.getOutputStream)) else null
  val in:Scanner = if(is_connected) new Scanner(new InputStreamReader(socket.getInputStream)) else null

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
  def send(data:String):Unit = {
    val json = new JSONObject
    json.put("raw", data)
    send(json)
  }

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = {
    has_new_data = false
    sd
  }

  private var has_new_data = false
  def hasNewData = has_new_data

  private var cd:JSONObject = new JSONObject
  def clientData = cd
  def eraseClientData = cd = new JSONObject
  def addData(key:Any, data:Any) = cd.put(key.toString, data)
  def addData(key:Any) = cd.put(key.toString, "")

  if(is_connected) {
    new Thread(new Runnable { // receive data from server
      def run():Unit = {
        while(Scage.isRunning) {
          if(in.hasNextLine) {
            val message = in.nextLine
            sd = try{new JSONObject(message)}
            catch {
              case e:JSONException => sd.put("raw", message)
            }
            if(sd.length > 0) has_new_data = true
          }
          Thread.sleep(10)
        }
      }
    }).start
  }

  override def exitSequence = disconnect

  def disconnect = {
    socket.close
    is_connected = false
    log.debug("disconnected from server "+server_url+":"+port)
  }
}