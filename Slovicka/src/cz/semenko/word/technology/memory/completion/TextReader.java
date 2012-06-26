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

public class TextReader {
	static Logger logger = Logger.getLogger(TextReader.class);
	// Objekty doda Spring FW
	private FastMemory fastMemory;
	private Knowledge knowledge;
	private ThoughtsSaver thoughtsSaver;
	private Config config;

	public TextReader() {
		
	}
	
	/**
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @param thoughtsSaver the thoughtsSaver to set
	 */
	public void setThoughtsSaver(ThoughtsSaver thoughtsSaver) {
		this.thoughtsSaver = thoughtsSaver;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setFastMemory(FastMemory memory) {
		this.fastMemory = memory;
	}
	/**
	 * @param knowledge the knowledge to set
	 */
	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}
	/**
	 * Čte soubor a ukládá sloky do DB. Spojuje sloky se syntetickymi vlastnosti. Behem cteni naleza nove sloky
	 * a uklada je do DB.
	 * @param fileName
	 * @param synPropArr
	 * @throws Exception
	 */
	public void storeFile(String fileName) throws Exception {
		int numChars = config.getDataProvider_numCharsReadsFromInput();
		
		FileReader fileReader = new FileReader(new File(fileName));
		InputStreamReader inputStreamReader = null;
		if (fileReader.getEncoding().equalsIgnoreCase("utf-8") == false) {
			logger.warn("File " + fileName + " may have not UTF-8 encoding!");
			fileReader = null;
			inputStreamReader = new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8"));
		}
		BufferedReader in = new BufferedReader(fileReader == null?inputStreamReader:fileReader);
		/* Cist blok textu */
		char[] cbuf = new char[numChars];
		int off = 0;
		int len = numChars;
		while ((len = in.read(cbuf, off, len)) != -1) {
			/* TODO odstranit
			StringBuffer str = new StringBuffer();
			for (char ch : cbuf) {
				str.append(ch);
			}
			if (str.toString().contains("s n'êtes plus mon am")) {
				System.out.println("Je to tady");
			}*/
			// ziskame id vsech znaku
			Long[] objects = fastMemory.getObjects(cbuf);
			// TODO odstranit breakpoint
			System.out.println(cbuf);
			// rekurzivne zpracujeme masiv objects, upravime vlastnost COST v tabulce ASSOCIATIONS
			// a vytvorime nove objekty. Pravidla vytvareni novych objektu budou stanovene ve zvlastni tride.
			knowledge.remember(objects);
		}
		// Ulozit zbytek knowledge do souboru
		if (config.isKnowledge_saveThoughtsToFile()) {
			Vector<Thought> thoughts = knowledge.getThoughts();
			thoughtsSaver.saveThoughts(thoughts);
		}
		// Zde by mohli odeznivat SyntheticProperties jako myslenky na pozadi. Donastavi se priorita a budou
		// vyvozeny zavery.
	}
	/**
	 * Nauci nachazet napriklad jmena, nebo cislovky, nebo podnet s prisudkem.
	 * @param propertyName
	 */
	public void learnSyntheticProperty(String propertyName) {
		
	}


}