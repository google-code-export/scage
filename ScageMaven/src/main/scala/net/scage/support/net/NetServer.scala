package net.scage.support.net

import org.apache.log4j.Logger
import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import java.net.{SocketException, ServerSocket, Socket}
import org.json.{JSONException, JSONObject}

object NetServer {
  private val log = Logger.getLogger(this.getClass)

  val port = property("port", 9800)
  val max_clients = property("max_clients", 20)
  val check_timeout = property("check_timeout", 0)

  private var server_socket:ServerSocket = null

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

  private var sd:JSONObject = new JSONObject  // outgoing data
  def send() {client_handlers.foreach(client => client.send(sd))} // data sending methods
  def send(data:JSONObject) {
    sd = data
    send()
  }
  def send(data:String) {send(new JSONObject().put("raw", data))}

  def hasOutgoingData = sd.length != 0
  def eraseOutgoingData() {sd = new JSONObject}
  def addOutgoingData(key:Any, data:Any) {sd.put(key.toString, data)}
  def addOutgoingData(key:Any) {sd.put(key.toString, "")}

  var serverGreetings:ClientHandler => Unit = (client) => client.send("This is Scage NetServer")
  private var next_client = 0

  private var is_running = false
  def startServer() {
    log.info("starting net server...")
    is_running = true
    new Thread() {  // awaiting new connections
      override def run() {
        server_socket = new ServerSocket(port)
        while(is_running) {
          if(max_clients == 0 || client_handlers.length < max_clients) {
            log.info("listening port "+port+", "+client_handlers.length+"/"+max_clients+" client(s) are connected")
            val socket = server_socket.accept
            val client = new ClientHandler(next_client, socket, is_running)
            client_handlers = client :: client_handlers
            log.info("established connection with "+socket.getInetAddress.getHostAddress)
            serverGreetings(client)
            has_new_connection = true
            next_client += 1
          }
          else Thread.sleep(1000)
        }
      }
    }.start()

    new Thread() {
      override def run() {
        while(is_running) {
          client_handlers.filter(client => !client.isOnline).foreach(client => {
            client.send(new JSONObject().put("quit", "no responce from you for "+check_timeout+" msecs"))
            client.disconnect()
          })
          client_handlers = client_handlers.filter(client => client.isOnline)
          Thread.sleep(1000)
        }
      }
    }.start()
  }

  def stopServer() {
    log.info("shutting net server down...")
    if(client_handlers.length > 0) log.info("disconnecting all clients...")
    client_handlers.foreach(client => client.send(new JSONObject().put("quit", "")))
    client_handlers.foreach(client => client.disconnect())

    is_running = false
    server_socket.close()
  }
}

import NetServer._

class ClientHandler(val id:Int, socket:Socket, isRunning: => Boolean) {
  private val log = Logger.getLogger(this.getClass)

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private var cd = new JSONObject
  private var has_new_data = false
  def incomingData = {
    has_new_data = false
    cd
  }
  def hasNewIncomingData = has_new_data

  def send(data:JSONObject) {
    out.println(data)
    out.flush()
  }
  def send(data:String) {send(new JSONObject().put("raw", data))}

  def disconnect() {
    socket.close()
    log.info("client #"+id+" was disconnected")
  }

  private var last_answer_time = System.currentTimeMillis
  def isOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  new Thread() {
    override def run() {
      while(isRunning) {
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
  }.start()
}