package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import com.weiglewilczek.slf4s.Logger
import actors.Actor._
import collection.mutable.ArrayBuffer
import actors.Actor
import net.scage.support.{ScageId, State}
import java.net.{DatagramSocket, ServerSocket, Socket}

/**
 * This is a naive but very simple and reliable network server implementation, using text json messages.
 * It starting one thread for new connections listening and two threads for every new connection (one for sending and one for receiveing)
 * It should be replaced with Netty Server in future releases
 */
object NetServer extends NetServer

class NetServer {
  private val log = Logger(this.getClass.getName)

  /**
   * returns next available port starting from given number
   */
  def nextAvailablePort(port:Int):Int = { // TODO: return Option[Int]: None if no available port found within some range
    def available(port:Int):Boolean = {
      var ss:ServerSocket = null
      var ds:DatagramSocket = null
      try {
        ss = new ServerSocket(port)
        ss.setReuseAddress(true)
        ds = new DatagramSocket(port)
        ds.setReuseAddress(true)
        return true
      } catch {
        case e:Exception => return false
      } finally {
        if(ds != null) ds.close()
        if(ss != null) ss.close()
      }
      false
    }
    log.info("trying port "+port+"...")
    if(available(port)) {
      log.info("the port is available!")
      port
    } else {
      log.info("the port is busy")
      nextAvailablePort(port+1)
    }
  }

  private lazy val clients_actor = actor {
    var client_handlers = ArrayBuffer[ClientHandler]()
    loop {
      react {
        case "remove_offline" =>
          val offline_clients = client_handlers.filter(client => !client.isOnline)
          offline_clients.foreach(client => client.disconnect())
          client_handlers --= offline_clients
        case ("send_to_all", data:State) =>
          if(is_running) client_handlers.foreach(_.send(data))
          else log.warn("cannot send data: server is not running!")
        case ("send_to_all_sync", data:State, actor:Actor) =>
          if(is_running) client_handlers.foreach(_.sendSync(data))
          else log.warn("cannot send data: server is not running!")
          actor ! "finished sending"
        case ("send_to_clients", data:State, client_ids:List[Int]) =>
          if(is_running) {
            for(client_id <- client_ids) {
              client_handlers.find(_.id == client_id) match {
                case Some(client) => client.send(data)
                case None =>
              }
            }
          } else log.warn("cannot send data: server is not running!")
        case ("send_to_clients_sync", data:State, client_ids:List[Int], actor:Actor) =>
          if(is_running) {
            for(client_id <- client_ids) {
              client_handlers.find(_.id == client_id) match {
                case Some(client) => client.send(data)
                case None =>
              }
            }
          } else log.warn("cannot send data: server is not running!")
          actor ! "finished sending"
        case "ping" =>
          client_handlers.foreach(_.send(State("ping")))
        case ("add", new_client:ClientHandler) =>
          client_handlers += new_client
        case ("length", actor:Actor) =>
          actor ! client_handlers.length
        case "check" =>
          client_handlers.foreach(client => client.check())
        case ("disconnect", actor:Actor) =>
          if(client_handlers.length > 0) log.info("disconnecting all clients...")
          client_handlers.foreach(client => client.sendSync(State("disconnect" -> "bye")))  // TODO: change message, make it optional or remove!
          client_handlers.foreach(client => client.disconnect())
          client_handlers.clear()
          if(server_socket != null) server_socket.close()  // null check will save our ass if somebody call stopServer() BEFORE startServer()
          actor ! "disconnected"
      }
    }
  }

  def sendToAll(data:State) {
    clients_actor ! ("send_to_all", data)
  }
  def sendToAll(data:String) {sendToAll(State("raw" -> data))}

  def sendToAllSync(data:State) {
    clients_actor ! ("send_to_all_sync", data, self)
    receive {
      case "finished sending" =>
    }
  }
  def sendToAllSync(data:String) {sendToAllSync(State("raw" -> data))}

  def sendToClients(data:State, client_ids:Int*) {
     clients_actor ! ("send_to_clients", data, client_ids.toList)
  }
  def sendToClients(data:String, client_ids:Int*) {sendToClients(State("raw" -> data), client_ids:_*)}

  def sendToClientsSync(data:State, client_ids:Int*) {
     clients_actor ! ("send_to_clients_sync", data, client_ids.toList, self)
    receive {
      case "finished sending" =>
    }
  }
  def sendToClientsSync(data:String, client_ids:Int*) {sendToClientsSync(State("raw" -> data), client_ids:_*)}

  private var connection_port:Int = 0
  def connectionPort:Int = connection_port
  private var server_socket:ServerSocket = null

  private var is_running = false
  def isRunning = is_running  // maybe rename this

