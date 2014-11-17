package cz.semenko.word.sleep;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.dao.DBViewer;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;


/**
 * <p>MemoryCleaner class manage sleep process.</p>
 * <img src="doc-files/class_diagram_MemoryCleaner.png"/>
 *
 * @author Kyrylo Semenko
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
	 * <img src="doc-files\sequence_diagram_cleanMemoryFromUselessCells.png"/> <br>
	 * Activity diagram <br>
	 * <img src="doc-files\activity_diagram_cleanMemoryFromUselessCells.png"/> <br>
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void cleanMemoryFromUselessCells() throws SQLException {
		int lowestCostForLeave = config.getMemoryCleaner_lowestCostForLeaving();
		cleanMemoryFromUselessAssociations(lowestCostForLeave);		
	}
	
	/** 
	 * Remove Associations with COST less than parameter<br>
	 * See {@link #cleanMemoryFromUselessCells() 
	 * @param lowestCostForLeave */
	private void cleanMemoryFromUselessAssociations (int lowestCostForLeave) throws SQLException {
		// Start from the last Association
		int numOfAssocToProcess = 500; //TODO add to config file
		Long maxAssociationsId = dbViewer.getMaxAssociationsId();
		logger.info("START. Max Associations id: " + maxAssociationsId);
		if (numOfAssocToProcess > maxAssociationsId) {
			numOfAssocToProcess = maxAssociationsId.intValue();
		}
		for (long i = maxAssociationsId; i > 0; i = i - numOfAssocToProcess) {
			logger.info("Process Associations from " + (i-numOfAssocToProcess+1) + " to " + i);
			List<Associations> associations = dbViewer.getAssociations(i-numOfAssocToProcess+1,i,lowestCostForLeave);		
			if (associations.size() == 0) {
				continue;
			}
			List<List<Associations>> levels = new ArrayList<List<Associations>>();
			levels.add(associations);
			while (true) {
				List<Associations> nextLevel = extractNextLevelFromLevels(lowestCostForLeave, levels);
				if (nextLevel.size() == 0) {
					break;
				}
				levels.add(nextLevel);
			}
			deleteAssociationsAndCellsList(levels); // ksemenko TODO nemazat ale nastavit na dummy
		}
		
		logger.info("STOP. Number of Associations: " + dbViewer.getMaxAssociationsId());
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
	 * Remove Associations and Cells from database
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
		logger.info("Removing Associations. Count: " + assocIdToDelete.size());
		dbViewer.deleteAssociations(assocIdToDelete);
		dbViewer.deleteCells(cellsToDelete);
	}

	/**
	 * {@link DBViewer#deleteEverything()}
	 */
	public void forgetEverything() throws SQLException {
		dbViewer.deleteEverything();
	}
}
