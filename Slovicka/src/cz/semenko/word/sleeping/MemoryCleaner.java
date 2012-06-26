package cz.semenko.word.sleeping;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.database.DBViewer;
import cz.semenko.word.persistent.Associations;


public class MemoryCleaner {
	
	// Pod spravou Spring FW
	private DBViewer dbViewer;
	private Config config;
	
	public MemoryCleaner() {
		;
	}
	
	/**
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @param dbViewer the dbViewer to set
	 */
	public void setDbViewer(DBViewer dbViewer) {
		this.dbViewer = dbViewer;
	}

	/**
	 * Odstrani asociace, ktere maji nizkou cost, a jejich objekty.
	 * @throws SQLException 
	 */
	public void cleanMemoryFromRedundantObjects() throws SQLException {
		int lowestCostForLeaving = config.getMemoryCleaner_lowestCostForLeaving();
		cleanMemoryFromRedundantAssociations(lowestCostForLeaving);		
	}
	
	private void cleanMemoryFromRedundantAssociations (int lowestCostForLeaving) throws SQLException {
		// Zacina z posledniho objektu
		int numOfAssoc = 200; //TODO add to config file
		Long lastIdAssociationsTable = dbViewer.getLastIdAssociationsTable();
		for (long i = lastIdAssociationsTable; i > 0; i = i - numOfAssoc) {
			System.out.println(i);
			if (i < 200) {
				System.out.println(200);
			}
			List<Associations> associations = dbViewer.getAssociations(
					i-numOfAssoc, 
					i, 
					lowestCostForLeaving);		
			if (associations.size() == 0) {
				continue;
			}
			Vector<Vector<Associations>> levels = new Vector<Vector<Associations>>();
			Vector<Associations> assocVector = new Vector<Associations>();
			assocVector.setSize(associations.size());
			Collections.copy(assocVector, associations);
			levels.add(assocVector);
			while (true) {
				List<Long> objectsId = new Vector<Long>();
				Vector<Associations> previousLevel = levels.lastElement();
				for (int k = 0; k < previousLevel.size(); k++) {
					Associations nextAssoc = previousLevel.get(k);
					objectsId.add(nextAssoc.getSrcId());
					objectsId.add(nextAssoc.getTgtId());
				}
				List<Associations> nextLevel = dbViewer.getAllAssociationsUpToCost(objectsId, lowestCostForLeaving);
				if (nextLevel.size() == 0) {
					break;
				}
				Vector<Associations> nextLevelVec = new Vector<Associations>();
				nextLevelVec.setSize(nextLevel.size());
				Collections.copy(nextLevelVec, nextLevel);
				levels.add(nextLevelVec);
			}
			// Sestavime seznam associaci pro odstraneni
			List<Long> assocIdToDelete = new Vector<Long>();
			
			for (int m = 0; m < levels.size(); m++) {
				Vector<Associations> nextLevel = levels.get(m);
				for (int n = 0; n < nextLevel.size(); n++) {
					assocIdToDelete.add(nextLevel.get(n).getId());
				}
			}
			dbViewer.deleteAssociations(assocIdToDelete);
		}
	}

}
