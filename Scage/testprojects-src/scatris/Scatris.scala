package scatris

import figures.Square
import su.msk.dunno.scage.support.ScageLibrary
import su.msk.dunno.scage.support.tracer.StandardTracer

object Scatris extends Application with ScageLibrary {
  new Square(StandardTracer.pointCenter(3, 12))

  start
}