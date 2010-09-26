package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.main.Scage
import java.net.ServerSocket
import org.json.JSONObject
import su.msk.dunno.scage.prototypes.Handler
import su.msk.dunno.scage.support.ScageProperties

object NetServer extends Handler {
  val port = ScageProperties.intProperty("port", 9800)
  val max_clients = ScageProperties.intProperty("max_clients", 0)
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

  def send = client_handlers.foreach(client => client.send(sd)) // data sending methods
  def send(data:JSONObject):Unit = {
    sd = data
    send
  }
  def send(data:String):Unit = send(new JSONObject().put("raw", data))

  private var sd:JSONObject = new JSONObject  // outgoing data
  def hasOutgoingData = sd.length != 0
  def eraseOutgoingData = sd = new JSONObject
  def addOutgoingData(key:Any, data:Any) = sd.put(key.toString, data)
  def addOutgoingData(key:Any) = sd.put(key.toString, "")

  private var next_client = 0
  new Thread(new Runnable { // awaiting new connections
    def run():Unit = {
      val server_socket = new ServerSocket(port)
      while(Scage.isRunning) {
        if(max_clients == 0 || client_handlers.length < max_clients) {
          log.debug("listening port "+port+", "+client_handlers.length+"/"+max_clients+" client(s) are connected")
          val socket = server_socket.accept
          client_handlers = new ClientHandler(next_client, socket) :: client_handlers
          log.debug("established connection with "+socket.getInetAddress.getHostAddress)
          has_new_connection = true
          next_client += 1
        }
        else Thread.sleep(1000)
      }
      server_socket.close
    }
  }).start

  override def actionSequence = { // check clients being online
    client_handlers.filter(client => !client.isOnline).foreach(client => client.disconnect)
    client_handlers = client_handlers.filter(client => client.isOnline)
  }

  override def exitSequence = { // sending quit message and disconnecting
    if(client_handlers.length > 0) log.debug("disconnecting all clients...")
    client_handlers.foreach(client => client.send(new JSONObject().put("quit", "")))
    client_handlers.foreach(client => client.disconnect)
  }
}