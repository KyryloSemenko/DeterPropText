package test.java.cz.semenko.word.dao;


import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.sleeping.MemoryCleaner;

public class MemoryCleanerTest extends TestCase {
	public static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
	static ApplicationContext ctx;

	/**
	 * @param memoryCleaner the memoryCleaner to set
	 */
	@BeforeClass
	public void setMemoryCleaner(MemoryCleaner memoryCleaner) {
		ctx = ApplicationContextProvider.getIntegrationTestApplicationContext();
	}

	@Test
	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		MemoryCleaner memoryCleaner = new MemoryCleaner();
		memoryCleaner.cleanMemoryFromRedundantCells();
	}
}
