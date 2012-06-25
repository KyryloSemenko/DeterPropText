package cz.semenko.word.database.tests;


import junit.framework.TestCase;
import cz.semenko.word.sleeping.MemoryCleaner;

public class TestMemoryCleaner extends TestCase {

	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		MemoryCleaner memoryCleaner = MemoryCleaner.getInstance();
		memoryCleaner.cleanMemoryFromRedundantObjects();
	}
}
