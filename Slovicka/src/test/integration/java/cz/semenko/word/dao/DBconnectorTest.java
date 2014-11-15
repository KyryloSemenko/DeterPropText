package test.integration.java.cz.semenko.word.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.dao.DBconnector;
import cz.semenko.word.sleep.MemoryCleaner;

/**
 * <p>Integration tests for {@link cz.semenko.word.dao.DBconnector}.</p>
 * <p>The class {@link cz.semenko.word.dao.DBconnector} managed creation and using of local database.</p>
 * <p>Database configuration is defined in Spring context.</p>
 *
 * @author Kyrylo Semenko
 */
public class DBconnectorTest {
	static ApplicationContext ctx;

	/**
	 * <p> Set up Spring application context </p>
	 * 
	 * @throws java.lang.Exception if any.
	 */
	@Before
	public void setUp() throws Exception {
		ctx = ApplicationContextProvider.getIntegrationTestApplicationContext();
	}

	/**
	 * After tests
	 */
	@After
	public void tearDown() {
	}

	/**
	 * <p>When database not exists, then the new one is created.</p>
	 */
	@Test
	public final void testGetConnection() {
		DBconnector connector = ctx.getBean(DBconnector.class);
		
		try {
			// Remove data from database
			MemoryCleaner memoryCleaner = ctx.getBean(MemoryCleaner.class);
			memoryCleaner.forgetEverything();
			
			assertNotNull("DBconnector is null", connector);
			Connection connection = connector.getConnection();
			assertNotNull("DBconnector does not return Connection object", connection);
			connection.createStatement().executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
}
