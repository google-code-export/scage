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

        val coord = Vec(width/2, height/2)
        val trace_id = tracer.addTrace(new Trace() {
          def getPoint = tracer.point(coord)
          def getState = new State
          def changeState(changer:Trace, state:State) = {}
        })

        key(KEY_UP, 10, onKeyDown = tracer.updateLocation({
          coord is coord + Vec(0,10)
          trace_id
        }))
        key(KEY_DOWN, 10, onKeyDown = tracer.updateLocation({
          coord is coord - Vec(0,10)
          trace_id
        }))
        key(KEY_LEFT, 10, onKeyDown = tracer.updateLocation({
          coord is coord - Vec(10,0)
          trace_id
        }))
        key(KEY_RIGHT, 10, onKeyDown = tracer.updateLocation({
          coord is coord + Vec(10,0)
          trace_id
        }))

        backgroundColor = WHITE
        interface {
          print(xml("hello.world"), width/2, height/2, BLACK)
        }
        render {
          Renderer.color = BLACK
          Renderer.drawCircle(coord, 10)
        }
      }.run
      assertTrue(true)
    };
}
