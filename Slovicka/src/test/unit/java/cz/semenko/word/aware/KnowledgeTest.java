/**
 * Test cases for cz.semenko.word.aware package
 */
package test.unit.java.cz.semenko.word.aware;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.Config;
import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * <p>Test for {@link cz.semenko.word.aware.Knowledge} class</p>
 *
 * @author Kyrylo Semenko
 */
public class KnowledgeTest {
	public static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
	static ApplicationContext ctx;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ctx = ApplicationContextProvider.getUnitTestApplicationContext();
	}

	/**
	 * Application predicts future text during a reading. <br>
	 */
	@Test
	public final void testRemember() {
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		FastMemory memory = ctx.getBean(FastMemory.class);
		try {
			Long[] testData = memory.getCells("abcdefg".toCharArray());
			knowledge.remember(testData);
			testData = memory.getCells("abcdefg".toCharArray());
			// Jakmile Knowledge precte pismeno a, ihned nabidne odhadovane pokracovani.
			// ab, ab-cd, abcd-ef, razene dle COST
			knowledge.remember(testData);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * During a reading of a text, associations COST increase up to configuration parameter value only.<br>
	 */
	@Test
	public final void testCostIncrease() {
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		FastMemory memory = ctx.getBean(FastMemory.class);
		Config config = ctx.getBean(Config.class);
		config.setKnowledge_relateThoughtsUpToCellType(5);
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(2);
		try {
			Long[] testData = memory.getCells("abcdefg".toCharArray());
			knowledge.remember(testData);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}

}
