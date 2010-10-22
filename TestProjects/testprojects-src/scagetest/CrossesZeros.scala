package scagetest

object CrossesZeros extends Application {
  implicit def rangeToPairs(range:Range) = {
   new ScalaObject {
      def foldeachpair[A](second_range:Range)(answer:A)(findAnswer:(Int, Int) => A):A = {
       second_range.foldLeft(List[(Int, Int)]())((pairs, number) =>
         (range zip List().padTo(second_range.length, number)).toList ::: pairs).foldLeft(answer)((answer, pair) => findAnswer(pair._1, pair._2))
      }

     def foreachpair(doIt:(Int, Int) => Unit) = {
        range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }
   }
  }

  class Point(val x:Int, val y:Int) {
   def +(p:Point) = new Point(x+p.x, y+p.y)
   def *(i:Int) = new Point(x*i, y*i)
  }

  def onBoard(p:Point, board:Array[Array[Int]]) = {
   p.x >= 0 && p. x < board.length && p.y >= 0 && p.y < board.length
  }

  def isPlayerWinHere(from:Point, line_to_win:Int, shift:Point, winFunc:(Int) => Boolean, board:Array[Array[Int]]) = {
   (0 to line_to_win-1).forall(offset => {
      val next_point = from + (shift*offset)
      onBoard(next_point, board) && winFunc(board(next_point.x)(next_point.y))
   })
  }

  def isPlayerWinGame(line_to_win:Int, winFunc:(Int) => Boolean, board:Array[Array[Int]]) = {
   val x_length = board.length
   val y_length = board(0).length

   (0 to x_length).foldeachpair(0 to y_length)(false)(
      (x, y) => {
       val from = new Point(x, y)

       isPlayerWinHere(from, line_to_win, new Point(1, 0), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(-1, 0), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(0, 1), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(0, -1), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(1, 1), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(-1, -1), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(1, -1), winFunc, board) ||
       isPlayerWinHere(from, line_to_win, new Point(-1, 1), winFunc, board)
   })
  }

  def generateBoard(side:Int) = {
    val board = Array.ofDim[Int](side, side)
    var is_crosses_move = true
    for(i <- 0 to (side*side)-1) {
      val x = (math.random*side).toInt
      val y = (math.random*side).toInt

      if(board(x)(y) == 0) {
        if(is_crosses_move) board(x)(y) = 1
        else board(x)(y) = 2
        is_crosses_move = !is_crosses_move
      }
    }
    board
  }

  def showBoard(board:Array[Array[Int]]) = {
    val len = board.length
    var new_j = len-1
    (0 to len-1).foreachpair(
      (i, j) => {
        if(j != new_j) {
          println
          new_j = j
        }
        board(i)(j) match {
          case 1 => print("x ")
          case 2 => print("o ")
          case _ => print("_ ")
        }
      }
    )
  }

  val board = generateBoard(10)
  showBoard(board)
}