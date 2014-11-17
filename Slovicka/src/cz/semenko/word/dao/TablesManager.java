package cz.semenko.word.dao;

import java.sql.SQLException;
import java.util.Stack;

import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;

/**
 * Manager of database tables.
 */
public class TablesManager {
	/** DAO object. See {@link DBViewer} */
	private DBViewer dbViewer;
	
	/** Current max ID of the {@link Cell} table */
	private Long maxCellsId;
	
	/** Current max ID of the {@link Associations} table */
	private Long maxAssociationsId;
	
	/** Stack of available IDs in Cells table */
	private Stack<Long> availableCellsIdStack = new Stack<Long>();
	
	/** Empty constructor */
	public TablesManager() throws SQLException {}

	/** Find out a new available ID to insert 
	 * @throws SQLException */
	public Long getNextCellsId() throws SQLException {
		if (availableCellsIdStack.empty()) {
			availableCellsIdStack.addAll(dbViewer.getAvailableCellsIdList());
		}
		return availableCellsIdStack.remove(0);
	}
	
	
	
	/**
	 * @return the {@link DBViewer}<br>
	 * See {@link TablesManager#dbViewer}
	 */
	public DBViewer getDbViewer() {
		return dbViewer;
	}

	/**
	 * @param dbViewer the {@link DBViewer} to set<br>
	 * See {@link TablesManager#dbViewer}
	 */
	public void setDbViewer(DBViewer dbViewer) {
		this.dbViewer = dbViewer;
	}

	/**
	 * @return the {@link Long}<br>
	 * See {@link TablesManager#maxCellsId}
	 * @throws SQLException 
	 */
	public Long getMaxCellsId() throws SQLException {
		if (maxCellsId == null) {
			maxCellsId = dbViewer.getMaxCellsId();
		}
		return maxCellsId;
	}

	/**
	 * @param maxCellsId the {@link Long} to set<br>
	 * See {@link TablesManager#maxCellsId}
	 */
	public void setMaxCellsId(Long maxCellsId) {
		this.maxCellsId = maxCellsId;
	}

	/**
	 * @return the {@link Long}<br>
	 * See {@link TablesManager#maxAssociationsId}
	 * @throws SQLException 
	 */
	public Long getMaxAssociationsId() throws SQLException {
		if (maxAssociationsId == null) {
			maxAssociationsId = dbViewer.getMaxAssociationsId();
		}
		return maxAssociationsId;
	}

	/**
	 * @param maxAssociationsId the {@link Long} to set<br>
	 * See {@link TablesManager#maxAssociationsId}
	 */
	public void setMaxAssociationsId(Long maxAssociationsId) {
		this.maxAssociationsId = maxAssociationsId;
	}
}
