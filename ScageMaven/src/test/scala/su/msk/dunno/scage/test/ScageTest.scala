package su.msk.dunno.scage.test;

import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.ScageScreen
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.support.tracer._

import junit.framework._
import Assert._

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
      new ScageScreen("Hello World", is_main_screen = true, properties = "scagetest-properties.txt") {
        val tracer = new CoordTracer[CoordTrace]

        val trace = tracer.addTrace(Vec(width/2, height/2), new CoordTrace() {
          def getState = new State
          def changeState(changer:Trace, state:State) {}
        })

        val another_trace = tracer.addTrace(Vec(width/4, height/2), new CoordTrace() {
          def getState = new State
          def changeState(changer:Trace, state:State) {}
        })

        def moveIfFreeLocation(trace:CoordTrace, delta:Vec) {
          val new_location = trace.coord + delta
          if(!tracer.hasCollisions(trace, new_location, 20))
            tracer.updateLocation(trace, new_location)
        }

        key(KEY_UP,    10, onKeyDown = moveIfFreeLocation(trace, Vec(0,1)))
        key(KEY_DOWN,  10, onKeyDown = moveIfFreeLocation(trace, Vec(0,-1)))
        key(KEY_RIGHT, 10, onKeyDown = moveIfFreeLocation(trace, Vec(1,0)))
        key(KEY_LEFT,  10, onKeyDown = moveIfFreeLocation(trace, Vec(-1,0)))
        key(KEY_W,         onKeyDown = trace.coord is Vec(0,0))

        val poly = displayList {
          drawPolygon(Array(Vec(100, 300), Vec(150, 250), Vec(300, 300), Vec(300, 450), Vec(200, 400)), CYAN)
        }

        val stars = displayList {
          for(i <- 1 to 100) {
            drawPoint(Vec(math.random.toFloat*width, math.random.toFloat*height), randomColor)
          }
        }

        backgroundColor = BLACK
        interface {
          print(xml("hello.world"), width/2, height/2,    WHITE)
          print(trace.point,        width/2, height/2-20, WHITE)
          print(fps, 10, height-20, WHITE)
        }
        render {
          drawDisplayList(stars)
          drawFilledCircle(trace.coord, 10, RED)
          drawCircle(another_trace.coord, 10, GREEN)
          drawDisplayList(poly)
        }
      }.run()
      assertTrue(true)
    };
}
