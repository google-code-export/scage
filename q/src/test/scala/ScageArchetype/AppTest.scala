package ScageArchetype;

import junit.framework._;
import Assert._
import su.msk.dunno.q.QueersIsland
;

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
      QueersIsland.run
      assertTrue(true)
    };
    //def testKO() = assertTrue(false);
    

}
