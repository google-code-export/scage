package helloworld

object HelloAround extends Application {
  /*val range = 5 to 0 by -1
  println(range.foldLeft(0)((sum, index) => sum+index))*/

  implicit def range2foldable[T](range:Range) = new ScalaObject {
    def foldeachpair[T](second_range:Range)(result:T)(func:(T, Int, Int) => T) = {
      second_range.foldLeft(List[(Int, Int)]())((pairs, number) =>
          (range zip List().padTo(range.length, number)).toList ::: pairs)
             .foldLeft(result)((result, pair) => func(result, pair._1, pair._2))
    }
  }

  val arr = Array.ofDim[Int](4,5)
  arr(0) = Array(1,6,3,6, 2)
  arr(1) = Array(4,2,7,2,-4)
  arr(2) = Array(6,8,-2,7,3)
  arr(3) = Array(4,9,3,-4,11)


  val biggest = (0 to arr.length-1).foldeachpair(0 to arr(0).length-1)(Integer.MIN_VALUE)((biggest, i, j) => {
    math.max(biggest, arr(i)(j))
  })
  println(biggest)  // 11

  /*val lowest = (0 to arr.length-1).foldeachpair(Integer.MAX_VALUE)((lowest, i, j) => math.min(lowest, arr(i)(j)))
  println(lowest)  // -4

  val sum = (0 to arr.length-1).foldeachpair(0)((sum, i, j) => sum + arr(i)(j))
  println(sum)  // 88

  val coord_of_biggest = (0 to arr.length-1).foldeachpair((-1, -1))((coord, i, j) => if(biggest == arr(i)(j)) (i,j) else coord)
  println(coord_of_biggest) // (4,0)

  val new_arr = (0 to arr.length-1).foldeachpair(arr)((arr, i, j) => {arr(i)(j) = arr(i)(j)*(-1); arr})
  println(new_arr)*/

  /*val biggest = (0 to arr.length-1).foldLeft(Integer.MIN_VALUE)((max, i) => {
    math.max(max, (0 to arr.length-1).foldLeft(Integer.MIN_VALUE)((max, j) => math.max(max, arr(i)(j))))
  })*/


  /*def max(arr:Array[Int]) = arr.foldLeft(Integer.MIN_VALUE)((max, i) => math.max(max, i))
  val biggest = (0 to arr.length-1).foldLeft(Integer.MIN_VALUE)((maxx, i) => math.max(maxx, max(arr(i))))*/

  //println(biggest) // 9

  /*def filter[T](l:List[T], condition:T => Boolean) = l.foldLeft(List[T]())((new_list, element) => {
    if(condition(element)) element :: new_list ele new_list
  })*/

  /*def myFold[A, B](l:List[A], initial:B)(func:(B, A) => B) = {
    l match {
      case head :: tail => myFold(func(initial, head))(func)
      case _ => initial
    }
  }*/
}