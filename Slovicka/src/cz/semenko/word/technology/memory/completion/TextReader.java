package cz.semenko.word.technology.memory.completion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.aware.ThoughtsSaver;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Object that read and analyse text from a file.<br>
 * @author Kyrylo Semenko
 *
 */
public class TextReader {
	static Logger logger = Logger.getLogger(TextReader.class);
	private FastMemory fastMemory;
	private Knowledge knowledge;
	private ThoughtsSaver thoughtsSaver;
	private Config config;

	/**
	 * <p>Empty constructor for TextReader.</p>
	 */
	public TextReader() {
		
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}

	public void setThoughtsSaver(ThoughtsSaver thoughtsSaver) {
		this.thoughtsSaver = thoughtsSaver;
	}

	public void setFastMemory(FastMemory memory) {
		this.fastMemory = memory;
	}

	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}
	/**
	 * Čte soubor a ukládá sloky do DB. Spojuje sloky se syntetickymi vlastnosti. Behem cteni naleza nove sloky
	 * a uklada je do DB.
	 *
	 * @param fileName a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public void storeFile(String fileName) throws Exception {
		int numChars = config.getDataProvider_numCharsReadsFromInput();
		
		FileReader fileReader = new FileReader(new File(fileName));
		InputStreamReader inputStreamReader = null;
		if (fileReader.getEncoding().equalsIgnoreCase("utf-8") == false) {
			logger.error("File " + fileName + " may have not UTF-8 encoding!");
			fileReader.close();
			fileReader = null;
			inputStreamReader = new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8"));
		}
		BufferedReader in = new BufferedReader(fileReader == null?inputStreamReader:fileReader);
		/* Cist blok textu */
		char[] cbuf = new char[numChars];
		int off = 0;
		int len = numChars;
		while ((len = in.read(cbuf, off, len)) != -1) {
			// ziskame id vsech znaku
			Long[] cells = fastMemory.getCells(cbuf);
			// TODO odstranit breakpoint
			System.out.println(cbuf);
			// rekurzivne zpracujeme masiv cells, upravime vlastnost COST v tabulce ASSOCIATIONS
			// a vytvorime nove objekty. Pravidla vytvareni novych objektu budou stanovene ve zvlastni tride.
			knowledge.remember(cells);
		}
		// Ulozit zbytek knowledge do souboru
		if (config.isKnowledge_saveThoughtsToFile()) {
			Vector<Thought> thoughts = knowledge.getThoughts();
			thoughtsSaver.saveThoughts(thoughts);
		}
		// Zde by mohli odeznivat SyntheticProperties jako myslenky na pozadi. Donastavi se priorita a budou
		// vyvozeny zavery.
		if (fileReader != null) {
			fileReader.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
	}
}
