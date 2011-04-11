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
import su.msk.dunno.scage.screens.support.newtracer.{State, Trace, Tracer}
import su.msk.dunno.scage.screens.handlers.Renderer
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
        val tracer = new Tracer[Trace](0,640,0,480,32,24)

        val trace = tracer.addTrace(Vec(16,12), new Trace() {
          def getState = new State
          def changeState(changer:Trace, state:State) {}
        })

        key(KEY_UP,    100, onKeyDown = tracer.move(trace, Vec(0,1)))
        key(KEY_DOWN,  100, onKeyDown = tracer.move(trace, Vec(0,-1)))
        key(KEY_RIGHT, 100, onKeyDown = tracer.move(trace, Vec(1,0)))
        key(KEY_LEFT,  100, onKeyDown = tracer.move(trace, Vec(-1,0)))

        backgroundColor = WHITE
        interface {
          print(xml("hello.world"), width/2, height/2, BLACK)
          print(trace.point, width/2, height/2-20, BLACK)
        }
        render {
          Renderer.color = BLACK
          Renderer.drawCircle(tracer.pointCenter(trace.point), 10)
        }
      }.run
      assertTrue(true)
    };
}
