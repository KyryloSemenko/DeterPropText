package test.integration.java.cz.semenko.word.dao;


import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.sleep.MemoryCleaner;

public class MemoryCleanerTest extends TestCase {
	public static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

	@Test
	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		ApplicationContext ctx = ApplicationContextProvider.getIntegrationTestApplicationContext();
		MemoryCleaner memoryCleaner = ctx.getBean(MemoryCleaner.class);
		memoryCleaner.cleanMemoryFromRedundantCells();
	}
}
