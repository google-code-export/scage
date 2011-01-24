package helloworld

object HelloAround extends Application {
  val range = 5 to 0 by -1
  println(range.foldLeft(0)((sum, index) => sum+index))
}