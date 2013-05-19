package cz.semenko.word.viewer;

import org.eclipse.swt.custom.StyledText;

import cz.semenko.word.database.DBViewer;

public class Model {
	
	/** Pod spravou Spring FW */
	private DBViewer dbViewer;
	
	public Model(DBViewer dbViewer) {
		this.dbViewer = dbViewer;
	}
	
	
	/**
	 * Dohleda src objektu
	 * @param Jedno nebo vice ID objektu oddelenych strednikem nebo novym radkem
	 * @return Text pro objekt nebo objekty z parametru
	 * @throws Exception
	 */
	public String getSourceToObjectsId (StyledText text) throws Exception {		
		StringBuffer resultBuff = new StringBuffer();
		resultBuff.append("****************" + System.getProperty("line.separator"));
		for (int i = 0; i < text.getLineCount(); i++) {
			resultBuff.append(dbViewer.getSrc(text.getLine(i)));
			resultBuff.append(System.getProperty("line.separator"));
			resultBuff.append("***" + System.getProperty("line.separator"));
		}
		return resultBuff.toString();
	}
}
