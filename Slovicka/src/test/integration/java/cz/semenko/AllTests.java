package test.integration.java.cz.semenko;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.integration.java.cz.semenko.word.dao.DBconnectorTest;
import test.integration.java.cz.semenko.word.dao.MemoryCleanerTest;

/** All integration tests */
@RunWith(Suite.class)
@SuiteClasses({DBconnectorTest.class, MemoryCleanerTest.class })
public class AllTests {

}