  def startServer(
    port:Int              = property("net.port", 9800),
    max_clients:Int   = property("net.max_clients", 20),
    ping_timeout:Int  = property("net.ping_timeout", 60000, {ping_timeout:Int => (ping_timeout >= 1000, "must be more 1000")}),
    onNewConnection:ClientHandler => (Boolean, String) = client => (true, ""),
    onClientAccepted:ClientHandler => Any = client => {},
    onClientDataReceived:(ClientHandler, State) => Any = (client:ClientHandler, data:State) => {},
    onClientDisconnected:ClientHandler => Any = client => {}
  ) {
    if(is_running) log.warn("server is already running!")
    else {
      log.info("starting net server...")
      actor {
        try {
          connection_port = nextAvailablePort(port)
          server_socket = new ServerSocket(connection_port)  // TODO: handle errors during startup (for example, port is busy)        
          while(true) {
            val clients_length = {
              clients_actor ! ("length", self)
              receive {case len:Int => len}
            }
            log.info("listening port "+connection_port+", "+clients_length+(if(max_clients > 0) "/"+max_clients else "unlimited")+" client(s) are connected")
            val socket = server_socket.accept
            log.info("incoming connection from "+socket.getInetAddress.getHostAddress)
            val client = new ClientHandler(socket, onClientDataReceived, onClientDisconnected)
            val (is_client_accepted, reason) = if(max_clients != 0 && clients_length >= max_clients) (false, "server is full")
                                               else onNewConnection(client)
            if(is_client_accepted) {
              clients_actor ! ("add", client)
              log.info("established connection with "+socket.getInetAddress.getHostAddress)
              client.send(State(("accepted" -> reason)))
              onClientAccepted(client)
            } else {
              log.info("refused connection from "+socket.getInetAddress.getHostAddress+": "+reason)
              client.send(State("refused" -> reason))
              client.disconnect()
            }
            Thread.sleep(10)
          }
        } catch {
          case e:Exception => // assume server socket was closed
            log.debug("stop listening for incoming connections: "+e)
        }
      }
      
      actor {
        var last_ping_moment = System.currentTimeMillis()
        is_running = true
        while(is_running) {
          Thread.sleep(10)  // TODO: make it an option
          clients_actor ! "check"
          if(System.currentTimeMillis() - last_ping_moment > ping_timeout) {
            clients_actor ! "remove_offline"
            clients_actor ! "ping"
            last_ping_moment = System.currentTimeMillis()
          }
        }
      }
    }
  }

  def stopServer() {
    log.info("shutting net server down...")
    is_running = false
    clients_actor ! ("disconnect", self)
    receive {
      case "disconnected" =>
    }
  }
}

class ClientHandler(socket:Socket,
                    onClientDataReceived:(ClientHandler, State) => Any = (client:ClientHandler, data:State) => {},
                    onClientDisconnected:ClientHandler => Any = client => {}) {
  private val log = Logger(this.getClass.getName)

  val id:Int = ScageId.nextId

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))

  private var write_error = false
  private var is_running = true
  def isOnline = is_running && !write_error

  private val io_actor = actor {
    def send(data:State) {
      log.debug("sending data to client #"+id+":\n"+data)
      if(isOnline) {
        out.println(data.toJsonString)
        out.flush()
        write_error = out.checkError()
        if(write_error) log.warn("failed to send data to client #"+id+": write error!")
      } else log.warn("can't send data to client #"+id+": client handler is offline!")      
    }
    
    loopWhile(is_running) {
      react {
        case ("send", data:State) => send(data)
        case ("sendSync", data:State, actor:Actor) =>
          send(data)
          actor ! "finished sending"
        case "check" =>
          if(is_running) {
            if(in.ready) {
              try {
                val message = in.readLine
                log.debug("incoming message from client #"+id+":\n"+message)
                val received_data = State.fromJsonStringOrDefault(message, State(("raw" -> message)))
                if(received_data.contains("ping")) log.debug("received ping from client #"+id)
                else {
                  log.debug("received data from client #"+id+":\n"+received_data)
                  actor {
                    onClientDataReceived(this, received_data)
                  }
                }
              } catch {
                case e:Exception => {
                  log.error("error while receiving data from client #"+id+":\n"+e)
                  // disconnect maybe?
                }
              }
            }  
          } // else maybe?
        case ("disconnect", actor:Actor) =>
          is_running = false
          onClientDisconnected(this)
          socket.close()
          actor ! "disconnected"
      }
    }
  }
  
  def check() {
    io_actor ! "check"
  }

  def send(data:State) {
    io_actor ! ("send", data)
  }
  def send(data:String) {send(State(("raw" -> data)))}

  def sendSync(data:State) {
    io_actor ! ("sendSync", data, self)
    receive {
      case "finished sending" =>
    }
  }
  def sendSync(data:String) {sendSync(State(("raw" -> data)))}

  def disconnect() {
    io_actor ! ("disconnect", self)
    receive {
      case "disconnected" => log.info("disconnected client #"+id)
    }
  }
}