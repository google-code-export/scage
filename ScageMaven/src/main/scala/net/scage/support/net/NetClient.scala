package net.scage.support.net

import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, PrintWriter}
import java.net.{SocketException, Socket}
import net.scage.support.State
import com.weiglewilczek.slf4s.Logger
import concurrent.ops._

object NetClient {
  private val log = Logger(this.getClass.getName)

  val server_url =  property("net.server", "127.0.0.1")
  val port = property("net.port", 9800)

  private var is_connected = false
  def isConnected = is_connected
  private var socket:Socket = null
  private var out:PrintWriter = null
  private var in:BufferedReader = null

  private def connect() {
    log.info("start connecting to server "+server_url+" at port "+port)
    socket = try {new Socket(server_url, port)}
    catch {
      case e:java.io.IOException => {
        log.error("failed to connect to server "+server_url+" at port "+port);
        null
      }
    }
    if(socket != null) {
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
      in = new BufferedReader(new InputStreamReader(socket.getInputStream))
      is_connected = true
      last_answer_time = System.currentTimeMillis
      log.info("connected")
    }
  }

  private var write_error = false
  def send() {
    if(is_connected) {
      out.println(cd.toJsonString)
      out.flush()
      write_error = out.checkError()
    }
  }
  def send(data:State) {
    cd = data
    send()
  }
  def send(data:String) {send(State(("raw" -> data)))}

  private var sd = State()
  private var has_new_data = false
  def incomingData = {
    has_new_data = false
    sd
  }
  def hasNewIncomingData = has_new_data

  private var cd = State()
  def outgoingData = cd
  def eraseOutgoingData() {cd.clear()}
  def addOutgoingData(key:Any, data:Any) {cd += (key.toString -> data)}
  def addOutgoingData(key:Any) {cd.add(key)}

  val check_timeout = property("net.check_timeout", 60000)
  private var last_answer_time = System.currentTimeMillis
  def isServerOnline = is_connected && !write_error && (check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout)
  
  val ping_timeout = property("net.ping_timeout", check_timeout*3/4)

  def disconnect() {
    if(socket != null) socket.close()
    is_connected = false
    log.info("disconnected from server "+server_url+":"+port)
  }

  private var is_running = false
  def startClient() {
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
              val received_data = (try{State.fromJson(message)}
              catch {
                case e:Exception => State(("raw" -> message))
              })
              if(received_data.contains("ping")) log.debug("received ping from server")
              else {
                log.debug("received data from server:\n"+received_data)
                sd = received_data
                has_new_data = true
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
    }
    
    if(ping_timeout > 0) {
      spawn { // pinger. Thread exists only if ping_timeout set to non-zero value
        while(is_running) {
          send(State("ping"))
          Thread.sleep(ping_timeout)
        }
      }
    }
  }
  def stopClient() {
    is_running = false
    disconnect()
  }
}