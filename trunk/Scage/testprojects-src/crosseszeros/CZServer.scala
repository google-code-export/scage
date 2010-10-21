package crosseszeros

import su.msk.dunno.scage.support.ScageLibrary._
import su.msk.dunno.scage.handlers.AI
import java.lang.String
import su.msk.dunno.scage.handlers.net.{ClientHandler, NetServer}

object CZServer {
  properties = "cz-properties.txt"

  NetServer.greetings = (client) => {
    if(NetServer.clients.length < 2) {
      client.send("CrossesZeros v0.1")
      client.send("waiting another player...")
    }
    else {
      client.send("CrossesZeros v0.1")
      NetServer.send("starting game...")
      NetServer.clients.foreach(client => showBoard(client))
    }
  }

  val size_x = property("size_x", 5)
  val size_y = property("size_y", 5)
  val line_to_win = property("line_to_win", 5)
  val board = Array.ofDim[Int](size_x, size_y)

  def onBoard(x:Int, y:Int) = x >= 0 && x < size_x && y >= 0 && y < size_y
  def isFreePoint(x:Int, y:Int) = board(x)(y) == 0
  def coord(str:String):(Int, Int) = {
    try {
      val l = str.split(" ").toList.map(number => number.toInt)
      (l(0), l(1))
    }
    catch {
      case e:Exception => (-1, -1)
    }
  }

  class Point(val x:Int, val y:Int) {
   def +(p:Point) = new Point(x+p.x, y+p.y)
   def *(i:Int) = new Point(x*i, y*i)
  }
  def isPlayerWinHere(from:Point, line_to_win:Int, shift:Point, winFunc:(Int) => Boolean, board:Array[Array[Int]]) = {
   (0 to line_to_win-1).forall(offset => {
      val next_point = from + (shift*offset)
      onBoard(next_point, board) && winFunc(board(next_point.x)(next_point.y))
   })
  }

  def showBoard(player:ClientHandler) = {
    if(player == NetServer.clients(0)) {
      player.send("You are cross")
      if(is_crosses_move) player.send("Your turn now")
      else player.send("It is zero's turn now")
    }
    else if(player == NetServer.clients(1)) {
      player.send("You are zero")
      if(!is_crosses_move) player.send("Your turn now")
      else player.send("It is cross's turn now")
    }

    val len = board.length
    var new_j = len-1
    var str:StringBuilder = new StringBuilder
    (0 to len-1).foreachpair(
      (i, j) => {
        if(j != new_j) {
          player.send(str.toString)
          str.clear
          new_j = j
        }
        board(i)(j) match {
          case 1 => str.append("x ")
          case 2 => str.append("o ")
          case _ => str.append("_ ")
        }
      }
    )
    player.send(str.toString)
    player.send("-----------------")
  }

  var is_crosses_move= true
  def makeMove(player:ClientHandler, player_type:Int) = {
    if(player.hasNewIncomingData) {
      val data = player.incomingData
      if(data.has("raw")) {
      coord(data.getString("raw")) match {
        case (x, y) =>
          if(!onBoard(x, y)) player.send("your move is not inside the board")
          else if(!isFreePoint(x, y)) player.send("you try to move on a non-free point")
          else {
            board(x)(y) = player_type
            is_crosses_move = !is_crosses_move
            NetServer.clients.foreach(client => showBoard(client))
          }
        case _ => player.send("wrong move. type 'x y', where x, y - coordinates of the free point of the board")
        }
      }
    }
  }

  AI.registerAI(() => {
    if(NetServer.clients.length == 2) {
      if(is_crosses_move) makeMove(NetServer.clients(0), 1)
      else makeMove(NetServer.clients(1), 2)
    }
  })

  def main(args:Array[String]):Unit = start
}