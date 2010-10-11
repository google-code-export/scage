package su.msk.dunno.scage.handlers.net

import su.msk.dunno.scage.main.Scage
import su.msk.dunno.scage.prototypes.Handler
import su.msk.dunno.scage.support.ScageProperties
import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter, PrintWriter}
import org.apache.log4j.Logger
import java.net.{Socket, SocketException, ServerSocket}
import org.json.{JSONException, JSONObject}

object NetServer extends Handler {
  val port = ScageProperties.intProperty("port", 9800)
  val max_clients = ScageProperties.intProperty("max_clients", 20)
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
          log.info("listening port "+port+", "+client_handlers.length+"/"+max_clients+" client(s) are connected")
          val socket = server_socket.accept
          client_handlers = new ClientHandler(next_client, socket) :: client_handlers
          log.info("established connection with "+socket.getInetAddress.getHostAddress)
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
    if(client_handlers.length > 0) log.info("disconnecting all clients...")
    client_handlers.foreach(client => client.send(new JSONObject().put("quit", "")))
    client_handlers.foreach(client => client.disconnect)
  }
}

class ClientHandler(val id:Int, val socket:Socket) {
  private val log = Logger.getLogger(this.getClass)

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private var cd:JSONObject = new JSONObject
  def incomingData = {
    has_new_data = false
    cd
  }

  private var has_new_data = false
  def hasNewIncomingData = has_new_data

  def send(data:JSONObject) = {
    out.println(data)
    out.flush
  }

  def disconnect = {
    socket.close
    log.debug("client #"+id+" was disconnected")
  }

  private val check_timeout = ScageProperties.intProperty("check_timeout", 0)
  private var last_answer_time = System.currentTimeMillis
  def isOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  new Thread(new Runnable { // receive data from client
    def run():Unit = {
      while(Scage.isRunning) {
        if(in.ready) {
          last_answer_time = System.currentTimeMillis
          val message = try{in.readLine}
          catch {
            case e:SocketException => return
          }
          cd = try{new JSONObject(message)}
          catch {
            case e:JSONException => cd.put("raw", message)
          }
          if(cd.length > 0) has_new_data = true
        }
        Thread.sleep(10)
       }
    }
  }).start
}