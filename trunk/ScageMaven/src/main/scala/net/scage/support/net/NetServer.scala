package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import com.weiglewilczek.slf4s.Logger
import actors.Actor._
import collection.mutable.ArrayBuffer
import actors.Actor
import net.scage.support.{ScageId, State}
import net.scage.Scage
import java.net.{DatagramSocket, ServerSocket, SocketException, Socket}

/**
 * This is a naive but very simple and reliable network server implementation, using text json messages.
 * It starting one thread for new connections listening and two threads for every new connection (one for sending and one for receiveing)
 * It should be replaced with Netty Server in future releases
 */
object NetServer extends NetServer(
  port          = property("net.port", 9800),
  max_clients   = property("net.max_clients", 20),
  ping_timeout  = property("net.ping_timeout", 60000, {ping_timeout:Int => (ping_timeout >= 1000, "must be more than 1000")})
) {
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
}

class NetServer(val port:Int          = property("net.port", 9800),
                val max_clients:Int   = property("net.max_clients", 20),
                val ping_timeout:Int  = property("net.ping_timeout", 60000, {ping_timeout:Int => (ping_timeout >= 1000, "must be more 1000")})) {
  private val log = Logger(this.getClass.getName)

  private val clients_actor = actor {
    var client_handlers = ArrayBuffer[ClientHandler]()
    loopWhile(Scage.isAppRunning) {
      react {
        case "remove_offline" =>
          val offline_clients = client_handlers.filter(client => !client.isOnline)
          offline_clients.foreach(client => client.disconnect())
          client_handlers --= offline_clients
        case ("send_to_all", data:State) =>
          client_handlers.foreach(_.send(data))
        case ("send_to_clients", data:State, client_ids:List[Int]) =>
          for(client_id <- client_ids) {
            client_handlers.find(_.id == client_id) match {
              case Some(client) => client.send(data)
              case None =>
            }
          }
        case "ping" =>
          client_handlers.foreach(_.send(State("ping")))
        case ("add", new_client:ClientHandler) =>
          client_handlers += new_client
        case ("length", actor:Actor) =>
          actor ! client_handlers.length
        case "receive" =>
          client_handlers.foreach(client => client.receive())
        case "disconnect" =>
          if(client_handlers.length > 0) log.info("disconnecting all clients...")
          client_handlers.foreach(client => client.send(State("disconnect" -> "bye")))  // TODO: change message
          client_handlers.foreach(client => client.disconnect())
          client_handlers.clear()
      }
    }
  }

  def sendToAll(data:State) {
    clients_actor ! ("send_to_all", data)
  }
  def sendToAll(data:String) {sendToAll(State("raw" -> data))}
  def sendToClients(data:State, client_ids:Int*) {
     clients_actor ! ("send_to_clients", data, client_ids.toList)
  }

  private var is_running = false
  def startServer(
    onNewConnection:ClientHandler => (Boolean, String) = client => (true, ""),
    onClientAccepted:ClientHandler => Any = client => {},
    onClientDataReceived:(ClientHandler, State) => Any = (client:ClientHandler, data:State) => {},
    onClientDisconnected:ClientHandler => Any = client => {}
  ) {
    if(is_running) log.warn("server is already running!")
    else {
      log.info("starting net server...")
      is_running = true
      actor {
        val available_port = NetServer.nextAvailablePort(port)
        val server_socket = new ServerSocket(available_port)  // TODO: handle errors during startup (for example, port is busy)
        while(is_running) {
          val clients_length = {
            clients_actor ! ("length", self)
            receive {case len:Int => len}
          }
          log.info("listening port "+available_port+", "+clients_length+(if(max_clients > 0) "/"+max_clients else "unlimited")+" client(s) are connected")
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
        server_socket.close()
      }
      
      actor {
        var last_ping_moment = System.currentTimeMillis()
        while(is_running) {
          Thread.sleep(10)  // TODO: make it an option
          clients_actor ! "receive"
          if(System.currentTimeMillis() - last_ping_moment > ping_timeout) {
            clients_actor ! "remove_offline"
            clients_actor ! "ping"
            last_ping_moment = System.currentTimeMillis()
          }
        }
        clients_actor ! "disconnect"
      }
    }
  }

  def stopServer() {
    log.info("shutting net server down...")
    is_running = false
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
    loopWhile(is_running) {
      react {
        case ("send", data:State) =>
          if(is_running) {
            log.debug("sending data to client #"+id+":\n"+data)
            out.println(data.toJsonString)
            out.flush()
            write_error = out.checkError()
            if(write_error) log.warn("failed to send data to client #"+id+": write error!")
          } else log.warn("can't send data to client #"+id+": client handler is offline!")
        case "receive" =>
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
                case e:SocketException => {
                  log.error("error while receiving data from client #"+id+":\n"+e)
                  // disconnect maybe?
                }
              }
            }  
          }
        case "disconnect" =>
          is_running = false
          onClientDisconnected(this)
          socket.close()
          log.info("disconnected client #"+id)
      }
    }
  }
  
  def receive() {
    io_actor ! "receive"
  }

  def send(data:State) {
    io_actor ! ("send", data)
  }
  def send(data:String) {send(State(("raw" -> data)))}

  def disconnect() {
    io_actor ! "disconnect"
  }
}