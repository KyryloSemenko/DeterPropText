package cz.semenko.word.technology.memory.completion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.viewer.DBViewer;

public class MemoryCleaner {
	static Logger logger = Logger.getLogger(DataProvider.class);
	private Connection conn = null;
	private PreparedStatement selectLowCostOb;
	private DBViewer dbViewer;
	
	public MemoryCleaner() {
		try {
			//Get a connection
            conn = new DBconnector().getDerbyConnection();
            dbViewer = new DBViewer();
            selectLowCostOb = dbViewer.selectLowCostOb;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public int cleanFromLowCostObjects(int filteredType) throws Exception {
		int result = 0;
		selectLowCostOb.setInt(1, filteredType);
		ResultSet idRs = selectLowCostOb.executeQuery();
		Vector<Long> idVector = new Vector<Long>();
		while(idRs.next()) {
			idVector.add(idRs.getLong("id"));
		}
		for (Iterator<Long> iter = idVector.iterator(); iter.hasNext(); ) {
			String obStr = dbViewer.getSrc(iter.next());
			System.out.println(obStr);
		}
		//TODO smazat objekt z tabulek
		dbViewer.deleteObjects(idVector);
		try {
			new DBconnector().finalize();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		MemoryCleaner mc = new MemoryCleaner();
		try {
			mc.cleanFromLowCostObjects(2);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
