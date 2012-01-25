package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, PrintWriter}
import java.net.{SocketException, Socket}
import net.scage.support.State
import com.weiglewilczek.slf4s.Logger
import concurrent.ops._
import actors.Actor._
import net.scage.Scage

object NetClient extends NetClient(
  server_url =  property("net.server", "localhost"),
  port = property("net.port", 9800),
  check_timeout = property("net.check_timeout", 60000),
  ping_timeout = property("net.ping_timeout", property("net.check_timeout", 60000)*3/4)
)

class NetClient(
  val server_url:String =  property("net.server", "127.0.0.1"),
  val port:Int = property("net.port", 9800),
  val check_timeout:Int = property("net.check_timeout", 60000),
  val ping_timeout:Int = property("net.ping_timeout", property("net.check_timeout", 60000)*3/4)
) {
  private val log = Logger(this.getClass.getName)

  private var is_connected = false
  def isConnected = is_connected
  private var socket:Socket = null
  private var out:PrintWriter = null
  private var in:BufferedReader = null

  private def connect() {
    connection_actor ! "connect"
  }

  private var write_error = false

  private val connection_actor = actor {
    loopWhile(Scage.isAppRunning) {
      react {
        case "connect" =>
          log.info("start connecting to server "+server_url+" at port "+port)
          socket = try {new Socket(server_url, port)}
          catch {
            case e:java.io.IOException => {
              log.error("failed to connect to server "+server_url+" at port "+port);
              null
            }
          }
          if(socket != null) {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))
            in = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))
            is_connected = true
            last_answer_time = System.currentTimeMillis
            log.info("connected!")
          }
        case ("send", data:State) =>
          if(is_connected) {
            log.debug("sending data to server:\n"+data)
            out.println(data.toJsonString)
            out.flush()
            write_error = out.checkError()
            if(write_error) log.warn("failed to send data to server: write error!")
          } else log.warn("not connected to send data!")
        case "disconnect" =>
          if(socket != null) socket.close()
          is_connected = false
          log.info("disconnected from server "+server_url+":"+port)
      }
    }
  }

  def send(data:State) {
    connection_actor ! ("send", data)
  }

  def send(data:String) {send(State(("raw" -> data)))}

  private var last_answer_time = System.currentTimeMillis
  def isServerOnline = is_connected && !write_error && (check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout)

  def disconnect() {
    connection_actor ! "disconnect"
  }

  private var is_running = false
  def startClient(onServerDataReceived:(State) => Any = (state) => {}) {
    connect()
    is_running = true
    spawn {
      while(is_running) {
        if(!isServerOnline) { // connection checker
          if(is_connected) disconnect()
          connect()
        }
        if(is_connected) {
          if(in.ready) {
            last_answer_time = System.currentTimeMillis
            try {
              val message = in.readLine
              log.debug("incoming message from server:\n"+message)
              val received_data = State.fromJsonStringOrDefault(message, State(("raw" -> message)))
              if(received_data.contains("ping")) log.debug("received ping from server")
              else {
                log.debug("received data from server:\n"+received_data)
                onServerDataReceived(received_data)
              }
            } catch {
              case e:SocketException => {
                log.error("error while receiving data from server: "+e)
                // disconnect maybe?
              }
            }
          }
          Thread.sleep(10)
        } else Thread.sleep(1000)
      }
      disconnect()
    }
    
    if(ping_timeout > 0) {
      spawn { // pinger. Thread exists only if ping_timeout set to non-zero value
        while(is_running) {
          Thread.sleep(ping_timeout)
          send(State("ping"))
        }
      }
    }
  }
  def stopClient() {
    is_running = false
  }
  
  println(this.getClass)
}