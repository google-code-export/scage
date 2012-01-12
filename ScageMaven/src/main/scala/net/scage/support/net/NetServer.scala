package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import java.net.{SocketException, ServerSocket, Socket}
import com.weiglewilczek.slf4s.Logger
import concurrent.ops._
import actors.Actor._
import collection.mutable.ArrayBuffer
import actors.Actor
import net.scage.support.{ScageId, State}

object NetServer {
  private val log = Logger(this.getClass.getName)

  val port = property("net.port", 9800)
  val max_clients = property("net.max_clients", 20)
  val check_timeout = property("net.check_timeout", 60000)
  val ping_timeout = property("net.ping_timeout", check_timeout*3/4)

  val clients_actor = actor {
    var client_handlers = ArrayBuffer[ClientHandler]()
    loop {
      react {
        case "remove_offline" =>
          val offline_clients = client_handlers.filter(client => !client.isOnline)
          offline_clients.foreach(client => {
            client.send(State(
              ("disconnect" -> ("no responce from you for "+check_timeout+" msecs"))
            ))
            client.disconnect()
          })
          client_handlers --= offline_clients
        case ("sendToAll", data:State) =>
          client_handlers.foreach(_.send(data))
        case ("sendToClient", client_id:Int, data:State) =>
          client_handlers.find(_.id == client_id) match {
            case Some(client) => client.send(data)
            case None =>
          }
        case "ping" =>
          client_handlers.foreach(_.send(State("ping")))
        case ("add", new_client:ClientHandler) =>
          client_handlers += new_client
        case ("length", actor:Actor) =>
          actor ! ("clients_length", client_handlers.length)
        case ("process", process_func:(ClientHandler => Any)) =>
          client_handlers.foreach(process_func(_))
        case "disconnect" =>
          if(client_handlers.length > 0) log.info("disconnecting all clients...")
          client_handlers.foreach(client => client.send(State("disconnect" -> "bye")))
          client_handlers.foreach(client => client.disconnect())
          client_handlers.clear()
      }
    }
  }

  def processClients(process_func:ClientHandler => Any) {
    clients_actor ! ("process", process_func)
  }
  def clientsAmount = {
    clients_actor ! ("length", self)
    receive {
      case clients_length:Int => clients_length
    }
  }
  def isClientsLimitReach:Boolean = max_clients != 0 && clientsAmount >= max_clients
  
  /*private var sd:State = State()  // outgoing data
  def send() {clients_actor ! ("process", (client:ClientHandler) => client.send(sd))} // data sending methods*/
  def sendToAll(data:State) {
    /*sd = data*/
    clients_actor ! ("sendToAll", data)
  }
  def sendToAll(data:String) {sendToAll(State(("raw" -> data)))}
  def sendToClient(client_id:Int, data:State) {
     clients_actor ! ("sendToClient", client_id, data)
  }

  /*def hasOutgoingData = sd.size != 0
  def eraseOutgoingData() {sd.clear()}
  def addOutgoingData(key:Any, data:Any) {sd += (key.toString -> data)}
  def addOutgoingData(key:Any) {sd.add(key)}*/

  private var is_running = false
  def startServer(
    serverGreetings:ClientHandler => (Boolean, String) = client => (true, "")
  ) {
    if(!is_running) log.warn("server is already running!")
    else {
      log.info("starting net server...")
      is_running = true
      spawn {
        var server_socket = new ServerSocket(port)
        while(is_running) {
          log.info("listening port "+port+", "+clientsAmount+(if(max_clients > 0) "/"+max_clients else "unlimited")+" client(s) are connected")
          val socket = server_socket.accept
          log.info("incoming connection from "+socket.getInetAddress.getHostAddress)
          val client = new ClientHandler(socket)
          val (is_client_accepted, reason) = if(isClientsLimitReach) (false, "server is full") else serverGreetings(client)
          if(is_client_accepted) {
            clients_actor ! ("add", client)
            log.info("established connection with "+socket.getInetAddress.getHostAddress)
            client.send(State("accepted"))
          } else {
            log.info("refused connection from "+socket.getInetAddress.getHostAddress+": "+reason)
            client.send(State("refused" -> reason))
            client.disconnect()
          }
          Thread.sleep(1000)
        }
        server_socket.close()
      }

      if(ping_timeout > 0) {
        spawn { // clients connection checker and pinger
          while(is_running) {
            clients_actor ! "remove_offline"
            clients_actor ! "ping"
            Thread.sleep(ping_timeout)
          }
        }        
      } else {
        spawn { // clients connection checker only
          while(is_running) {
            clients_actor ! "remove_offline"
            Thread.sleep(1000)  // maybe increase or make depend on check_timeout
          }
        }        
      }
    }
  }

  def stopServer() {
    log.info("shutting net server down...")
    clients_actor ! "disconnect"

    is_running = false
  }
}

import NetServer._

class ClientHandler(socket:Socket) {
  private val log = Logger(this.getClass.getName)

  val id:Int = ScageId.nextId

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
    if(is_running) {
      out.println(data.toJsonString)
      out.flush()
      write_error = out.checkError()
    } else log.warn("client #"+id+" is disconnected!")
  }
  def send(data:String) {send(State(("raw" -> data)))}

  def disconnect() {
    is_running = false
  }

  private var last_answer_time = System.currentTimeMillis
  def isOnline = is_running && !write_error && (check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout)

  private var is_running = false
  spawn {
    is_running = true
    while(is_running) {
      if(in.ready) {
        last_answer_time = System.currentTimeMillis
        try {
          val message = in.readLine
          val received_data = (try{State.fromJson(message)}
          catch {
            case e:Exception => State(("raw" -> message))
          })
          if(received_data.contains("ping")) log.debug("received ping from client #"+id)
          else {
            log.debug("received data from client #"+id+":\n"+received_data)
            cd = received_data
            has_new_data = true
          }
        } catch {
          case e:SocketException => {
            log.error("error while receiving data from client #"+id+":\n"+e)
            // dsiconnect maybe?
          }
        }
      }
      Thread.sleep(10)
    }
    socket.close()
    log.info("client #"+id+" was disconnected")
  }
}