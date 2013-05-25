package cz.semenko.word;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>This object create, manage and provide application Spring beans context
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
	public static ApplicationContext getApplicationContext() {
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext("classpath:/applicationContext-all.xml");
		}
		return ctx;
	}

	/** {@inheritDoc} */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		ApplicationContextProvider.ctx = ctx;
	}
}
