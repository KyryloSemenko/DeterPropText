package test.java.cz.semenko.word.dao;


import junit.framework.TestCase;
import cz.semenko.word.sleeping.MemoryCleaner;

public class MemoryCleanerTest extends TestCase {
	// Componenta pod spravou Spring FW
	private MemoryCleaner memoryCleaner;
	
	public MemoryCleanerTest() {
	}

	/**
	 * @param memoryCleaner the memoryCleaner to set
	 */
	public void setMemoryCleaner(MemoryCleaner memoryCleaner) {
		this.memoryCleaner = memoryCleaner;
	}

	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		//memoryCleaner.cleanMemoryFromRedundantObjects();
	}
}
