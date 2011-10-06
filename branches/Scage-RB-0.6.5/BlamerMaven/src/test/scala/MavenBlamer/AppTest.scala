package MavenBlamer;

import junit.framework._
import su.msk.dunno.blame.screens.Blamer
import su.msk.dunno.scage.screens.support.ScageColorTest
;
import Assert._;

object AppTest {
    def suite: Test = {
        val suite = new TestSuite(classOf[AppTest]);
        suite
    }

    def main(args : Array[String]) {
        junit.textui.TestRunner.run(suite);
    }
}

/**
 * Unit test for simple App.
 */
class AppTest extends TestCase("app") {

    /**
     * Rigourous Tests :-)
     */
    def testOK() = {
      Blamer.run
      //ScageColorTest.main_screen.run
      assertTrue(true)
    };
    //def testKO() = assertTrue(false);
    

}
