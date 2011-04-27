package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.physics.objects.StaticBox
import su.msk.dunno.scage.screens.handlers.Renderer._
import Scaranoid._
import org.lwjgl.input.Keyboard._

class PlayerPlatform extends StaticBox(Vec(width/2,25), 50, 10) {
  init {
    coord = Vec(width/2,25)
  }

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && coord.x > 60) move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && coord.x < width-40) move(Vec(3, 0)))

  Scaranoid --> this
}