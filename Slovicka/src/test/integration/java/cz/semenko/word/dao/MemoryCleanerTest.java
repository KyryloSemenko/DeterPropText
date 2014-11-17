package test.integration.java.cz.semenko.word.dao;


import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.Config;
import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.dao.DBViewer;
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
	public void testCleanMemoryFromUselessAssociations () throws Exception {
		ApplicationContext ctx = ApplicationContextProvider.getIntegrationTestApplicationContext();
		MemoryCleaner memoryCleaner = ctx.getBean(MemoryCleaner.class);
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		FastMemory memory = ctx.getBean(FastMemory.class);
		DBViewer dbViewer = ctx.getBean(DBViewer.class);
		Config config = ctx.getBean(Config.class);
		config.setMemoryCleaner_lowestCostForLeaving(2);
		
		// Clean database
		memoryCleaner.forgetEverything();
		Long[] testData = memory.getCells("abc".toCharArray());
		knowledge.remember(testData);
		testData = memory.getCells("abc".toCharArray());
		knowledge.remember(testData);
		testData = memory.getCells("abc".toCharArray());
		knowledge.remember(testData);
		
		memoryCleaner.cleanMemoryFromUselessCells();
		Long expectedAssociationsCount = 1L;
		Long expectedCellsCount = 4L;
		
		assertEquals(expectedAssociationsCount, dbViewer.getAssociationsCount());
		assertEquals(expectedCellsCount, dbViewer.getCellsCount());
	}
}
