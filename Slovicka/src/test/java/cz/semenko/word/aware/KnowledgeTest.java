/**
 * Test cases for cz.semenko.word.aware package
 */
package test.java.cz.semenko.word.aware;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.aware.Knowledge;

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
		ctx = ApplicationContextProvider.getTestApplicationContext();
		setUpDataBase();
	}

	/**
	 * Set up connection to DB server, create tables and constraints
	 */
	private static void setUpDataBase() throws Exception {
//		Resource resource = ctx.getResource("classpath:cz/semenko/word/sql/createTables.sql");
//		JdbcTestUtils.executeSqlScript(new JdbcTemplate((DataSource)ctx.getBean("dataSource")), resource, false);
		
		//LazyConnectionDataSourceProxy dbProxy = (LazyConnectionDataSourceProxy)ctx.getBean("database.dataSource");
	}

	/**
	 * Test method for {@link cz.semenko.word.aware.Knowledge#remember(java.lang.Long[])}.
	 */
	@Test
	public final void testRemember() {
		Knowledge knowledge = ctx.getBean(Knowledge.class);
		Vector<Long> testData = new Vector<Long>();
		testData.add(1L);		
		testData.add(2L);		
		testData.add(3L);		
		Long[] inputObjects = testData.toArray(new Long[0]);
		try {
			knowledge.remember(inputObjects);
			// TODO dopsat tento test
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
