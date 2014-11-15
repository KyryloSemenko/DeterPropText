package test.integration.java.cz.semenko.word.dao;


import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.Config;
import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.sleep.MemoryCleaner;
import cz.semenko.word.technology.memory.fast.FastMemory;

public class MemoryCleanerTest extends TestCase {
	public static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

	/**
	 * Clean database,<br>
	 * insert test data,<br>
	 * clean memory<br>
	 * and compare result with expected result.
	 * @throws Exception
	 */
	@Test
	public void testCleanMemoryFromRedundantAssociations () throws Exception {
		ApplicationContext ctx = ApplicationContextProvider.getIntegrationTestApplicationContext();
		MemoryCleaner memoryCleaner = ctx.getBean(MemoryCleaner.class);
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		FastMemory memory = ctx.getBean(FastMemory.class);
		Config config = ctx.getBean(Config.class);
		config.setMemoryCleaner_lowestCostForLeaving(10);
		
		// Clean database
		memoryCleaner.forgetEverything();
		Long[] testData = memory.getCells("abcabcabcabcabcabcabcabcabcabc".toCharArray());
		knowledge.remember(testData);
		
		memoryCleaner.cleanMemoryFromRedundantCells();
		String expected = "";
		
		assertEquals(expected, "TODO");
	}
}
