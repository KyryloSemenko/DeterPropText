package cz.semenko.word.database.tests;


import junit.framework.TestCase;
import cz.semenko.word.sleeping.MemoryCleaner;

public class TestMemoryCleaner extends TestCase {
	// Componenta pod spravou Spring FW
	private MemoryCleaner memoryCleaner;
	
	public TestMemoryCleaner() {
	}

	/**
	 * @param memoryCleaner the memoryCleaner to set
	 */
	public void setMemoryCleaner(MemoryCleaner memoryCleaner) {
		this.memoryCleaner = memoryCleaner;
	}

	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		memoryCleaner.cleanMemoryFromRedundantObjects();
	}
}
