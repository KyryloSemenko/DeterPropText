package cz.semenko.word.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import cz.semenko.word.ApplicationContextProvider;

/**
 * Provide a connection to database defined in Spring context.<br>
 * Class diagram<br>
 * <img src="doc-files/DBconnector.png" />
 * @author Kyrylo Semenko
 *
 */
public class DBconnector {
	static Logger logger = Logger.getLogger(DBconnector.class);
	// Spring managed
	private String dbUrl;
	private DataSource dataSource;
	// Class interface
	private String dbPath;
	
	/** Constructor for unit tests */
	public DBconnector() {
	}

	/**
	 * @see test.java.cz.semenko.word.dao.DBconnectorTest#testGetConnection
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		Connection connection = dataSource.getConnection();
		if (!isTablesExists()) {
			createDatabaseStructure();
		}
		return connection;
	}
	
	/** Apply initialisation script */
	private void createDatabaseStructure() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		ResourceLoader resourceLoader = ApplicationContextProvider.getApplicationContext();
		String sqlResourcePath = "cz\\semenko\\word\\sql\\createTables.sql";
		boolean continueOnError = false;
		JdbcTestUtils.executeSqlScript(jdbcTemplate, resourceLoader, sqlResourcePath, continueOnError); 
	}
	
	/** If tables not exists, return false 
	 * @throws SQLException */
	private boolean isTablesExists() throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			connection.createStatement().executeQuery("SELECT 1 FROM cells");
			connection.createStatement().executeQuery("SELECT 1 FROM associations");
			connection.createStatement().executeQuery("SELECT 1 FROM tables");
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	/** Place of database on disk */
	public String getDbPath() {
		return dbPath;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDbUrl() {
		return dbUrl;
	}

}
