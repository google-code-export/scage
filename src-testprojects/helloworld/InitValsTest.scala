package helloworld

class InitValsClass {
  val p = "scage.properties.txt"
  println(p)
}

class InitValsClassExtender extends InitValsClass {
  override val p = "ttt"
}

object InitValsTest extends Application {
  val c = new InitValsClass
  val c1 = new InitValsClassExtender
}