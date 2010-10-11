package scagetest

import su.msk.dunno.scage.support.ScageProperties

object TestProject extends Application {
  //val list = List(9,8,7,6,5,4,3,2,1,0)
  //val range = 1 to 3
  //foreachpair(1 to 3)(println(_))
  println(makeThree(1 to 3).length)

  def foreachpair(range:Range)(doIt:((Any, Any)) => Unit) = {
    range.foldLeft(List[(Int, Int)]())((pairs, number) =>
      (range zip List().padTo(range.length, number)).toList ::: pairs)
         .foreach(doIt(_))
  }

  def makeThree(range:Range) = {
    val two = range.foldLeft(List[(Int, Int)]())((pairs, number) => (range zip List().padTo(range.length, number)).toList ::: pairs)
    range.foldLeft(List[((Int, Int), Int)]())((triples, number) => (two zip List().padTo(range.length*range.length, number)).toList ::: triples)
         .map(three => (three._1._1, three._1._2, three._2))
  }
}