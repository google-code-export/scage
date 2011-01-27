package su.msk.dunno.scage;

import junit.framework._
import su.msk.dunno.scage.support.ScageColors._
import su.msk.dunno.screens.handlers.Renderer._
import support.messages.ScageMessage
import su.msk.dunno.screens.prototypes.ScageRender
import su.msk.dunno.screens.ScageScreen
;
import Assert._;

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
        backgroundColor = WHITE

        addRender(new ScageRender{
          override def interface = {
            ScageMessage.print("Hello World!", width/2, height/2, BLACK)
          }
        })
      }.run
      assertTrue(true)
    };
}
