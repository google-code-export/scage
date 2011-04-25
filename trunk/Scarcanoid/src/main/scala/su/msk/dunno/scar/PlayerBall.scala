package su.msk.dunno.scar

import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.screens.physics.objects.DynaBall
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer._
import su.msk.dunno.scage.single.support.ScageColors._

object PlayerBall extends DynaBall(Vec(width/2, height/2), property("ball.radius", 5)) {
  val ball_speed = property("ball.speed", 25)

  var ball_color = WHITE

  Scaranoid.init {
    println ("me here!")
    coord = Vec(width/2, height/2)
    velocity = new Vec(-ball_speed, -ball_speed)
  }

  Scaranoid.action {
    if(velocity.norma < ball_speed-1)
      velocity = velocity.n * ball_speed
    else if(math.abs(velocity.y) < 1)
      velocity = Vec(velocity.x, 10*math.signum(velocity.y))
  }

  override def render() {
    color = ball_color
    drawFilledCircle(coord, radius)
  }

  Scaranoid --> this
}