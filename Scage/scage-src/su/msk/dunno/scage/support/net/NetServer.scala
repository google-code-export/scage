package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.prototypes.THandler
import su.msk.dunno.scage.main.Scage
import java.net.{Socket, ServerSocket}
import java.io._
import java.util.Scanner
import org.json.{JSONException, JSONObject}
import org.apache.log4j.Logger
import su.msk.dunno.scage.handlers.Idler

object NetServer {
  private val log = Logger.getLogger(this.getClass)

  val port = Scage.getIntProperty("port")
  val max_clients = Scage.getIntProperty("max_clients")
  private var client_handlers:List[ClientHandler] = Nil
  def clients = client_handlers

  private var is_sending_data = false
  def send = is_sending_data = true
  def send(data:JSONObject):Unit = {
    while(is_sending_data) Thread.sleep(10)
    sd = data
    is_sending_data = true
  }
  def send(data:String):Unit = {
    val json = new JSONObject
    json.put("data", data)
    send(json)
  }

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = sd
  def eraseServerData = {
    while(is_sending_data) Thread.sleep(10)
    sd = new JSONObject
  }

  new Thread(new Runnable { // awaiting new connections
    def run():Unit = {
      val server_socket = new ServerSocket(port)
      while(client_handlers.length < max_clients) {
        log.debug("listening at port "+port+", "+client_handlers.length+" client(s) connected")
        val socket = server_socket.accept
        client_handlers = client_handlers ::: List(new ClientHandler(socket))
        log.debug("established connection with "+socket.getInetAddress.getHostAddress)
      }
      server_socket.close
    }
  }).start

  new Thread(new Runnable { // send data to clients
    def run():Unit = {
      while(true) {
        if(is_sending_data) {
          clients.foreach(handler => handler.send)
          is_sending_data = false
        }
        Thread.sleep(10)
      }
    }
  }).start

  class ClientHandler(socket:Socket) {
    val name:String = client_handlers.length+""
    val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
    val in = new Scanner(new InputStreamReader(socket.getInputStream))

    private var cd:JSONObject = new JSONObject
    def clientData = cd
    def eraseClientData = cd = new JSONObject

    def send = {
      out.println(sd)
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
}