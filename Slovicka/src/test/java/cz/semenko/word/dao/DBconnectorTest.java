package test.java.cz.semenko.word.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.dao.DBconnector;

/**
 * <p>DBconnectorTest class. Tests for {@link cz.semenko.word.dao.DBconnector}
 *
 * @author Kyrylo Semenko
 */
public class DBconnectorTest {
	static ApplicationContext ctx;

	/**
	 * <p>Context set up</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@Before
	public void setUp() throws Exception {
		ctx = ApplicationContextProvider.getApplicationContext();
	}

	/**
	 * <p>Shutdown DB server</p>
	 *
	 * @throws java.lang.Exception if any.
	 */
	@After
	public void tearDown() throws Exception {
		DBconnector connector = ctx.getBean(DBconnector.class);
		connector.stopConnection();
	}

	/**
	 * <p>Start DB server and try to call select</p>
	 */
	@Test
	public final void testDBconnector() {
		DBconnector connector = ctx.getBean(DBconnector.class);
		assertNotNull("DBconnector is null", connector);
		Connection connection = connector.getConnection();
		assertNotNull("DBconnector does not return Connection object", connection);
		try {
			connection.createStatement().executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	

	/**
	 * <p>Shutdown DB server and test it state</p>
	 */
	@Test
	public final void testCloseDBconnector() {
		DBconnector connector = ctx.getBean(DBconnector.class);
		connector.stopConnection();
		try {
			connector.getConnection().createStatement().executeQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
		} catch (SQLException e) {
			return;
		}
		fail("Connection does not closed!");
	}

}
