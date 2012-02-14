package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter, PrintWriter}
import com.weiglewilczek.slf4s.Logger
import actors.Actor._
import collection.mutable.ArrayBuffer
import actors.Actor
import net.scage.support.{ScageId, State}
import java.net.{DatagramSocket, ServerSocket, Socket}
import concurrent.ops._

/**
 * Network server implementation, using text json messages.
 * It starting one thread for new connections listening, one actor for connection listening and one actor for every new connection
 */
object NetServer extends NetServer

class NetServer {
  private val log = Logger(this.getClass.getName)

  private var connection_port:Int = 0
  def connectionPort:Int = connection_port

  private var ping_timeout:Int = 60000
  def pingTimeout = ping_timeout

  private var max_clients = 0
  def maxClients = max_clients

  private var server_socket:ServerSocket = null

  private var is_running = false
  def isRunning = is_running

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

  private lazy val clients_actor:Actor = actor {
    var client_handlers = ArrayBuffer[ClientHandler]()
    loop {
      react {
        case ("add", new_client:ClientHandler) =>
          val is_first_client = client_handlers.isEmpty
          client_handlers += new_client
          if(is_first_client) {
            actor {Thread.sleep(10); clients_actor ! "check"}
            actor {Thread.sleep(ping_timeout); clients_actor ! "ping"}
          }
        case ("send_to_all", data:State) =>
          client_handlers.foreach(_.send(data))
        case ("send_to_all_sync", data:State) =>
          client_handlers.foreach(_.sendSync(data))
          reply("finished sending")
        case ("send_to_clients", data:State, client_ids:List[Int]) =>
          client_handlers.filter(c => client_ids.contains(c.id)).foreach(c => c.send(data))
        case ("send_to_clients_sync", data:State, client_ids:List[Int]) =>
          client_handlers.filter(c => client_ids.contains(c.id)).foreach(c => c.sendSync(data))
          reply("finished sending")
        case "ping" =>
          if(!client_handlers.isEmpty) {
            client_handlers.foreach(_.send(State("ping")))
            val offline_clients = client_handlers.filter(client => !client.isOnline)
            offline_clients.foreach(client => client.disconnect())
            client_handlers --= offline_clients
            actor {Thread.sleep(ping_timeout); clients_actor ! "ping"}
          }
        case "length" =>
          reply(client_handlers.length)
        case "check" =>
          if(!client_handlers.isEmpty) {
            client_handlers.foreach(client => client.check())
            actor {Thread.sleep(10); clients_actor ! "check"}
          }
        case "disconnect" =>
          log.info("shutting net server down...")
          if(!client_handlers.isEmpty) log.info("disconnecting all clients...")
          client_handlers.foreach(client => client.sendSync(State("disconnect" -> "bye")))  // TODO: change message, make it optional or remove!
          client_handlers.foreach(client => client.disconnect())
          client_handlers.clear()
          reply("disconnected")
        case other => log.warn("NetServer clients_actor received unexpected message: "+other)
      }
    }
  }

  def sendToAll(data:State) {
    clients_actor ! ("send_to_all", data)
  }
  def sendToAll(data:String) {sendToAll(State("raw" -> data))}

  def sendToAllSync(data:State) {
    clients_actor !? (("send_to_all_sync", data))
  }
  def sendToAllSync(data:String) {sendToAllSync(State("raw" -> data))}

  def sendToClients(data:State, client_ids:Int*) {
     clients_actor ! ("send_to_clients", data, client_ids.toList)
  }
  def sendToClients(data:String, client_ids:Int*) {sendToClients(State("raw" -> data), client_ids:_*)}

  def sendToClientsSync(data:State, client_ids:Int*) {
     clients_actor !? (("send_to_clients_sync", data, client_ids.toList))
  }
  def sendToClientsSync(data:String, client_ids:Int*) {sendToClientsSync(State("raw" -> data), client_ids:_*)}

  def startServer(
    port:Int              = property("net.port", 9800),
    new_max_clients:Int   = property("net.max_clients", 20),
    new_ping_timeout:Int  = property("net.ping_timeout", 60000, {ping_timeout:Int => (ping_timeout >= 1000, "must be more 1000")}),
    onNewConnection:ClientHandler => (Boolean, String) = client => (true, ""),
    onClientAccepted:ClientHandler => Any = client => {},
    onClientDataReceived:(ClientHandler, State) => Any = (client:ClientHandler, data:State) => {},
    onClientDisconnected:ClientHandler => Any = client => {}
  ) {
    synchronized {
      if(is_running) log.warn("server is already running!")
      else {
        log.info("starting net server...")
        spawn {
          try {
            connection_port = nextAvailablePort(port)
            max_clients = new_max_clients
            ping_timeout = new_ping_timeout
            server_socket = new ServerSocket(connection_port)  // TODO: handle errors during startup (for example, port is busy)
            is_running = true
            while(true) {
              val clients_length = clients_actor !? "length" match {
                case len:Int => len
                case _ => 0
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
              is_running = false
              log.debug("stop listening for incoming connections: "+e)
          }
        }
      }
    }
  }

  def stopServer() {
    synchronized {
      clients_actor !? "disconnect"
      if(server_socket != null) server_socket.close()  // null check will save our ass if somebody call stopServer() BEFORE startServer()
    }
  }
}

private[net] class ClientHandler(socket:Socket,
                    onClientDataReceived:(ClientHandler, State) => Any = (client:ClientHandler, data:State) => {},
                    onClientDisconnected:ClientHandler => Any = client => {}) {
  private val log = Logger(this.getClass.getName)

  val id:Int = ScageId.nextId

  private val out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))
  private val in = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))

  private var write_error = false
  def isOnline = !write_error

  private val io_actor = actor {
    def performSend(data:State) {
      log.debug("sending data to client #"+id+":\n"+data)
      out.println(data.toJsonString)
      out.flush()
      write_error = out.checkError()
      if(write_error) log.warn("failed to send data to client #"+id+": write error!")
    }
    
    loop {
      react {
        case ("send", data:State) => performSend(data)
        case ("sendSync", data:State) =>
          performSend(data)
          reply("finished sending")
        case "check" =>
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
        case "disconnect" =>
          onClientDisconnected(this)
          socket.close()
          log.info("disconnected client #"+id)
          reply("disconnected")
          exit()
        case other => log.warn("ClientHandler io_actor received unexpected message: "+other)
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
    io_actor !? (("sendSync", data))
  }
  def sendSync(data:String) {sendSync(State(("raw" -> data)))}

  def disconnect() {
    io_actor !? "disconnect"
  }
}