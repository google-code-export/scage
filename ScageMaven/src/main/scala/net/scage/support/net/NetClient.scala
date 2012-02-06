package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, PrintWriter}
import java.net.Socket
import net.scage.support.State
import com.weiglewilczek.slf4s.Logger
import actors.Actor._
import actors.Actor

object NetClient extends NetClient

class NetClient {
  private val log = Logger(this.getClass.getName)

  private var is_connected = false
  private var write_error  = false

  private lazy val io_actor = actor {
    var onServerDataReceived:State => Any = state => {}
    var socket:Socket = null
    var out:PrintWriter = null
    var in:BufferedReader = null
    
    def send(data:State) {
      log.debug("sending data to server:\n"+data)
      if(isOnline) {
        out.println(data.toJsonString)
        out.flush()
        write_error = out.checkError()
        if(write_error) log.warn("failed to send data to server: write error!")
      } else log.warn("not connected to send data!")
    }
    
    loop {
      react {
        case ("connect", server_url:String, port:Int, new_onServerDataReceived:(State => Any), actor:Actor)=>
          if(isOnline) {
            is_connected = false
            if(socket != null) {
              val socket_url = socket.getInetAddress.getHostAddress
              socket.close()
              log.info("disconnected from server "+socket_url)
            }
          }
          log.info("start connecting to server "+server_url+" at port "+port)
          try {
            socket = new Socket(server_url, port)
            onServerDataReceived = new_onServerDataReceived
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))
            in = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))
            is_connected = true
            write_error = false
            log.info("connected!")
            actor ! "connected"
          } catch {
            case e:Exception => {
              log.error("failed to connect to server "+server_url+" at port "+port+": "+e);
              actor ! "fail"  // maybe change the message
            }
          }
        case ("send", data:State) => send(data)
        case ("sendSync", data:State, actor:Actor) =>
          send(data)
          actor ! "finished sending"
        case "check" =>
          if(is_connected) {
            if(in.ready) {
              try {
                val message = in.readLine
                log.debug("incoming message from server:\n"+message)
                val received_data = State.fromJsonStringOrDefault(message, State(("raw" -> message)))
                if(received_data.contains("ping")) log.debug("received ping from server")
                else {
                  log.debug("received data from server:\n"+received_data)
                  actor {
                    onServerDataReceived(received_data)
                  }
                }
              } catch {
                case e:Exception => {
                  log.error("error while receiving data from server: "+e)
                  // disconnect maybe?
                }
              }
            }
          } // else maybe?
        case ("disconnect", actor:Actor) =>
          is_connected = false
          if(socket != null) {
            val socket_url = socket.getInetAddress.getHostAddress
            log.info("disconnected from server "+socket_url)
            socket.close()
          }
          actor ! "disconnected"
      }
    }
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

  def isOnline = is_connected && !write_error // maybe rename this and check is_running too

  private var is_running = false
  def startClient(
    server_url:String =  property("net.server", "localhost"),
    port:Int = property("net.port", 9800),
    ping_timeout:Int = property("net.ping_timeout", 60000, {ping_timeout:Int => (ping_timeout >= 1000, "must be more than 1000")}),
    onServerDataReceived:(State) => Any = (state) => {}
  ) {
    is_running = true
    actor {
      var last_ping_moment = System.currentTimeMillis()
      while(is_running) {
        if(!isOnline) { // connection checker
          io_actor ! ("connect", server_url, port, onServerDataReceived, self)
          receive {
            case "fail" =>
              Thread.sleep(1000)
              io_actor ! ("connect", onServerDataReceived, self)
            case _ =>
          }
        } else {
          Thread.sleep(10)
          io_actor ! "check"
          if(System.currentTimeMillis() - last_ping_moment > ping_timeout) {
            io_actor ! ("send", State("ping"))
            last_ping_moment = System.currentTimeMillis()
          }
        }
      }
    }
  }

  def stopClient() {
    is_running = false
    io_actor ! ("disconnect", self)
    receive {
      case "disconnected" =>
    }
  }
}