package net.scage.test

import net.scage.ScageLib._
import net.scage.support.messages.ScageMessage
import concurrent.ops._

import junit.framework._
import Assert._
import net.scage.support.physics.ScagePhysics
import net.scage.support.tracer3.{Trace, CoordTracer}
import net.scage.support.physics.objects.{StaticPolygon, DynaBall}
import collection.mutable.ListBuffer
import javax.swing.JOptionPane
import net.scage.handlers.controller2.MultiController
import net.scage.ScreenApp
import net.scage.support.Vec

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
      new ScreenApp("Hello World") with MultiController {
        /*scage_log.info("starting main unit "+unit_name+"...")
        ScageProperties.properties = properties*/

        val rect_color = property("rect.color", RED)
        render {
          drawFilledRect(Vec(30, 30), 60, 20, rect_color)
        }

        render(10) {
          drawFilledRect(Vec(100, 30), 60, 20, YELLOW)
        }

        val tracer = CoordTracer()
        render(-10) {
          drawTraceGrid(tracer, DARK_GRAY)
        }

        val trace = tracer.addTrace(Vec(window_width/2, window_height/2))
        val another_trace = tracer.addTrace(Vec(window_width/4, window_height/2))

        def moveIfFreeLocation(trace:Trace, delta:Vec) {
          val new_location = trace.location + delta
          if(!tracer.hasCollisions(trace.id, new_location, 20))    // test collisions using tracer
            tracer.updateLocation(trace.id, new_location)
        }

        anykey(onKeyDown = println("any key pressed =)"))   // test special method to obtain "press any key" event

        key(KEY_W, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,1)))
        key(KEY_W, onKeyDown = println("also, W was pressed :3"))   // test for multiple functions on one key

        key(KEY_A, 10, onKeyDown = moveIfFreeLocation(trace, Vec(-1,0)))
        key(KEY_S, 10, onKeyDown = moveIfFreeLocation(trace, Vec(0,-1)))
        key(KEY_D, 10, onKeyDown = moveIfFreeLocation(trace, Vec(1,0)))

        private var input_text = ""
        key(KEY_F1, onKeyDown = spawn {
          input_text = JOptionPane.showInputDialog("Input text here")
        })

        /*leftMouse(onBtnDown = {
          mouse_coord => tracer.updateLocation(trace, mouse_coord)
        })*/

        val physics = new ScagePhysics
        val poly_physical = physics.addPhysical(new StaticPolygon(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400)))
        val poly_render = displayList {   // test special method to save any opengl code as display list
          drawPolygon(poly_physical.points, CYAN)
        }

        val stars = displayList {   // I like "starry sky" since high school =)
          for(i <- 1 to 100) {
            drawPoint(Vec(math.random.toFloat*window_width, math.random.toFloat*window_height), randomColor)
          }
        }

        private var target_point = trace.location
        /*def scaledCoord(coord:Vec, scale:Float, center:Vec) = {
          if(scale == 1) coord
          else (coord / scale) + (center - Vec(window_width / scale / 2, window_height / scale / 2))
        }*/
        mouseMotion {   // test mouse motion event
          mouse_coord =>
            target_point = (scaledCoord(mouse_coord) - trace.location).n * 20
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
          mouse_coord => physics.addPhysical(new DynaBall(trace.location + target_point, 2) {
            val ball_trace = tracer.addTrace(trace.location + target_point)
            val action_id:Int = action {
              tracer.updateLocation(ball_trace.id, coord)
              coord = ball_trace.location
              /*if(!tracer.isCoordOnArea(coord)) {
                isActive = false
                delActionOperation(action_id)
              }*/
            }

            velocity = (target_point).n*10
            render {
              if(physics.containsPhysical(this)) drawFilledCircle(coord, 2, YELLOW)
            }
          })
        })

        backgroundColor = fromStringOrDefault("BLACK", BLACK)    // test method to obtain color by name
        val another_font = new ScageMessage(max_font_size = 15, font_file = "comic.ttf") // test using two different fonts in one app
        interface {
          another_font.print(xml("hello.world"), window_width/2, window_height/2+20, WHITE)
        }

        interfaceFromXml("scagetest.help", Array(trace.location, tracer.point(trace.location), fps, input_text))

        render {
          drawDisplayList(stars)
          drawFilledCircle(trace.location, 10, RED)
          drawLine(trace.location, trace.location + target_point)
          drawCircle(another_trace.location, 10, GREEN)

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
        
        // scaling test
        mouseWheelUp(onWheelUp = m => scale += 1)
        mouseWheelDown(onWheelDown = m => if(scale > 1) scale -= 1)
        center = if(scale > 1) trace.location else windowCenter

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
        clear {
          stopServer()
        }*/
      }.main(Array[String]())
      assertTrue(true)
    };
}

/*test*/
