package cz.semenko.word;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import cz.semenko.word.technology.memory.completion.TextReader;
/**
 * Main class to run text analyser.
 * @author Kyrylo Semenko
 *
 */
public class Main {
	static Logger logger = Logger.getRootLogger();
	private static ApplicationContext applicationContext;

	/**
	 * @param args - void
	 * TODO: vytvorit GUI
	 */
	public static void main(String[] args) {
		registerSpringService();
		try {
			long startTime = System.currentTimeMillis();
			// Komponenta pod spravou Spring FW
			TextReader textReader = (TextReader)applicationContext.getBean("textReader");
			File dir = new File("c:\\temp\\deeplearning");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				System.out.println("**********************");
				System.out.println(System.currentTimeMillis());
				System.out.println(files[i].getPath());
				System.out.println("File size: " + files[i].getTotalSpace());
				textReader.storeFile(files[i].getPath());
				System.out.println(System.currentTimeMillis());
				System.out.println("**********************");
			}
			System.out.println("Celkem sekund: " + ((System.currentTimeMillis() - startTime)/1000));
		} catch (Exception e) {
			e.getLocalizedMessage();
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
//	
//	/**
//	 * @param args - void
//	 * TODO: vytvorit GUI
//	 */
//	public static void main(String[] args) {
//		registerSpringService();
//		try {
//			long startTime = System.currentTimeMillis();
//			// Komponenta pod spravou Spring FW
//			TextReader textReader = (TextReader)applicationContext.getBean("textReader");
//			File dir = new File("Data");
//			File[] files = dir.listFiles();
//			for (int i = 0; i < files.length; i++) {
//				System.out.println("**********************");
//				System.out.println(System.currentTimeMillis());
//				System.out.println(files[i].getPath());
//				System.out.println("File size: " + files[i].getTotalSpace());
//				textReader.storeFile(files[i].getPath());
//				System.out.println(System.currentTimeMillis());
//				System.out.println("**********************");
//			}
//			System.out.println("Celkem sekund: " + ((System.currentTimeMillis() - startTime)/1000));
//		} catch (Exception e) {
//			e.getLocalizedMessage();
//			e.printStackTrace();
//			logger.error(e.getMessage(), e);
//		}
//	}

	/**
	 * Initialise Spring container context.
	 */
	private static void registerSpringService() {
		applicationContext = ApplicationContextProvider.getDevApplicationContext();
	}

}
