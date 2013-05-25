package cz.semenko.word.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

/**
 * Provide a connection to Derby database.
 * Start Derby server on separate process execution.
 * You has to call stopConnection() method to shutdown server. 
 * @author Kyrylo Semenko
 *
 */
public class DBconnector {
	static Logger logger = Logger.getLogger(DBconnector.class);
	// Spring managed elements
	private static Connection derbyConnection = null;
	private String dbURL;
	private String derbyJarServerStart;
	private String derbyJarServerStop;
	
	/** Empty constructor */
	public DBconnector() {}

	public Connection getConnection() {
		if (derbyConnection == null) {
			startConnection();
		}
		return derbyConnection;
	}
	
	/**
	 * Start Derby server in separate process
	 */
	private void startConnection() {
		try {
			logger.info("Start Derby process");
			Process p = Runtime.getRuntime().exec(getDerbyJarServerStart());
			String line;
			BufferedReader input = new BufferedReader
		          (new InputStreamReader(p.getInputStream()));
			while (input.ready() == false) {
				Thread.currentThread();
				Thread.sleep(500);
			}
			while (input.ready() && (line = input.readLine()) != null) {
				logger.info(line);
			}
			input.close();
			BufferedReader errors = new BufferedReader
				(new InputStreamReader(p.getErrorStream()));
			while (errors.ready() && (line = errors.readLine()) != null) {
				logger.error(line);
			}
			errors.close();
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            derbyConnection = DriverManager.getConnection(dbURL);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Shutdown Derby DB instance
	 */
	public void stopConnection() {
		Process p;
		try {
			if (derbyConnection != null && derbyConnection.isClosed() == false) {
				derbyConnection.close();
			}
			p = Runtime.getRuntime().exec(getDerbyJarServerStop());
			String line;
			BufferedReader input = new BufferedReader
		          (new InputStreamReader(p.getInputStream()));
			while (input.ready() == false) {
				Thread.currentThread();
				Thread.sleep(500);
			}
			while (input.ready() && (line = input.readLine()) != null) {
				logger.info(line);
			}
			input.close();
			BufferedReader errors = new BufferedReader
				(new InputStreamReader(p.getErrorStream()));
			while (errors.ready() && (line = errors.readLine()) != null) {
				logger.error(line);
			}
			errors.close();
			
			logger.info("Shutdown Derby DB connection");

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}			
	}

	/**
	 * @return the dbURL
	 */
	public String getDbURL() {
		return dbURL;
	}

	/**
	 * @param dbURL the dbURL to set
	 */
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	/**
	 * @return the derbyJarServerStart
	 */
	public String getDerbyJarServerStart() {
		return derbyJarServerStart;
	}

	/**
	 * @param derbyJarServerStart the derbyJarServerStart to set
	 */
	public void setDerbyJarServerStart(String derbyJarServerStart) {
		this.derbyJarServerStart = derbyJarServerStart;
	}

	/**
	 * @return the derbyJarServerStop
	 */
	public String getDerbyJarServerStop() {
		return derbyJarServerStop;
	}

	/**
	 * @param derbyJarServerStop the derbyJarServerStop to set
	 */
	public void setDerbyJarServerStop(String derbyJarServerStop) {
		this.derbyJarServerStop = derbyJarServerStop;
	}
}
