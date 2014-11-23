package cz.semenko.word.dao;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * <p>HibernateSessionFactory class initialises connection to database.</p>
 *
 * @author Kyrylo Semenko
 */
public class HibernateSessionFactory {
	
	/**
	 * Empty constructor for class HibernateSessionFactory
	 */
	public HibernateSessionFactory() {}

	private SessionFactory sessionFactory;
	private String dbUrl;

	/** Create the SessionFactory from hibernate.cfg.xml */
	private void buildSessionFactory() {
		ServiceRegistry serviceRegistry =  null;
		try {
			Configuration configuration = new Configuration();
			configuration.setProperty("hibernate.connection.url", dbUrl);
			configuration.setProperty("hibernate.hbm2ddl.auto", "create");
			configuration.setProperty("hibernate.hbm2ddl.import_files", "cz\\semenko\\word\\sql\\createTables.sql");
		    configuration.configure();
		    serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		    
		    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed. " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * <p>Getter for the field <code>sessionFactory</code>.</p>
	 *
	 * @return a {@link org.hibernate.SessionFactory} object.
	 */
	public SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * @return the {@link String}<br>
	 * See {@link HibernateSessionFactory#dbUrl}
	 */
	public String getDbUrl() {
		return dbUrl;
	}

	/**
	 * @param dbUrl the {@link String} to set<br>
	 * See {@link HibernateSessionFactory#dbUrl}
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

}
