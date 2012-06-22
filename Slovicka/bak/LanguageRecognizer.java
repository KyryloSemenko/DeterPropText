package cz.semenko.word.technology.memory.completion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.aware.Thought;

public class LanguageRecognizer {
	static Logger logger = Logger.getRootLogger();
	private static PreparedStatement getSomeChild;
	private static Connection con;
	private Map<Long, BufferedWriter> langFiles = new TreeMap<Long, BufferedWriter>();
	private Map<Long, String> langsProp = new TreeMap<Long, String>();
	private String langIds = "";
	private File testFile;
	
	public LanguageRecognizer(File file) throws Exception {
		try {
            con = new DBconnector().getDerbyConnection();
            getSomeChild = con.prepareStatement("SELECT id " +
            		"FROM associations WHERE src_id IN " +
            		"(SELECT id FROM objects WHERE " +
            		"src LIKE ?)");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		testFile = file;
		getLangsProp();
	}

	private void getLangsProp() throws Exception {
		String sql = "SELECT id, src FROM objects WHERE type > " +
				SyntheticProperties.Thought + " AND type < " +
				SyntheticProperties.Thought;
		ResultSet res = con.createStatement().executeQuery(sql);
		while (res.next()) {
			Long id = res.getLong("id");
			String src = res.getString("src");
			langsProp.put(id, src);
			langIds = langIds + id + ", ";			
		}
		// Create non-recognized.txt
		File nonrec = new File(testFile.getParentFile(), "non-recognized.txt");
		/*if (nonrec.exists()) {
			throw new IOException("File exists. File - " + nonrec.getPath());
		}*/
		//langsProp.put(0L, "non-recognized");
		BufferedWriter out = new BufferedWriter(new FileWriter(nonrec));
		langFiles.put(0L, out);
		langIds = langIds.substring(0, langIds.length() - 2);
	}

	public void testFileToLanguages() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(testFile));
		StringBuffer buf = new StringBuffer();
		int next;
		while ((next = reader.read()) != -1) {
			buf.append((char)next);
			if (hasChildrens(buf)) {
				continue;
			} else {
				saveToLangFiles(buf.substring(0, buf.length()-1));
				buf.delete(0, buf.length()-1);
			}
		}
		closeFiles();
	}
	private void saveToLangFiles(String str) throws Exception {
		String sql = "SELECT tgt_id FROM associations WHERE src_id = " +
				"(SELECT id FROM objects WHERE src LIKE '" + str + "')" +
				" AND tgt_id IN (" + langIds + ")";
		ResultSet rs = con.createStatement().executeQuery(sql);
		Vector<Long> langs = new Vector<Long>();
		while(rs.next()) {
			langs.add(rs.getLong("tgt_id"));
		}
		if (langs.size() == 1) {
			Long id = langs.firstElement();
			addToFile(id, str);
		} else if (langs.size() == 0) {
			addToFile(0L, str); // Add to non-recognized.txt
		}
	}
	private void addToFile(Long id, String str) throws Exception {
		if (langFiles.containsKey(id) == false) {
			File langFile = new File(testFile.getParentFile(), langsProp.get(id));
			/*if (langFile.exists()) {
				throw new IOException("File exists. File - " + langFile.getPath());
			}*/
			BufferedWriter writer = new BufferedWriter(new FileWriter(langFile));
			langFiles.put(id, writer);
		}
		BufferedWriter writer = langFiles.get(id);
		writer.append(str + "|");
	}

	private void closeFiles() throws Exception {
		for (Iterator<BufferedWriter> iter = langFiles.values().iterator(); iter.hasNext(); ) {
			BufferedWriter writer = iter.next();
			writer.flush();
			writer.close();
		}
	}
	private boolean hasChildrens(StringBuffer buf) throws Exception {
		getSomeChild.setString(1, buf.toString());
		long from = new Date().getTime();
		ResultSet rs = getSomeChild.executeQuery();
		System.out.println("" + (new Date().getTime() - from));
		rs.next();
		if (rs.next() == false) {
			return false;
		}
		return true;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String testFileName = "Data/test.txt";
			LanguageRecognizer rec = new LanguageRecognizer(new File(testFileName));
			rec.testFileToLanguages();
			new DBconnector().finalize();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

}
