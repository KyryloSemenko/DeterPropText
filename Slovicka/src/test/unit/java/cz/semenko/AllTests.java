package test.unit.java.cz.semenko;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.unit.java.cz.semenko.word.aware.KnowledgeTest;

/** All unit tests */
@RunWith(Suite.class)
@SuiteClasses({KnowledgeTest.class})
public class AllTests {

}
