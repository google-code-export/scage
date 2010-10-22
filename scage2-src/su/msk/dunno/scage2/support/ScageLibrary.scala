package su.msk.dunno.scage2.support

import su.msk.dunno.scage2.handlers.Renderer

trait ScageLibrary extends Colors {
  lazy val width = Renderer.width
  lazy val height = Renderer.height
}