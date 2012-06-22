package cz.semenko.word.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.technology.memory.completion.TextReader;

public class DBconnector {
	static Logger logger = Logger.getLogger(DBconnector.class);
	private static String dbURL;
	private static Connection derbyConnection = null;
	private static DBconnector instance = null;
	
	public static DBconnector getInstance() {
		if (instance == null) {
			synchronized(DBconnector.class) {
				DBconnector inst = instance;
				if (inst == null) {
					instance = new DBconnector();
				}
			}
		}
		return instance;
	}
	
	private DBconnector() {
		;
	}
	
	public Connection getConnection() {
		if (derbyConnection == null) {
			startConnection();
		}
		return derbyConnection;
	}
	
	private void startConnection() {
		try {
			// Start embedded server in different JVM
			//String command = "java -jar /home/k/MyProgs/Derby/db-derby-10.2.2.0-bin/lib/derbyrun.jar server start";
			String command = Config.getInstance().getDbCon_derbyJarServerStart();
			//String command = "ls";
			Process p = Runtime.getRuntime().exec(command);
			String line;
			BufferedReader input = new BufferedReader
		          (new InputStreamReader(p.getInputStream()));
			while (input.ready() == false) {
				Thread.currentThread().sleep(500);
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
			dbURL = Config.getInstance().getDbCon_dbURL();
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            derbyConnection = DriverManager.getConnection(dbURL);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	public void stopConnection() {
		//logger.info("Try to stop DB connection.");
		String command = Config.getInstance().getDbCon_derbyJarServerStop();
		Process p;
		try {
			if (derbyConnection != null && derbyConnection.isClosed() == false) {
				derbyConnection.close();
			}
			p = Runtime.getRuntime().exec(command);
			String line;
			BufferedReader input = new BufferedReader
		          (new InputStreamReader(p.getInputStream()));
			while (input.ready() == false) {
				Thread.currentThread().sleep(500);
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

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}			
	}
	
	protected void finalize() throws Throwable {
		try {
			stopConnection();
		} finally {
			super.finalize();
		}
	}
}
