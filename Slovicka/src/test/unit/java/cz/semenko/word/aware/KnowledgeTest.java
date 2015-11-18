/**
 * Test cases for cz.semenko.word.aware package
 */
package test.unit.java.cz.semenko.word.aware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.technology.memory.completion.TextReader;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * <p>Test for {@link cz.semenko.word.aware.Knowledge} class</p>
 *
 * @author Kyrylo Semenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/test/unit/applicationContext-all-unit-test.xml"})
@DirtiesContext
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true) 
@Transactional
public class KnowledgeTest {
	public static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());
	
	@Autowired
	private ApplicationContext ctx;
	
	@Autowired
	private Knowledge knowledge;
	
	@Autowired
	private FastMemory memory;
	
	@Autowired
	private Config config;
	
	@Autowired
	private TextReader textReader;
	
	@Autowired
	private DataSourceTransactionManager transactionManager;

	/**
	 * Application saves Cells and Associations to depth 100 defined in configuration <br>
	 */
	@Test
	@Transactional
	@Rollback
	public void testRememberToDepth100() {
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(100);
		config.setDbViewer_maxTextLengthToSave(100);
		
		try {
			config.setKnowledge_relateOnlyCellsOfTheSameTypes(false);
			knowledge.remember(memory.getCells("abrakadabra"));
			knowledge.remember(memory.getCells("abrakadabry"));
			knowledge.remember(memory.getCells("angora"));
			knowledge.remember(memory.getCells("gory"));
			
			List<Thought> resultA = knowledge.getThoughts();
			assertTrue("Thought contains word", "abrakadabraabrakadabryangoragory".equals(resultA.get(0).getActiveCell().getSrc()));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	/**
	 * Application saves Cells and Associations to depth 5 defined in configuration <br>
	 */
	@Test
	@Transactional
	@Rollback
	public void testRememberToDepth5() {
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(5);
		config.setDbViewer_maxTextLengthToSave(100);
		
		try {
			config.setKnowledge_relateOnlyCellsOfTheSameTypes(false);
			knowledge.remember(memory.getCells("abrakadabra"));
			knowledge.remember(memory.getCells("abrakadabry"));
			knowledge.remember(memory.getCells("angora"));
			knowledge.remember(memory.getCells("gory"));
			
			List<Thought> resultA = knowledge.getThoughts();
			assertEquals("Thought contains word", "abrakada", resultA.get(0).getActiveCell().getSrc());
			assertTrue("Thought contains word", "bra".equals(resultA.get(1).getActiveCell().getSrc()));
			assertTrue("Thought contains word", "abrakada".equals(resultA.get(2).getActiveCell().getSrc()));
			assertTrue("Thought contains word", "bryang".equals(resultA.get(3).getActiveCell().getSrc()));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	/**
	 * Application predicts future text while read <br>
	 */
	@Test
	@Transactional
	@Rollback
	public void testSuggest() {
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(4);
		config.setDbViewer_maxTextLengthToSave(100);
		try {
			knowledge.remember(memory.getCells("abrakadabra"));
			
			List<Cell> resultA = knowledge.suggest(memory.getCells("a"));
			assertTrue("Suggestion result", "abrakadabra".equals(resultA.get(0).getSrc()));
			assertTrue("Suggestion result", "abrakada".equals(resultA.get(1).getSrc()));
			assertTrue("Suggestion result", "abra".equals(resultA.get(2).getSrc()));
			assertTrue("Suggestion result", "ab".equals(resultA.get(3).getSrc()));
			
			List<Cell> resultB = knowledge.suggest(memory.getCells("b"));
			assertTrue("Suggestion result. First is newer.", "bry".equals(resultB.get(0).getSrc()));
			assertTrue("Suggestion result. Second is older", "bra".equals(resultB.get(1).getSrc()));
			assertTrue("Suggestion result.", "br".equals(resultB.get(2).getSrc()));
			
			// Jakmile Knowledge precte pismeno a, ihned nabidne odhadovane pokracovani.
			// ab, ab-cd, abcd-ef, razene dle COST
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	/**
	 * Application saves Cells and Associations to depth 16 characters <br>
	 */
	@Test
	// TODO fix transactionManager
	@Transactional
	@Rollback
	public void testRememberTextFromFile() {
		config.setCellsCreationDecider_createNewCellsToAllPairsDepth(16);
		config.setDbViewer_maxTextLengthToSave(100);
		
		try {
			config.setKnowledge_relateOnlyCellsOfTheSameTypes(false);
			
			textReader.storeFile("Data\\text.txt");
			
			List<Thought> resultA = knowledge.getThoughts();
			assertEquals("Thought contains word", "abrakada", resultA.get(0).getActiveCell().getSrc());
			assertTrue("Thought contains word", "bra".equals(resultA.get(1).getActiveCell().getSrc()));
			assertTrue("Thought contains word", "abrakada".equals(resultA.get(2).getActiveCell().getSrc()));
			assertTrue("Thought contains word", "bryang".equals(resultA.get(3).getActiveCell().getSrc()));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
}
