package net.scage.support.net

import org.apache.log4j.Logger
import _root_.net.scage.support.ScageProperties._
import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, PrintWriter}
import java.net.{SocketException, Socket}
import org.json.{JSONException, JSONObject}

object NetClient {
  private val log = Logger.getLogger(this.getClass)

  val server_url =  property("server", "127.0.0.1")
  val port = property("port", 9800)

  private var is_connected = false
  def isConnected = is_connected
  private var socket:Socket = null
  private var out:PrintWriter = null
  private var in:BufferedReader = null

  def connect() {
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

  def send() {
    if(is_connected) {
      out.println(cd)
      out.flush()
    }
  }
  def send(data:JSONObject) {
    cd = data
    send()
  }
  def send(data:String) {send(new JSONObject().put("raw", data))}

  private var sd = new JSONObject
  private var has_new_data = false
  def incomingData = {
    has_new_data = false
    sd
  }
  def hasNewIncomingData = has_new_data

  private var cd = new JSONObject
  def outgoingData = cd
  def eraseOutgoingData() {cd = new JSONObject}
  def addOutgoingData(key:Any, data:Any) {cd.put(key.toString, data)}
  def addOutgoingData(key:Any) {cd.put(key.toString, "")}

  private val check_timeout = intProperty("check_timeout")
  private var last_answer_time = System.currentTimeMillis
  def isServerOnline = check_timeout == 0 || System.currentTimeMillis - last_answer_time < check_timeout

  def disconnect() {
    if(socket != null) socket.close()
    is_connected = false
    log.info("disconnected from server "+server_url+":"+port)
  }

  private var is_running = false
  def startClient() {
    is_running = true
    new Thread() {
      override def run() {
        while(is_running) {
          if(!isServerOnline) { // connection checker
            if(is_connected) disconnect()
            connect()
          }
          if(is_connected) {
            if(in.ready) {
              last_answer_time = System.currentTimeMillis
              val message = try{in.readLine}
              catch {
                case e:SocketException => return
              }
              sd = try{new JSONObject(message)}
              catch {
                case e:JSONException => sd.put("raw", message)
              }
              if(sd.length > 0) has_new_data = true
            }
          }
          Thread.sleep(10)
        }
      }
    }.start()
  }
  def stopServer() {
    is_running = false
    disconnect()
  }
}