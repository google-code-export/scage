package scagetest

import su.msk.dunno.scage.support.ScageProperties._

object TestProject extends Application {
  /*implicit def rangeToPairs(range:Range) = {
    new ScalaObject {
      def foreachpair(doIt:(Int, Int) => Unit) = {
        range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }

      def foreachpair(second_range:Range)(doIt:(Int, Int) => Unit) = {
        second_range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(second_range.length, number)).toList ::: pairs)
             .foreach(pair => doIt(pair._1, pair._2))
      }
    }
  }

  private var coord_matrix = Array.ofDim[List[Int]](5, 5)
  (0 to 4).foreachpair((i, j) => {
    println(i+":"+j)
    coord_matrix(i)(j) = List(1)
  })*/

  /*properties = "test-properties.txt"
  println(property("testprop1", false))
  println(property("testprop2", true))
  println(property("testprop3", 0.0f))*/
  /*println(5)

  val s = "10.1f".toFloat
  println(s)*/
  val logEntry = """(\d+) (\d+)""".r
  val str = "5 6"
  val logEntry(first_coord, second_coord ) = str

  def str2list[A](s:String, regexp:String = " "):List[A] = {
    str.split(regexp).toList.map(p => p.asInstanceOf[A])
  }

  val l = str2list[Int](s = str)
  println(l(0) + l(1))

  str match {
    case logEntry(first_coord, second_coord) =>
    println(first_coord)
    println(second_coord)
  }
}