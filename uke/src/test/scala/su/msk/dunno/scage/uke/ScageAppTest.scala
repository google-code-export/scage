package su.msk.dunno.scage.uke;

import junit.framework._;
import Assert._;

object ScageAppTest {
    def suite: Test = {
        val suite = new TestSuite(classOf[ScageAppTest]);
        suite
    }

    def main(args : Array[String]) {
        junit.textui.TestRunner.run(suite);
    }
}

/**
 * Unit test for simple App.
 */
class ScageAppTest extends TestCase("app") {

    /**
     * Rigourous Tests :-)
     */
    def testOK() {
      World.main(Array[String]())
      assertTrue(true)
    };
    //def testKO() = assertTrue(false);
    

}
