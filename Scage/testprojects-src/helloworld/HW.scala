package helloworld

object HW {

  var list:List[() => Unit] = Nil
  def addFunc(func: => Unit) = list = (() => func) :: list

  var x = 0
  var y = 0
  addFunc {
    for(i <- 0 to 4) {
      x += 1
      y -= 1
      println(x+" tttttt "+y)
    }
  }

  def main(args:Array[String]):Unit = {
    println("start here")
    list.foreach(func => func())
  }
}