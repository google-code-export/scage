package su.msk.dunno.scar

import su.msk.dunno.scage.screens.physics._
import su.msk.dunno.scage.screens.physics.objects._

import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import net.phys2d.math.Vector2f
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.ScageProperties._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.single.support.{ScageColor, Vec}
import org.lwjgl.opengl.GL11

object Scaranoid extends PhysicsScreen(
  screen_name = "Scaranoid",
  is_main_screen = true,
  properties = "scaranoid-properties.txt"
) {
  private var count = 0
  private var bonus = 1
  init {
    count = 0
    bonus = 1
  }

  this --> new StaticLine(Vec(30,  10),   Vec(30,  470))
  this --> new StaticLine(Vec(30,  470),  Vec(630, 470))
  this --> new StaticLine(Vec(630, 470),  Vec(630, 10))
  val down_line = this --> new StaticLine(Vec(630, 10),   Vec(30,  10)) {
    init {
      prepare()
    }

    action {
      if(isTouching(player_ball)) pause
    }
  }

  def randomColor = {
    (math.random*3).toInt match {
      case 0 => RED
      case 1 => YELLOW
      case 2 => BLUE
    }
  }

  implicit def compareColors(color:ScageColor) = new ScalaObject {
    def >(other_color:ScageColor) = {
      color match {
        case RED => other_color == YELLOW
        case YELLOW => other_color == BLUE
        case BLUE => other_color == RED
        case _ => false
      }
    }
  }

  def colorModificator(box_color:ScageColor) = {
    if(player_ball_current_color == WHITE) 4
    if(player_ball_current_color > box_color) 3
    else if(player_ball_current_color == box_color) 2
    else 1
  }

  class TargetBox(leftup_coord:Vec, box_color:ScageColor) extends StaticBox(leftup_coord, 40, 40) {
    action {
      if(isActive && isTouching(player_ball)) {
        count += bonus*colorModificator(box_color)

        player_ball_current_color = box_color
        isActive = false

        if(winCondition) pause
        else bonus += 1
      }
    }

    override def render() {
      color = box_color
      val verts:Array[Vector2f] = box.getPoints(body.getPosition(), body.getRotation());
      GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        verts.foreach(v => GL11.glVertex2f(v.getX, v.getY))
      GL11.glEnd();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
  }

  private var boxes:List[Physical] = Nil
  init {
    boxes = Nil
    for(i <- 0 to 12) boxes = (this --> new TargetBox(Vec(35 + i*45, 460), randomColor)) :: boxes
    for(i <- 0 to 12) boxes = (this --> new TargetBox(Vec(35 + i*45, 415), randomColor)) :: boxes
    for(i <- 0 to 12) boxes = (this --> new TargetBox(Vec(35 + i*45, 370), randomColor)) :: boxes
  }
  def winCondition = boxes.forall(!_.isActive)

  val player_platform = this --> new StaticBox(Vec(width/2,25), 50, 10) {
    init {
      coord = Vec(width/2,25)
    }

    action {
      if(isTouching) bonus = 1
    }
  }

  val additional_platform = this --> new StaticBox(Vec(width/4, 200), 50, 10) {
    init {
      coord = Vec(width/4, 200)
    }

    private var dir = 1
    action {
      if(isTouching(player_ball)) player_ball_current_color = WHITE

      move(Vec(dir,0))
      if(coord.x > 600) dir = -1
      else if(coord.x < 60) dir = 1
    }
  }

  key(KEY_LEFT,  10, onKeyDown = if(!onPause && player_platform.coord.x > 60) player_platform.move(Vec(-3, 0)))
  key(KEY_RIGHT, 10, onKeyDown = if(!onPause && player_platform.coord.x < 600) player_platform.move(Vec(3, 0)))

  val ball_radius = property("ball.radius", 5)
  val ball_speed = property("ball.speed", 25)
  private var player_ball_current_color = WHITE
  val player_ball = this --> new DynaBall(Vec(width/2, height/2), ball_radius) {
    init {
      coord = Vec(width/2, height/2)
      velocity = new Vec(-ball_speed, -ball_speed)
    }

    action {
      if(velocity.norma < ball_speed-1)
        velocity = velocity.n * ball_speed
      else if(math.abs(velocity.y) < 1)
        velocity = Vec(velocity.x, 10*math.signum(velocity.y))
    }

    override def render() {
      color = player_ball_current_color
      drawFilledCircle(coord, radius)
    }
  }

  interface {
    if(onPause) {
      if(winCondition) print(xml("game.win"), width/2, height/2, WHITE)
      else print(xml("game.lose"), width/2, height/2, WHITE)
      print(xml("game.playagain"), width/2, height/2-20, WHITE)
    }
    print(count, 5, height-20, WHITE)
    print("x"+bonus, 5, height-40, WHITE)
    print(fps, 5, height-60, WHITE)
  }
  key(KEY_Y, onKeyDown = if(onPause) {
    init
    pauseOff
  })
  key(KEY_N, onKeyDown = if(onPause) stop)

  new ScageScreen("Help Screen") {
    key(KEY_SPACE, onKeyDown = stop)

    interface {
      print(xml("helpscreen.helpmessage"), 10, height-20, WHITE)
    }
  }.run

  def main(args:Array[String]):Unit = run
}