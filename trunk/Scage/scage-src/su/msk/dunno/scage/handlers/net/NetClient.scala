package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Scage
import java.net.Socket
import java.util.Scanner
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}
import org.json.{JSONException, JSONObject}

object NetClient extends THandler {
  val server = Scage.getProperty("server")
  val port = Scage.getIntProperty("port")

  var is_connected = false
  log.debug("start connecting to server "+server+" at port "+port)
  val socket:Socket = try {
    is_connected = true
    new Socket(server, port)    
  }
  catch {
    case e:java.io.IOException => {log.debug("failed to connect to server "+server+" at port "+port); null}
  }
  val out:PrintWriter = if(is_connected) new PrintWriter(new OutputStreamWriter(socket.getOutputStream)) else null
  val in :Scanner= if(is_connected) new Scanner(new InputStreamReader(socket.getInputStream)) else null
  log.debug("connected")

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = sd

  private var cd:JSONObject = new JSONObject
  def clientData = cd
  def clientData_= (new_cd:JSONObject):Unit = cd = new_cd

  override def actionSequence() = {
    if(is_connected) {
      if(cd.length != 0) {
        out.println(cd)
        out.flush
        cd = new JSONObject
      }
      if(in.hasNextLine) {
        val message = in.nextLine
        sd = try{new JSONObject(message)}
        catch {
          case e:JSONException => sd.put("raw", message)
        }
      }
    }
  }
}