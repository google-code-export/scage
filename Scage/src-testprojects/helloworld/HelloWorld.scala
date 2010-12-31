package helloworld

import su.msk.dunno.screens.ScageScreen
import su.msk.dunno.screens.handlers.Renderer
import su.msk.dunno.scage.support.ScageColors
import su.msk.dunno.screens.support.tracer.{Trace, Tracer}
import su.msk.dunno.scage.support.{Vec}

object HelloWorld extends Application {
  /*val myScreen = new ScageScreen("My Screen", is_main_screen = true, "blame-properties.txt") {
    Renderer.backgroundColor = ScageColors.GREEN
    println(Renderer.backgroundColor)

    Renderer.color = ScageColors.CYAN
    println(Renderer.color)

    /*addRender(new ScageRender {
      override def interface = {
         print(xml("hello.world"), Renderer.width/2, Renderer.height/2)
         Renderer.drawCircle(Vec(Renderer.width/2, Renderer.height/2), 5)         
      }
    })

    run*/
  }*/

  /*implicit def toObjectWithForeachI[A](l:List[A]) = new ScalaObject {
    def foreachi(func:(A, Int) => Unit) = {
      var index = 0
      l.foreach(value => {
        func(value, index)
        index += 1
      })
    }
  }

  var mylist = List[Float](1)
  mylist = mylist.head :: 1.5f :: mylist.tail
  println(mylist)

  mylist.foreachi((value, index) => println(value+index))

  var mymap = Map(1 -> "one", 2 -> "two", 3 -> "three")
  println(mymap)
  mymap += (2 -> "twotwo")
  println(mymap)
  mymap(2) = "twotwotwo"
  println(mymap)*/

  /*class TestClass(_param:Int) {
    val param = _param
  }

  val test = new TestClass(5)
  println(test.param)*/

  class MyTracer extends Tracer[Trace]

  val mytracer = new MyTracer
  val point = Vec(0,1)
  println(mytracer.point(mytracer.pointCenter(point)))
}
