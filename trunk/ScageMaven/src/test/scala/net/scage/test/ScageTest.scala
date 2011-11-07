package net.scage.test

import _root_.net.scage.support.ScageColors._
import _root_.net.scage.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import _root_.net.scage.support.messages.ScageMessage._
import _root_.net.scage.ScageScreen
import _root_.net.scage.support.Vec
import net.scage.support.net.NetServer._
import net.scage.support.net.ClientHandler
import concurrent.ops._

import junit.framework._
import Assert._
import net.scage.support.physics.ScagePhysics
import _root_.net.scage.support.messages.ScageMessage
import net.scage.support.tracer._
import net.scage.support.physics.objects.{StaticPolygon, DynaBall}
import collection.mutable.ListBuffer
import javax.swing.JOptionPane

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
          if(!tracer.hasCollisions(trace, new_location, 20))    // test collisions using tracer
            tracer.updateLocation(trace, new_location)
        }

        key(KEY_W, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,1)))
        key(KEY_A, 10, onKeyDown = moveIfFreeLocation(trace, Vec(-1,0)))
        key(KEY_S, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,-1)))
        key(KEY_D, 10, onKeyDown = moveIfFreeLocation(trace, Vec(1,0)))

        private var input_text = ""
        key(KEY_F1, onKeyDown = spawn {
          input_text = JOptionPane.showInputDialog("Input text here")
        })
        interface {
          print("F1: enter text", screen_width/2, screen_height/2-40, WHITE)
          if(input_text != "") {
            print("Here is your text: "+input_text, screen_width/2, screen_height/2-60, WHITE)
          }
        }

        anykey(onKeyDown = println("any key pressed =)"))   // test special method to obtain "press any key" event

        /*leftMouse(onBtnDown = {
          mouse_coord => tracer.updateLocation(trace, mouse_coord)
        })*/

        val physics = new ScagePhysics

        val poly_render = displayList {   // test special method to save any opengl code as display list
          color = CYAN
          drawPolygon(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400))
        }
        val poly_physical = new StaticPolygon(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400))
        physics.addPhysical(poly_physical)

        val stars = displayList {   // I like "starry sky" since high school =)
          for(i <- 1 to 100) {
            drawPoint(Vec(math.random.toFloat*screen_width, math.random.toFloat*screen_height), randomColor)
          }
        }

        private var target_point = trace.coord
        mouseMotion {   // test mouse motion event
          mouse_coord =>
            target_point = (mouse_coord - trace.coord).n * 20
        }
        private var x = 0.0f
        def period = {
          x += 0.01f
          if(x > 2*math.Pi) x = 0
          (125 * 0.25f*(math.sin(x)) + 1).toLong
        }
        action(period) {  // test actions with non-static period defined as function
          physics.step()
        }
        leftMouse(onBtnDown = {  // test left mouse event
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
              if(physics.containsPhysical(this)) drawFilledCircle(coord, 2, YELLOW)
            }
          })
        })

        backgroundColor = colorFromString("BLACK")    // test method to obtain color by name
        val another_font = new ScageMessage(font_size = 12) // test using two different fonts in one app
        interface {
          another_font.print(xml("hello.world"), screen_width/2, screen_height/2+20,    WHITE)
          print(xml("help"), screen_width/2, screen_height/2,    WHITE) // test obtaining string from xml
          print(trace.point,        screen_width/2, screen_height/2-20, WHITE)
          print(fps, 10, screen_height-20, WHITE)
        }
        render {
          drawDisplayList(stars)
          drawFilledCircle(trace.coord, 10, RED)
          drawLine(trace.coord, trace.coord + target_point)
          drawCircle(another_trace.coord, 10, GREEN)

          drawDisplayList(poly_render)
        }

        private var touches:ListBuffer[(Vec, Long)] = ListBuffer()    // test obtaining touching points for physical objects
        action {
          for {
            (point, _) <- poly_physical.touchingPoints
            if !touches.exists(_._1 == point)
          } touches += ((point, System.currentTimeMillis))

          for {
            t <- touches
            if System.currentTimeMillis - t._2 > 5000
          } touches -= t
        }
        render {
          for((point, _) <- touches) drawFilledCircle(point, 3, RED)
        }

        // test network features: simple echo server
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

/*test*/
