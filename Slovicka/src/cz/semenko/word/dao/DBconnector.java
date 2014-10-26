package cz.semenko.word.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import cz.semenko.word.ApplicationContextProvider;
import cz.semenko.word.Config;

/**
 * Provide a connection to database defined in Spring context.
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
	
	/** Constructor */
	public DBconnector(Config config, String databaseName) {
		// "jdbc:derby:target/database;create=true"
		String dbSystem = "jdbc:derby:";
		String userHome = System.getProperty("user.home");
		String applicationName = config.getApplication_name();
		dbPath = userHome + System.getProperty("file.separator") + applicationName + System.getProperty("file.separator") + databaseName;
		dbUrl = dbSystem + dbPath + ";create=true";
	}

	/**
	 * @see test.java.cz.semenko.word.dao.DBconnectorTest#testGetConnection
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		Connection connection = null;
		if (connection == null) {
			connection = dataSource.getConnection();
		}
		return connection;
	}
	
	/** Apply initialisation script */
	public void createDatabaseStructure() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		ResourceLoader resourceLoader = ApplicationContextProvider.getApplicationContext();
		String sqlResourcePath = "cz\\semenko\\word\\sql\\createTables.sql";
		boolean continueOnError = false;
		JdbcTestUtils.executeSqlScript(jdbcTemplate, resourceLoader, sqlResourcePath, continueOnError); 
	}
	
	/** Place of database on disk */
	public String getDbPath() {
		return dbPath;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

}
