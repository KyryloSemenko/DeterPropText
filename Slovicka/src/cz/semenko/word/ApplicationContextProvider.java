package cz.semenko.word;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * <p>This object create, manage and provide application Spring beans contexts for different environments.
 * 
 * @author Kyrylo Semenko
 *
 */
public class ApplicationContextProvider implements ApplicationContextAware {
	private static ApplicationContext ctx = null;

	/**
	 *
	 * @return a {@link org.springframework.context.ApplicationContext} object.
	 */
	public static ApplicationContext getDevApplicationContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/spring/dev/applicationContext-all.xml");
		}
		return ctx;
	}
	
	/**
	 *
	 * @return a {@link org.springframework.context.ApplicationContext} object to unit tests.
	 */
	public static ApplicationContext getUnitTestApplicationContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/spring/test/unit/applicationContext-all-unit-test.xml");
		}
		return ctx;
	}
	
	/**
	 * Application context for integration test environment<br><br>
	 * <img src="doc-files/IntegrationTestApplicationContext.png" /> 
	 * @return a {@link org.springframework.context.ApplicationContext}
	 */
	public static ApplicationContext getIntegrationTestApplicationContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/spring/test/integration/applicationContext-all-integration-test.xml");
		}
		return ctx;
	}

	/** {@inheritDoc} */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		ApplicationContextProvider.ctx = ctx;
	}

	/**
	 * @return a {@link org.springframework.context.ApplicationContext} object if exists. Otherwise return null.
	 * To create new {@link org.springframework.context.ApplicationContext} instance please use one of specific methods, like {@link cz.semenko.word.ApplicationContextProvider#getIntegrationTestApplicationContext}
	 */
	public static ResourceLoader getApplicationContext() {
		return ctx;
	}

}
