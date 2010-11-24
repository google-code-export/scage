package helloworld

object HelloWorld extends Application {
  var my_list = List(1,2,3,4,5)

  var count = 5
  my_list.foreach(number => {
    if(count > 0) {
      my_list = my_list ::: List(0)
      count -= 1
    }
    println(my_list)
    my_list = my_list.tail
  })
}