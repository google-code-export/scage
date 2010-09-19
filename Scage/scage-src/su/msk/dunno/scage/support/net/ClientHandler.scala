package su.msk.dunno.scage.support.net

import java.net.Socket
import java.util.Scanner
import org.json.{JSONException, JSONObject}
import java.io.{InputStreamReader, OutputStreamWriter, PrintWriter}

class ClientHandler(val name:Int, socket:Socket) {
  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
  private val in = new Scanner(new InputStreamReader(socket.getInputStream))

  private var cd:JSONObject = new JSONObject
  def clientData = cd
  def eraseClientData = cd = new JSONObject

  def send(data:JSONObject) = {
    out.println(data)
    out.flush
  }

  new Thread(new Runnable { // receive data from client
    def run():Unit = {
      while(true) {
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