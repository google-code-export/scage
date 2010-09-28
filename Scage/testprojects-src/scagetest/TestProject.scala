package scagetest

import su.msk.dunno.scage.support.ScageProperties

object TestProject extends Application {
  val p = ScageProperties.intProperty("name", 5)
  println(p)
}