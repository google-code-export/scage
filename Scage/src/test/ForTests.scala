package test

object ForTests {
  class MyInt(val mi:Int) {
    val plus:(Int) => Int = (i:Int) => mi + i
  }

  implicit def int2myint(i:Int) = new MyInt(i)

  def test_=(s:String) = println(s)

  def myfor(i:Int, func: (Int) => (Boolean, Int))(p: (Int) => Unit):Unit = {
    if(func(i)._1) { p(i); myfor(func(i)._2, func)(p) }
  }

  def mywhile(condition: => Boolean)(p: => Unit):Unit = {
    if(condition){p; mywhile(condition)(p)}
  }

  def main(args:Array[String]):Unit = {
    var i = 0
    mywhile(i < 5) {
      println("hello")
      i += 1
    }
  }

  def root(x:Double):Double = {
    def root_(ans:Double):Double = {
      if(Math.abs(ans*ans/x - 1) < 0.01)(ans + x/ans)/2
      else root_((ans + x/ans)/2)
    }
    root_(1)
  }

  def fac(n:Int):Int = {
    def fac_(ans:Int, n:Int):Int = {
      if(n == 0)ans
      else if(n < 0){println("you mad :)"); ans}
      else fac_(ans*n, n-1)
    }
    fac_(1, n)
  }

  def findFixed(f: Double => Double)(a:Double, b:Double):List[Double] = {
    def finder_(ans:List[Double], x:Double, b:Double):List[Double] = {
      if(x > b)ans
      else {
        val new_ans = (Math.abs(f(x)/x - 1) < 0.01) match {
          case true => x :: ans
          case false => ans
        }
        finder_(new_ans, x+1, b)
      }
    }
    finder_(List[Double](), a, b)
  }
}