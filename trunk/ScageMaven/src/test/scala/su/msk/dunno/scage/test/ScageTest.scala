package su.msk.dunno.scage.test;

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.net.NetServer._
import su.msk.dunno.scage.screens.support.net.ClientHandler

import junit.framework._
import Assert._
import su.msk.dunno.scage.screens.support.physics.ScagePhysics
import su.msk.dunno.scage.single.support.messages.ScageMessage
import su.msk.dunno.scage.screens.support.tracer._
import su.msk.dunno.scage.screens.support.physics.objects.{StaticPolygon, DynaBall}

object ScageTest {
    def suite: Test = {
        val suite = new TestSuite(classOf[ScageTest]);
        suite
    }

    def main(args : Array[String]) {
        junit.textui.TestRunner.run(suite);
    }
}

/**
 * Unit test for simple App.
 */
class ScageTest extends TestCase("app") {

    /**
     * Rigourous Tests :-)
     */
    def testOK() {
      new ScageScreen(screen_name = "Hello World", is_main_screen = true, properties = "scagetest-properties.txt") {
        val tracer = new CoordTracer[CoordTrace]

        val trace = tracer.addTrace(Vec(screen_width/2, screen_height/2), new EmptyCoordTrace)
        val another_trace = tracer.addTrace(Vec(screen_width/4, screen_height/2), new EmptyCoordTrace)

        def moveIfFreeLocation(trace:CoordTrace, delta:Vec) {
          val new_location = trace.coord + delta
          if(!tracer.hasCollisions(trace, new_location, 20))
            tracer.updateLocation(trace, new_location)
        }

        key(KEY_W, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,1)))
        key(KEY_A, 10, onKeyDown = moveIfFreeLocation(trace, Vec(-1,0)))
        key(KEY_S, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,-1)))
        key(KEY_D, 10, onKeyDown = moveIfFreeLocation(trace, Vec(1,0)))

        anykey(onKeyDown = println("any key pressed =)"))

        /*leftMouse(onBtnDown = {
          mouse_coord => tracer.updateLocation(trace, mouse_coord)
        })*/

        val physics = new ScagePhysics

        val poly_render = displayList {
          color = CYAN
          drawPolygon(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400))
        }
        val poly_physical = new StaticPolygon(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400))
        physics.addPhysical(poly_physical)

        val stars = displayList {
          for(i <- 1 to 100) {
            drawPoint(Vec(math.random.toFloat*screen_width, math.random.toFloat*screen_height), randomColor)
          }
        }

        private var target_point = trace.coord
        mouseMotion {
          mouse_coord =>
            target_point = (mouse_coord - trace.coord).n * 20
        }
        private var x = 0.0f
        def period = {
          x += 0.01f
          if(x > 2*math.Pi) x = 0
          (250 * 0.25f*(math.sin(x)) + 1).toLong
        }
        action(period) {
          physics.step()
        }
        leftMouse(onBtnDown = {
          mouse_coord => physics.addPhysical(new DynaBall(trace.coord + target_point, 2) {
            val ball_trace = tracer.addTrace(trace.coord + target_point, new EmptyCoordTrace)
            val action_id:Int = action {
              tracer.updateLocation(ball_trace, coord)
              coord = ball_trace.coord
              /*if(!tracer.isCoordOnArea(coord)) {
                isActive = false
                delActionOperation(action_id)
              }*/
            }

            velocity = (mouse_coord - trace.coord).n*10
            render {
              if(isActive) drawFilledCircle(coord, 2, YELLOW)
            }
          })
        })

        backgroundColor = colorFromString("BLACK")
        val another_font = new ScageMessage(font_size = 12)
        interface {
          another_font.print(xml("hello.world"), screen_width/2, screen_height/2+20,    WHITE)
          print(xml("hello.world"), screen_width/2, screen_height/2,    WHITE)
          print(trace.point,        screen_width/2, screen_height/2-20, WHITE)
          print(fps, 10, screen_height-20, WHITE)
        }
        render {
          drawDisplayList(stars)
          drawFilledCircle(trace.coord, 10, RED)
          drawLine(trace.coord, trace.coord + target_point)
          drawCircle(another_trace.coord, 10, GREEN)

          drawDisplayList(poly_render)
          for((point, _) <- poly_physical.touchingPoints(physics)) drawFilledCircle(point, 5, BLUE)
        }

        /*startServer()
        action {
          clients.foreach(client => {
            if(client.hasNewIncomingData) {
              if(client.incomingData.has("quit")) stop()
              else client.send(client.incomingData)
            }
          })
        }
        exit {
          stopServer()
        }*/
      }.run()
      assertTrue(true)
    };
}
