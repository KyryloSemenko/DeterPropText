package cz.semenko.word;

import java.io.File;
import java.sql.Connection;
import java.sql.Time;

import org.apache.log4j.Logger;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.technology.memory.completion.TextReader;
/**
 * Main class to run text analyze.
 * @author k
 *
 */
public class Main {
	static Logger logger = Logger.getRootLogger();

	/**
	 * @param args - void
	 * TODO: vytvorit GUI
	 */
	public static void main(String[] args) {
		try {
			long startTime = System.currentTimeMillis();
			TextReader dp = new TextReader();
			File dir = new File("Data");
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				System.out.println("**********************");
				System.out.println(System.currentTimeMillis());
				System.out.println(files[i].getPath());
				System.out.println("File size: " + files[i].getTotalSpace());
				dp.storeFile(files[i].getPath());
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

}
