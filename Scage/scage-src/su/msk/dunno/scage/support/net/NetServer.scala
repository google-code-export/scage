package su.msk.dunno.scage.support.net

import su.msk.dunno.scage.main.Scage
import java.net.{ServerSocket}
import org.json.{JSONObject}
import org.apache.log4j.Logger

object NetServer {
  private val log = Logger.getLogger(this.getClass)

  val port = Scage.getIntProperty("port")
  val max_clients = Scage.getIntProperty("max_clients")
  private var client_handlers:List[ClientHandler] = Nil
  def clients = client_handlers
  def client(num:Int) = client_handlers(num)
  def numClients = client_handlers.length
  def lastConnected = client_handlers.head

  private var has_new_connection = false
  def hasNewConnection = {
    val has_new = has_new_connection
    has_new_connection = false
    has_new
  }

  private var is_sending_data = false
  def send = is_sending_data = true
  def send(data:JSONObject):Unit = {
    while(is_sending_data) Thread.sleep(10)
    sd = data
    send
  }
  def send(data:String):Unit = send(new JSONObject().put("data", data))

  private var sd:JSONObject = new JSONObject
  def serverData:JSONObject = sd
  def eraseServerData = {
    while(is_sending_data) Thread.sleep(10)
    sd = new JSONObject
  }

  private var next_client = 0
  new Thread(new Runnable { // awaiting new connections
    def run():Unit = {
      val server_socket = new ServerSocket(port)
      while(client_handlers.length < max_clients && Scage.isRunning) {
        log.debug("listening at port "+port+", "+client_handlers.length+" client(s) are connected")
        val socket = server_socket.accept
        client_handlers = new ClientHandler(next_client, socket) :: client_handlers
        log.debug("established connection with "+socket.getInetAddress.getHostAddress)
        has_new_connection = true
        next_client += 1
      }
      server_socket.close
    }
  }).start

  new Thread(new Runnable { // send data to clients
    def run():Unit = {
      while(Scage.isRunning) {
        client_handlers.filter(client => !client.isOnline).foreach(client => client.disconnect)
        client_handlers = client_handlers.filter(client => client.isOnline)
        if(is_sending_data) {
          client_handlers.foreach(client => client.send(sd))
          is_sending_data = false
        }
        Thread.sleep(10)
      }
      client_handlers.foreach(client => client.disconnect)
    }
  }).start
}