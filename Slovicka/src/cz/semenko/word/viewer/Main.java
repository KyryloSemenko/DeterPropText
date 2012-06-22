package cz.semenko.word.viewer;

import org.apache.log4j.Logger;
/**
 * GUI front end to handle Slovicka data.
 * @author k
 *
 */
public class Main {
	public static Logger logger = Logger.getRootLogger();
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ViewerGUI window = ViewerGUI.getInstance();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

}
