/**
 * Test cases for cz.semenko.word.aware package
 */
package test.java.cz.semenko.word.aware;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
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
		FastMemory memory = ctx.getBean(FastMemory.class);
		try {
			Long[] testData = memory.getObjects(new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g'});
			knowledge.remember(testData);
			// TODO dopsat tento test
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
