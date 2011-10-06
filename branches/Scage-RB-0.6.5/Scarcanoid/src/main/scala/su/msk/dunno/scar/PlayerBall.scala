package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._
import Scaranoid._
import su.msk.dunno.scage.screens.support.physics.objects.DynaBall

object PlayerBall extends DynaBall(Vec(screen_width/2, screen_height/2), property("ball.radius", 5)) {
  val ball_speed = property("ball.speed", 25)

  var ball_color = WHITE

  init {
    println ("me here!")
    coord = Vec(screen_width/2, screen_height/2)
    velocity = new Vec(-ball_speed, -ball_speed)
  }

  action {
    if(velocity.norma < ball_speed-1)
      velocity = velocity.n * ball_speed
    else if(math.abs(velocity.y) < 1)
      velocity = Vec(velocity.x, 10*math.signum(velocity.y))
  }

  render {
    color = ball_color
    drawFilledCircle(coord, radius)
  }
}