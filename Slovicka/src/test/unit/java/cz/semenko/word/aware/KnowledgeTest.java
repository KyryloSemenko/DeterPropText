/**
 * Test cases for cz.semenko.word.aware package
 */
package test.unit.java.cz.semenko.word.aware;

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
	 * Při čtení aplikace předpovídá budoucí text. <br>
	 * During reading application predicts future text. <br>
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
	 * Při čtení textu, COST associations roste jen do výše definované v konfiguraci.<br>
	 * During reading of the text, association's COST increase up to configuration parameter value only.<br>
	 */
	@Test
	public final void testCostIncrease() {
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		FastMemory memory = ctx.getBean(FastMemory.class);
		Config config = ctx.getBean(Config.class);
		config.setKnowledge_cellsCreationDepth(5);
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(5);
		
		try {
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
