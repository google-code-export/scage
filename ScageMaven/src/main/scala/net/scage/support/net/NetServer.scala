package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import java.net.{SocketException, ServerSocket, Socket}
import net.scage.support.State
import com.weiglewilczek.slf4s.Logger
import concurrent.ops._
import collection.mutable.ArrayBuffer

object NetServer {
  private val log = Logger(this.getClass.getName)

  val port = property("net.port", 9800)
  val max_clients = property("net.max_clients", 20)
  val check_timeout = property("net.check_timeout", 60000)
  val ping_timeout = property("net.ping_timeout", check_timeout*3/4)
  
  private var server_socket:ServerSocket = null

  private var client_handlers = ArrayBuffer[ClientHandler]()
  def clients = client_handlers.toSeq
  def client(num:Int) = client_handlers(num)
  def numClients = client_handlers.size
  def lastConnected = client_handlers.last

  private var has_new_connection = false
  def hasNewConnection = {
    val has_new = has_new_connection
    has_new_connection = false
    has_new
  }

  private var sd:State = State()  // outgoing data
  def send() {client_handlers.foreach(client => client.send(sd))} // data sending methods
  def send(data:State) {
    sd = data
    send()
  }
  def send(data:String) {send(State(("raw" -> data)))}

  def hasOutgoingData = sd.size != 0
  def eraseOutgoingData() {sd.clear()}
  def addOutgoingData(key:Any, data:Any) {sd += (key.toString -> data)}
  def addOutgoingData(key:Any) {sd.add(key)}

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
            client.send(State("quit"))
            client.disconnect()
          }
        } else Thread.sleep(1000)
      }      
    }

    spawn { // connection checker and pinger
      while(is_running) {
        val offline_clients = client_handlers.filter(client => !client.isOnline)
        offline_clients.foreach(client => {
          client.send(State(
            ("disconnect" -> ("no responce from you for "+check_timeout+" msecs"))
          ))
          client.disconnect()
        })
        client_handlers --= offline_clients
        if(ping_timeout > 0) {
          client_handlers.foreach(_.send(State("ping")))
          Thread.sleep(ping_timeout)
        } else Thread.sleep(1000)
      }
    }
  }

  def stopServer() {
    log.info("shutting net server down...")
    if(client_handlers.length > 0) log.info("disconnecting all clients...")
    client_handlers.foreach(client => client.send(State("quit")))
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

  private var cd = State()
  private var has_new_data = false
  def incomingData = {
    has_new_data = false
    cd
  }
  def hasNewIncomingData = has_new_data

  private var write_error = false
  def send(data:State) {
    out.println(data.toJsonString)
    out.flush()
    write_error = out.checkError()
  }
  def send(data:String) {send(State(("raw" -> data)))}

  def disconnect() {
    socket.close()
    log.info("client #"+id+" was disconnected")
  }

  private var last_answer_time = System.currentTimeMillis
  def isOnline = !write_error && (check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout)

  spawn {
    while(isRunning) {
      if(in.ready) {
        last_answer_time = System.currentTimeMillis
        try {
          val message = in.readLine
          val received_data = (try{State.fromJson(message)}
          catch {
            case e:Exception => State(("raw" -> message))
          })
          if(received_data.contains("ping")) log.debug("received ping from client "+id)
          else {
            log.debug("received data from client "+id+":\n"+received_data)
            cd = received_data
            has_new_data = true
          }
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