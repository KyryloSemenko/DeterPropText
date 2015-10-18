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

	/** Stack of available IDs in Associations table */
	private Stack<Long> availableAssociationsIdStack = new Stack<Long>();
	
	/** Empty constructor */
	public TablesManager() throws SQLException {}

	/***************************************** business logic ****************************************/
	
	/**
	 * @return the {@link Long}<br>
	 * See {@link TablesManager#maxCellsId}
	 * @throws SQLException 
	 */
	public Long getMaxCellsId() {
		if (maxCellsId == null) {
			maxCellsId = dbViewer.getMaxCellsId();
		}
		return maxCellsId;
	}

	/** Find out a new available ID to insert */
	public Long getNextCellsId() {
		if (availableCellsIdStack.empty()) {
			availableCellsIdStack.addAll(dbViewer.getAvailableCellsIdList(getMaxCellsId()));
		}
		setMaxCellsId(availableCellsIdStack.remove(0));
		return getMaxCellsId();
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
	
	/** Find out a new available ID to insert 
	 * @throws SQLException */
	public Long getNextAssociationsId() throws SQLException {
		if (availableAssociationsIdStack.empty()) {
			availableAssociationsIdStack.addAll(dbViewer.getAvailableAssociationsIdList(getMaxAssociationsId()));
		}
		setMaxAssociationsId(availableAssociationsIdStack.remove(0));
		return getMaxAssociationsId();
	}
	
	/***************************************** getters and setters ****************************************/
	
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
	 * @param maxCellsId the {@link Long} to set<br>
	 * See {@link TablesManager#maxCellsId}
	 */
	public void setMaxCellsId(Long maxCellsId) {
		this.maxCellsId = maxCellsId;
	}

	/**
	 * @param maxAssociationsId the {@link Long} to set<br>
	 * See {@link TablesManager#maxAssociationsId}
	 */
	public void setMaxAssociationsId(Long maxAssociationsId) {
		this.maxAssociationsId = maxAssociationsId;
	}

	/**
	 * When creation of {@link Cell} object crashed, return back unused ID
	 */
	public void moveBackNextCellsId(Long returnedId) {
		availableCellsIdStack.set(0, returnedId);
	}
	
	/**
	 * When creation of {@link Associations} object crashed, return back unused ID
	 */
	public void moveBackNextAssociationsId(Long returnedId) {
		availableAssociationsIdStack.set(0, returnedId);
	}
}
