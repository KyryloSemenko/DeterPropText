package cz.semenko.word.technology.memory.completion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.model.memory.*;
import cz.semenko.word.aware.*;
import cz.semenko.word.database.AbstractDBViewer;
import cz.semenko.word.database.DBconnector;
import cz.semenko.word.persistent.Objects;

public class TextReader {
	static Logger logger = Logger.getLogger(TextReader.class);
	private Connection conn = null;
	//private static PreparedStatement selectAssociationToObjects;
	
	
	private Long synteticProperty;
	
	private AbstractDBViewer db;
	
	public TextReader() {
		try {
			//Get a connection
            conn = DBconnector.getInstance().getConnection();
            db = AbstractDBViewer.getInstance();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Čte soubor a ukládá sloky do DB. Spojuje sloky se syntetickymi vlastnosti. Behem cteni naleza nove sloky
	 * a uklada je do DB.
	 * @param fileName
	 * @param synPropArr
	 * @throws Exception
	 */
	public void storeFile(String fileName) throws Exception {
		int numChars = Config.getInstance().getDataProvider_numCharsReadsFromInput();
		
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
		Knowledge knowledge = Knowledge.getInstance();
		Memory mem = Memory.getInstance();
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
			Long[] objects = mem.getObjects(cbuf);
			// TODO odstranit breakpoint
			System.out.println(cbuf);
			// rekurzivne zpracujeme masiv objects, upravime vlastnost COST v tabulce ASSOCIATIONS
			// a vytvorime nove objekty. Pravidla vytvareni novych objektu budou stanovene ve zvlastni tride.
			knowledge.remember(objects);
		}
		// Ulozit zbytek knowledge do souboru
		if (Config.getInstance().isKnowledge_saveThoughtsToFile()) {
			Vector<Thought> thoughts = knowledge.getThoughts();
			ThoughtsSaver.getInstance().saveThoughts(thoughts);
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
	/**
	 * Nefunguje tak jak chci.
	 * @param fileName
	 * @throws Exception
	 
	public void storeFile(String fileName, int syntProp) throws Exception {
		this.synteticProperty = getNewSyntheticObject(syntProp);
		
		FileReader fileReader = new FileReader(new File(fileName));
		BufferedReader in = new BufferedReader(fileReader);
		int next;
		Long charID = null;
		Long srcID = null;
		Long tgtID = null;
		Long tempID = null; 
		StringBuffer buf = new StringBuffer();
		boolean isFirstPass = true;
		while ((next = in.read()) != -1) {
			in.mark(1);
			charID = getCharID(next);
			buf.append((char)next);
			if (isFirstPass) {
				srcID = charID;
				srcLabel :
				while ((next = in.read()) != -1) {
					charID = getCharID(next);
					buf.append((char)next);
					if ((tempID = getObject(srcID, charID)) != null) {
						srcID = tempID;
						in.mark(1);
						continue srcLabel;
					} else {
						in.reset();
						buf.deleteCharAt(buf.length() - 1);
						break;
					}
				}
				isFirstPass = false;
				continue;
			} else {
				tgtID = charID;
				tgtLabel :
				while ((next = in.read()) != -1) {
					charID = getCharID(next);
					buf.append((char)next);
					if ((tempID = getObject(tgtID, charID)) != null) {
						tgtID = tempID;
						in.mark(1);
						continue tgtLabel;
					} else {
						in.reset();
						buf.deleteCharAt(buf.length() - 1);
						break;
					}
				}
				if ((tempID = getObject(srcID, tgtID)) != null) {
					srcID = tempID;
					continue;
				} else {
					srcID = getNewObject(srcID, tgtID, buf);
					System.out.println(buf.toString());
					if ("тны".equals(buf.toString())) {
						System.out.println();
					}
					buf.delete(0, buf.length());
					in.reset();
					isFirstPass = true;
				}
			}
		}
	}
	*/
	
	
	/*
	private long getWordID(String word) throws Exception {
		long result = 0L;
		selectWordID.setString(1, word);
		ResultSet rs = selectWordID.executeQuery();
		if (rs.next() == true) {
			return rs.getLong("id");
		}
		return result;
	}*/
	/*
	private long saveWord(String word) throws Exception {
		String insert = "INSERT INTO objects (src) VALUES('" + word + "')";
		Statement statement = conn.createStatement();
		statement.execute(insert,	Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		return rs.getLong(1);
	}*/
}