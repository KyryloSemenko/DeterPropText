package cz.semenko.word.viewer;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * GUI front end to handle Slovicka data.
 * @author k
 *
 */
public class Main {
	public static Logger logger = Logger.getRootLogger();
	private static ApplicationContext applicationContext;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			registerSpringService();
			//ViewerGUI window = ViewerGUI.getInstance();
			ViewerGUI viewerGUI = (ViewerGUI)applicationContext.getBean("viewerGUI");
			viewerGUI.open();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Zaregistrovat Spring FW kontejner
	 */
	private static void registerSpringService() {
		applicationContext = 
			new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
	}

}
