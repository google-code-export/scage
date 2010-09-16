package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Scage
import java.net.{Socket, ServerSocket}
import java.io._
import java.util.Scanner
import org.json.{JSONException, JSONObject}

object NetServer extends THandler {
  val port = Scage.getIntProperty("port")

  val server_socket = new ServerSocket(port)
  var socket:Socket = null
  var in:Scanner = null
  var out:PrintWriter = null
  var is_connected = false
  override def initSequence():Unit = {
    new Thread(new Runnable{
      def run():Unit = {
        log.debug("start listening at port "+port)
        socket = server_socket.accept
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
        in = new Scanner(new InputStreamReader(socket.getInputStream))


        is_connected = true
        log.debug("connected")
      }
    }).start
  }

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = sd
  def serverData_= (new_sd:JSONObject):Unit = sd = new_sd

  private var cd:JSONObject = new JSONObject
  def clientData = cd

  override def actionSequence() = {
    if(is_connected) {
      if(sd.length != 0) {
        out.println(sd)
        out.flush
        sd = new JSONObject
      }
      if(in.hasNextLine) {
        val message = in.nextLine
        cd = try{new JSONObject(message)}
        catch {
          case e:JSONException => cd.put("raw", message)
        }
      }
    }
  }
}