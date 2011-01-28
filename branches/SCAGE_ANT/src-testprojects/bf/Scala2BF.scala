package bf

import collection.mutable.HashMap

object Text2BF extends Application {
  def printBF(s:String) = {
    val char_position = HashMap[Char, Int]()
    val number_position = HashMap[Int, Int]()
    var position = 0

    for(i <- 1 to 10) print("+")
    print("[")
    s.foreach(char => {
      if(!char_position.contains(char)) {
        val number = char/10
        if(!number_position.contains(number)) {
          position += 1
          print(">")
          for(i <- 1 to char/10) print("+")
          number_position += (number -> position)
        }
        if(number_position.contains(number)) char_position += (char -> number_position(number))
      }
    })
    number_position.keys.foreach(char => print("<"))
    print("-]")

    var cur_position = 0
    s.foreach(char => {
      position = char_position(char)
      if(position > cur_position)
        for(i <- 1 to (position - cur_position)) print(">")
      else if(cur_position > position)
        for(i <- 1 to (cur_position - position)) print("<")
      print(".")
      cur_position = position
    })

    println
    println(char_position)
    println(number_position)
  }
  /*s.foreach(char => {

  })*/

  printBF("BrainFuck Is A Language!")
}