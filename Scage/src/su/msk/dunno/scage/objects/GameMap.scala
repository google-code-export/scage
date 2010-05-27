package su.msk.dunno.scage.objects

import su.msk.dunno.scage.prototypes.Physical
import su.msk.dunno.scage.main.Engine
import su.msk.dunno.scage.handlers.AI
import su.msk.dunno.scage.support.Vec

object GameMap  {
  private val VERTICAL = 0
  private val HORIZONTAL = 1

  val game_width = Engine.getIntProperty("game_width")
  val game_height = Engine.getIntProperty("game_height")

  def generate():List[Physical] = {
    def generate_(l:List[Physical], prev_x:Int, prev_y:Int):List[Physical] = {
      if(game_width - prev_x < 100)return l
      val platform_width = 100
      val platfrom_direction = 0  // 0 or 1
      platfrom_direction match {
        case VERTICAL => {
          val min_platform_y = prev_y - (Math.random*prev_y).toInt
          val max_platform_y = prev_y + (Math.random*(game_height-prev_y)).toInt
          val new_l = new StaticBox(Vec(prev_x+platform_width/2, min_platform_y), platform_width, 10){
            var dir:Int = 1
            AI.registerAI(() => {
              val vec:Vec = new Vec(body.getPosition)+Vec(0,1)*dir
              if(vec.y <= min_platform_y)dir = 1
              else if(vec.y >= max_platform_y)dir = -1
              body.setPosition(vec.x, vec.y)
            })
          } :: l
          generate_(new_l, prev_x+platform_width, min_platform_y+(Math.random*(max_platform_y-min_platform_y)).toInt)
        }
        case HORIZONTAL => {
          val platform_y = prev_y + (-30+Math.random*60).toInt
          val platform_moving_width = (game_width - prev_x)*0.1f
          val new_l = new StaticBox(Vec(prev_x+platform_width/2, platform_y), platform_width, 10) {
            var dir:Int = 1
            AI.registerAI(() => {
              val vec:Vec = new Vec(body.getPosition)+Vec(1,0)*dir
              if(vec.x <= prev_x+platform_width/2)dir = 1
              else if(vec.y >= prev_x+platform_width/2+platform_moving_width)dir = -1
              body.setPosition(vec.x, vec.y)
            })
          } :: l
          generate_(new_l, (prev_x+platform_moving_width).toInt, platform_y)
        }
        case _ => return l
      }
    }
    val l = new StaticLine(Vec(0,0), Vec(game_width,0)) ::
              new StaticLine(Vec(game_width,0), Vec(game_width,game_height)) ::
              new StaticLine(Vec(game_width,game_height), Vec(0,game_height)) ::
              new StaticLine(Vec(0,game_height), Vec(0,0))
    generate_(l, 0, 0)
  }
}