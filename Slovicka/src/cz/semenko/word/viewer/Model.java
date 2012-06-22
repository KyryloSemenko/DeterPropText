package cz.semenko.word.viewer;

import java.sql.SQLException;

import org.eclipse.swt.custom.StyledText;

import cz.semenko.word.database.AbstractDBViewer;

public class Model {
	/** Single instance */
	private static Model instance = null;
	
	/** U singletona je prazdny konstruktor */
	private Model() {}
	
	public static Model getInstance() {
		if (instance == null) {
			synchronized(Model.class) {
				Model inst = instance;
				if (inst == null) {
					instance = new Model();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Dohleda src objektu
	 * @param Jedno nebo vice ID objektu oddelenych strednikem nebo novym radkem
	 * @return Text pro objekt nebo objekty z parametru
	 * @throws Exception
	 */
	public String getSourceToObjectsId (StyledText text) throws Exception {		
		StringBuffer inputBuff = new StringBuffer();
		StringBuffer resultBuff = new StringBuffer();
		resultBuff.append("****************" + System.getProperty("line.separator"));
		AbstractDBViewer viewer = AbstractDBViewer.getInstance();
		for (int i = 0; i < text.getLineCount(); i++) {
			resultBuff.append(viewer.getSrc(text.getLine(i)));
			resultBuff.append(System.getProperty("line.separator"));
			resultBuff.append("***" + System.getProperty("line.separator"));
		}
		return resultBuff.toString();
	}
}
