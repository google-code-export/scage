package helloworld

trait Recursion[A,B] {
  var _self: A => B = null

  def recurse(x:A)(func: A => B) = {
    _self = func
    func(x)
  }
}

object RecursionTest extends Application with Recursion[Int, Int] {
  println(recurse(5) {
    x => if(x == 0) 1 else x*_self(x-1)
  })
}