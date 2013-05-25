package cz.semenko.word.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * <p>HibernateUtil class.</p>
 *
 * @author k
 * @version $Id: $Id
 */
public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
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
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
