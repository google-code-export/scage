package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import java.net.{SocketException, ServerSocket, Socket}
import org.json.{JSONException, JSONObject}
import com.weiglewilczek.slf4s.Logger
import concurrent.ops._
import collection.mutable.ArrayBuffer

object NetServer {
  private val log = Logger(this.getClass.getName)

  val port = property("port", 9800)
  val max_clients = property("max_clients", 20)
  val check_timeout = property("check_timeout", 0)

  private var server_socket:ServerSocket = null

  private var client_handlers = ArrayBuffer[ClientHandler]()
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

  var serverGreetings:ClientHandler => (Boolean, String) = (client) => {
    client.send("This is Scage NetServer")
    (true, "")
  }
  private var next_client = 0

  private var is_running = false
  def startServer(new_serverGreetings:ClientHandler => (Boolean, String) = serverGreetings) {
    log.info("starting net server...")
    serverGreetings = new_serverGreetings
    is_running = true
    spawn {
      server_socket = new ServerSocket(port)
      while(is_running) {
        if(max_clients == 0 || client_handlers.length < max_clients) {
          log.info("listening port "+port+", "+client_handlers.length+"/"+max_clients+" client(s) are connected")
          val socket = server_socket.accept
          log.info("incoming connection from "+socket.getInetAddress.getHostAddress)
          val client = new ClientHandler(next_client, socket, is_running)
          val (is_client_accepted, reason) = serverGreetings(client)
          if(is_client_accepted) {
            client_handlers += client
            log.info("established connection with "+socket.getInetAddress.getHostAddress)          
            has_new_connection = true
            next_client += 1
          } else {
            log.info("refused connection from "+socket.getInetAddress.getHostAddress+": "+reason)
            client.send(new JSONObject().put("quit", ""))
            client.disconnect()
          }
        } else Thread.sleep(1000)
      }      
    }

    if(check_timeout > 0) {
      spawn {
        while(is_running) {
          val oofline_clients = client_handlers.filter(client => !client.isOnline)
          oofline_clients.foreach(client => {
            client.send(new JSONObject().put("quit", "no responce from you for "+check_timeout+" msecs"))
            client.disconnect()
          })
          client_handlers --= oofline_clients
          Thread.sleep(1000)
        }
      }
    }
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
  private val log = Logger(this.getClass.getName)

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

  spawn {
    while(isRunning) {
      if(in.ready) {
        last_answer_time = System.currentTimeMillis
        try {
          val message = in.readLine
          cd = try{new JSONObject(message)}
          catch {
            case e:JSONException => cd.put("raw", message)
          }
          if(cd.length > 0) has_new_data = true
        } catch {
          case e:SocketException => {
            log.error("error while receiving data from client "+id+": "+e)
            // dsiconnect maybe?
          }
        }
      }
      Thread.sleep(10)
    }
  }
}