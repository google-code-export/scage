package su.msk.dunno.runnegun

import net.scage.support.tracer3.{Trace, CoordTracer}
import collection.mutable.ArrayBuffer
import net.scage.ScageScreenApp
import net.scage.ScageLib

trait GameObject extends Trace {
  type ChangerType = GameObject
}

object Runnegun extends ScageScreenApp("Runnegun") with ScageLib {
  val tracer = new CoordTracer[GameObject](solid_edges = false)

  val bullet_radius = property("bullet.radius", 2)
  val player_radius = property("player.radius", 10)
  val enemy_radius = property("enemy.radius", 10)
  val num_enemies = property("enemy.amount", 5)

  private var score = 0

  private val enemies = ArrayBuffer[Enemy]()

  init {
    score = 0
    for(i <- 1 to num_enemies) enemies += new Enemy()
    action {
      if(!Player.isAlive) pause()
      val dead_enemies = enemies.filter(!_.isAlive)
      score += dead_enemies.length
      enemies --= dead_enemies
      if(enemies.size < math.random*num_enemies) enemies += new Enemy()
    }
  }

  key(KEY_SPACE, onKeyDown = {switchPause()})

  key(KEY_Y, onKeyDown = {
    if(onPause) {
      restart()
      pauseOff()
    }
  })
  key(KEY_N, onKeyDown = {if(onPause) stop()})

  backgroundColor = WHITE
  interface {
    print(fps, 10, window_height-20, BLACK)
    print("SCORE: "+score, 10, window_height-40, BLACK)
    if(onPause) print("PLAY AGAIN? (Y/N)", window_width/2, window_height/2, BLACK)
  }

  clear {
    enemies.clear()
    delAllActions()
    delAllRenders()
    tracer.removeAllTraces()
  }
}