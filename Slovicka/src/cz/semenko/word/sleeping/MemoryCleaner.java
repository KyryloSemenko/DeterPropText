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
import cz.semenko.word.persistent.Cell;


/**
 * <p>MemoryCleaner class.</p>
 *
 * @author k
 * @version $Id: $Id
 */
public class MemoryCleaner {
	
	// Pod spravou Spring FW
	private DBViewer dbViewer;
	private Config config;
	
	/** Constant <code>logger</code> */
	public static Logger logger = Logger.getLogger(MemoryCleaner.class);
	
	/**
	 * <p>Empty constructor for MemoryCleaner.</p>
	 */
	public MemoryCleaner() {}
	
	/**
	 * <p>Setter for the field <code>config</code>.</p>
	 *
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * <p>Setter for the field <code>dbViewer</code>.</p>
	 *
	 * @param dbViewer the dbViewer to set
	 */
	public void setDbViewer(DBViewer dbViewer) {
		this.dbViewer = dbViewer;
	}

	/**
	 * Remove {@link Associations} objects and their {@link Cell} objects that has low {@link Associations#cost} from database.<br>
	 * Sequence diagram <br>
	 * <img src="doc-files\sequence_diagram_cleanMemoryFromRedundantCells.png"/> <br>
	 * Activity diagram <br>
	 * <img src="doc-files\activity_diagram_cleanMemoryFromRedundantCells.png"/> <br>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void cleanMemoryFromRedundantCells() throws SQLException {
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
			List<Associations> associations = dbViewer.getAssociations(i-numOfAssocToProcess+1,i,lowestCostForLeaving);		
			if (associations.size() == 0) {
				System.out.println("Nenalezeny Associations s Cost mensim nez " + lowestCostForLeaving);
				continue;
			}
			List<List<Associations>> levels = new ArrayList<List<Associations>>();
			levels.add(associations);
			while (true) {
				List<Associations> nextLevel = extractNextLevelFromLevels(lowestCostForLeaving, levels);
				if (nextLevel.size() == 0) {
					break;
				}
				levels.add(nextLevel);
			}
			deleteAssociationsAndCellsList(levels);
		}
		System.out.println("Startuji removeEmptyRows()");
		dbViewer.removeEmptyRows();
		
		lastIdAssociations = dbViewer.getLastIdAssociationsTable();
		logger.info("cleanMemoryFromRedundantAssociations - STOP. Number of Associations: " + lastIdAssociations);
	}

	/** Get Associations List from levels */
	private List<Associations> extractNextLevelFromLevels(int lowestCostForLeaving,
			List<List<Associations>> levels) throws SQLException {
		List<Long> cellsId = new ArrayList<Long>();
		List<Associations> previousLevel = levels.get(levels.size()-1);
		
		addCellsIdFromPreviousLevelsAssociations(cellsId, previousLevel);
		
		List<Associations> nextLevel = dbViewer.getAllAssociationsLowerThenCost(cellsId, lowestCostForLeaving);
		return nextLevel;
	}

	/** For each Associations in previousLevel List extract srcId and tgtId and add them to cellsId List */
	private void addCellsIdFromPreviousLevelsAssociations(List<Long> cellsId,
			List<Associations> previousLevel) {
		for (int k = 0; k < previousLevel.size(); k++) {
			Associations nextAssoc = previousLevel.get(k);
			cellsId.add(nextAssoc.getSrcId());
			cellsId.add(nextAssoc.getTgtId());
		}
	}

	/**
	 * Odstrani z DB seznam Associations
	 * @param levels
	 * @throws SQLException
	 */
	private void deleteAssociationsAndCellsList(List<List<Associations>> levels)
			throws SQLException {
		List<Long> assocIdToDelete = new ArrayList<Long>();
		List<Long> cellsToDelete = new ArrayList<Long>();
		
		for (int m = 0; m < levels.size(); m++) {
			List<Associations> nextLevel = levels.get(m);
			for (int n = 0; n < nextLevel.size(); n++) {
				assocIdToDelete.add(nextLevel.get(n).getId());
				cellsToDelete.add(nextLevel.get(n).getObjId());
			}
		}
		System.out.println("Mazu Associations. Pocet: " + assocIdToDelete.size());
		dbViewer.deleteAssociations(assocIdToDelete);
		dbViewer.deleteCells(cellsToDelete);
	}

	// TODO Remove this
	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} cells.
	 */
	public static void main(String[] args) {
		try {
			ApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext("classpath:/applicationContext.xml");
			//ViewerGUI window = ViewerGUI.getInstance();
			MemoryCleaner cleaner = (MemoryCleaner)applicationContext.getBean("memoryCleaner");
			cleaner.cleanMemoryFromRedundantCells();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
}
