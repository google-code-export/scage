package su.msk.dunno.scage.test;

import junit.framework._
import su.msk.dunno.scage.single.support.ScageColors._
import su.msk.dunno.scage.screens.handlers.Renderer._
import org.lwjgl.input.Keyboard._
import su.msk.dunno.scage.single.support.messages.ScageMessage._
import su.msk.dunno.scage.screens.ScageScreen
;
import Assert._
import su.msk.dunno.scage.single.support.Vec
import su.msk.dunno.scage.screens.handlers.Renderer
import su.msk.dunno.scage.screens.support.newtracer._
;

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
    def testOK() = {
      new ScageScreen("Hello World", is_main_screen = true, properties = "scagetest-properties.txt") {
        val this_screen = this
        val tracer = new CoordTracer[CoordTrace](0,640,0,480,32,24) {
          this_screen.interface {

          }
        }

        val trace = tracer.addTrace(Vec(width/2, height/2), new CoordTrace() {
          def getState = new State
          def changeState(changer:Trace, state:State) {}
        })

        key(KEY_UP,    100, onKeyDown = tracer.move(trace, Vec(0,10)))
        key(KEY_DOWN,  100, onKeyDown = tracer.move(trace, Vec(0,-10)))
        key(KEY_RIGHT, 100, onKeyDown = tracer.move(trace, Vec(10,0)))
        key(KEY_LEFT,  100, onKeyDown = tracer.move(trace, Vec(-10,0)))
        key(KEY_W,     100, onKeyDown = trace.coord is Vec(0,0))

        backgroundColor = WHITE
        interface {
          print(xml("hello.world"), width/2, height/2, BLACK)
          print(trace.point, width/2, height/2-20, BLACK)
        }
        render {
          Renderer.color = BLACK
          Renderer.drawCircle(trace.coord, 10)
        }
      }.run
      assertTrue(true)
    };
}
