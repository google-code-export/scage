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

  private var is_send_data = false
  def send = is_send_data = true
  def send(data:String) = {
    sd = data
    is_send_data = true
  }

  private var sd:String = ""
  def serverData:String = sd
  def serverData_= (new_sd:String):Unit = sd = new_sd

  new Thread(new Runnable { // awaiting new connections
    def run():Unit = {
      val server_socket = new ServerSocket(port)
      while(client_handlers.length < max_clients) {
        log.debug("listening at port "+port+", "+client_handlers.length+" client(s) connected")
        val socket = server_socket.accept
        client_handlers = new ClientHandler(socket) :: client_handlers
        log.debug("established connection with "+socket.getInetAddress.getHostAddress)
      }
      server_socket.close
    }
  }).start

  new Thread(new Runnable { // send data to clients
    def run():Unit = {
      while(true) {
        if(is_send_data) {
          clients.foreach(handler => handler.send)
          is_send_data = false
        }
        Thread.sleep(500/Idler.framerate)
      }
    }
  }).start

  class ClientHandler(socket:Socket) {
    val name:String = client_handlers.length+""
    val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
    val in = new Scanner(new InputStreamReader(socket.getInputStream))

    private var cd:String = ""
    def clientData = cd
    def eraseClientData = cd = ""

    def send = {
      out.println(sd)
      out.flush
    }

    new Thread(new Runnable { // receive data from client
      def run():Unit = {
        while(true) {
          if(in.hasNextLine) cd = in.nextLine
          Thread.sleep(500/Idler.framerate)
        }
      }
    }).start
  }
}