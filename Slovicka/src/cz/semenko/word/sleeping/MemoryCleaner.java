package cz.semenko.word.sleeping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.semenko.word.Config;
import cz.semenko.word.dao.DBViewer;
import cz.semenko.word.persistent.Associations;


public class MemoryCleaner {
	
	// Pod spravou Spring FW
	private DBViewer dbViewer;
	private Config config;
	
	public static Logger logger = Logger.getLogger(MemoryCleaner.class);
	
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
		// Zacina z posledni Association
		int numOfAssocToProcess = 500; //TODO add to config file
		Long lastIdAssociations = dbViewer.getLastIdAssociationsTable();
		System.out.println("lastIdAssociations: " + lastIdAssociations);
		logger.info("cleanMemoryFromRedundantAssociations - START. Number of Associations: " + lastIdAssociations);
		for (long i = lastIdAssociations; i > 0; i = i - numOfAssocToProcess) {
			System.out.println("Zpracovavam Associations od " + (i-numOfAssocToProcess+1) + " do " + i);
			List<Associations> associations = dbViewer.getAssociations(
					i-numOfAssocToProcess+1, 
					i, 
					lowestCostForLeaving);		
			if (associations.size() == 0) {
				System.out.println("Nenalezeny Associations s Cost mensim nez " + lowestCostForLeaving);
				continue;
			}
			List<List<Associations>> levels = new ArrayList<List<Associations>>();
			levels.add(associations);
			while (true) {
				List<Long> objectsId = new ArrayList<Long>();
				List<Associations> previousLevel = levels.get(levels.size()-1);
				
				for (int k = 0; k < previousLevel.size(); k++) {
					Associations nextAssoc = previousLevel.get(k);
					objectsId.add(nextAssoc.getSrcId());
					objectsId.add(nextAssoc.getTgtId());
				}
				List<Associations> nextLevel = dbViewer.getAllAssociationsUpToCost(objectsId, lowestCostForLeaving);
				if (nextLevel.size() == 0) {
					break;
				}
				levels.add(nextLevel);
			}
			
			deleteAssociationsAndObjectsList(levels);
		}
		System.out.println("Startuji removeEmptyRows()");
		dbViewer.removeEmptyRows();
		
		lastIdAssociations = dbViewer.getLastIdAssociationsTable();
		logger.info("cleanMemoryFromRedundantAssociations - STOP. Number of Associations: " + lastIdAssociations);
	}

	/**
	 * Odstrani z DB seznam Associations
	 * @param levels
	 * @throws SQLException
	 */
	private void deleteAssociationsAndObjectsList(List<List<Associations>> levels)
			throws SQLException {
		List<Long> assocIdToDelete = new ArrayList<Long>();
		List<Long> objectsToDelete = new ArrayList<Long>();
		
		for (int m = 0; m < levels.size(); m++) {
			List<Associations> nextLevel = levels.get(m);
			for (int n = 0; n < nextLevel.size(); n++) {
				assocIdToDelete.add(nextLevel.get(n).getId());
				objectsToDelete.add(nextLevel.get(n).getObjId());
			}
		}
		System.out.println("Mazu Associations. Pocet: " + assocIdToDelete.size());
		dbViewer.deleteAssociations(assocIdToDelete);
		dbViewer.deleteObjects(objectsToDelete);
	}

	// TODO Remove this
	public static void main(String[] args) {
		try {
			ApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
			//ViewerGUI window = ViewerGUI.getInstance();
			MemoryCleaner cleaner = (MemoryCleaner)applicationContext.getBean("memoryCleaner");
			cleaner.cleanMemoryFromRedundantObjects();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
}
