package cz.semenko.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.sleep.MemoryCleaner;
import cz.semenko.word.technology.memory.slow.SlowMemory;

/**
 * Interface that defines methods for access to database.<br>
 * All Known Implementing Classes:<br>
 * {@link JdbcDBViewer}
 * {@link HibernateDBViewer}<br>
 * Classes depends on link DBViewer<br>
 * {@link SlowMemory}
 * {@link MemoryCleaner}<br>
 * 
 * <p>Class diagram</p>
 * <img src="doc-files/DBViewer.png" />
 *
 * @author Kyrylo Semenko
 */
public interface DBViewer {

	/**
	 * Get max id from Associations table
	 *
	 * @return a {@link java.lang.Long} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Long getMaxAssociationsId() throws SQLException;

	/**
	 * <p>getSuperiorCellsId.</p>
	 *
	 * @param pairsToFind a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Long> getSuperiorCellsId(Vector<Long> pairsToFind)
			throws SQLException;

	/**
	 * <p>getAllAssociations.</p>
	 *
	 * @param cellsId a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getAllAssociations(
			Vector<Long> cellsId) throws Exception;

	/**
	 * <p>increaseAssociationsCostToCellsId.</p>
	 *
	 * @param obIdArray an array of {@link java.lang.Long} cells.
	 * @throws java.sql.SQLException if any.
	 */
	public void increaseAssociationsCostToCellsId(Long[] obIdArray)
			throws SQLException;

	/**
	 * <p>increaseAssociationsCost.</p>
	 *
	 * @param associationsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception;

	/**
	 * Create new Cells of {@link Cell#TYPE_PRIMITIVE} type and save it to database.
	 *
	 * @param nonExistChars a {@link java.util.Vector} of {@link Character} object.
	 * @return a {@link java.util.Vector} of {@link Cell} objects with type {@link Cell#TYPE_PRIMITIVE}.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Cell> createNewPrimitiveCells(
			Vector<Character> nonExistChars) throws Exception;

	/**
	 * <p>insertAssociations.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @param newCells a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Cell> newCells)
			throws SQLException;

	/**
	 * <p>Create {@link java.util.Vector} of new {@link Cell} objects and save these Cells to DB.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Cell> insertNewCells(Vector<Thought> thoughtPairsToUnion) throws Exception;

	/**
	 * Set Cells as empty or available for reuse
	 *
	 * @param idVector a {@link java.util.List} object.
	 * @throws java.sql.SQLException if any.
	 */
	public void markCellsAsAvailableForReuse(List<Long> idVector) throws SQLException;

	/**
	 * <p>getMaxLevel.</p>
	 *
	 * @param srcID a {@link java.lang.Long} object.
	 * @param tgtID a {@link java.lang.Long} object.
	 * @return a int.
	 * @throws java.lang.Exception if any.
	 */
	public int getMaxLevel(Long srcID, Long tgtID) throws Exception;

	/**
	 * @deprecated
	 * <p>getCell.</p>
	 *
	 * @param srcCellID a {@link java.lang.Long} object.
	 * @param tgtCellID a {@link java.lang.Long} object.
	 * @param synteticProperty a {@link java.lang.Long} object.
	 * @return a {@link java.lang.Long} object.
	 * @throws java.lang.Exception if any.
	 */
	public Long getCell(Long srcCellID, Long tgtCellID,
			Long synteticProperty) throws Exception;

	/**
	 * <p>getLeftNeighbours.</p>
	 *
	 * @param src a {@link java.lang.String} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Cell> getLeftNeighbours(String src)
			throws SQLException;

	/**
	 * <p>getSrcToCells.</p>
	 *
	 * @param inputCells an array of {@link java.lang.Long} cells.
	 * @return a {@link java.util.Map} object.
	 * @throws java.lang.Exception if any.
	 */
	public Map<Long, String> getSrcToCells(Long[] inputCells)
			throws Exception;

	/**
	 * <p>getSrcAndTgt.</p>
	 *
	 * @param srcCell a {@link java.lang.Long} object.
	 * @param tgtCell a {@link java.lang.Long} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public String getSrcAndTgt(Long srcCell, Long tgtCell)
			throws Exception;

	/**
	 * <p>getStringToTwoAssociations.</p>
	 *
	 * @param lastAssoc a {@link java.lang.Long} object.
	 * @param nextAssoc a {@link java.lang.Long} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public String getStringToTwoAssociations(Long lastAssoc,
			Long nextAssoc) throws Exception;

	/**
	 * <p>getSrc.</p>
	 *
	 * @param semicolonSeparategId a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.sql.SQLException if any.
	 */
	public String getSrc(String semicolonSeparategId)
			throws SQLException;

	/**
	 * <p>getSrc.</p>
	 *
	 * @param idVector a {@link java.util.Vector} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.sql.SQLException if any.
	 */
	public String getSrc(Vector<Long> idVector) throws SQLException;

	/**
	 * <p>getSrc.</p>
	 *
	 * @param cellId a {@link java.lang.Long} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public String getSrc(Long cellId) throws Exception;

	/**
	 * <p>executeQuery.</p>
	 *
	 * @param sql a {@link java.lang.String} object.
	 * @return a {@link java.sql.ResultSet} object.
	 * @throws java.lang.Exception if any.
	 */
	public ResultSet executeQuery(String sql) throws Exception;

	/**
	 * Get list of Associations from min ID to max ID with COST is down to parameter
	 *
	 * @param minId a long.
	 * @param maxId a long.
	 * @param lowestCostForLeaving a int.
	 * @return a {@link java.util.List} object.
	 * @throws java.sql.SQLException if any.
	 */
	public List<Associations> getAssociations(long minId, long maxId,
			int lowestCostForLeaving) throws SQLException;

	/**
	 * Get Cell from DB
	 *
	 * @return Set of Cell POJO
	 * @throws java.sql.SQLException if any.
	 * @param missingChars a {@link java.util.List} object.
	 */
	public List<Cell> getPrimitiveCells(
			List<Character> missingChars) throws SQLException;

			/**
			 * Get Assotiations from list, that have cost smaller then parameter
			 *
			 * @throws java.sql.SQLException if any.
			 * @param cellsId a {@link java.util.List} object.
			 * @param lowestCostForLeaving a int.
			 * @return a {@link java.util.List} object.
			 */
	public List<Associations> getAllAssociationsLowerThenCost(
			List<Long> cellsId, int lowestCostForLeaving) throws SQLException;

	/**
	 * Set Associations as empty or available for reuse
	 *
	 * @throws java.sql.SQLException if any.
	 * @param assocIdToDelete a {@link java.util.List} object.
	 */
	public void markAssociationsAsAvailableForReuse(List<Long> assocIdToDelete) throws SQLException;
	
	/**
	 * Nastavi cost u vsech objektu na 0.
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void resetAssociationCost() throws SQLException;
	
	/**
	 * Procisti databazi od objektu, ktere nejsou spojene s zadnum jinym objektem
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void cleanMemoryFromUselessCells() throws SQLException;
	
	/**
	 * Dostane z DB objekty dle zadanych ID. Nevytvari nove.
	 *
	 * @param missingCellsId a {@link java.util.Vector} of {@link cz.semenko.word.persistent.Cell}
	 * @throws java.sql.SQLException if any.
	 * @return a {@link java.util.Vector} of {@link cz.semenko.word.persistent.Cell}
	 */
	public Vector<Cell> getCells(Vector<Long> missingCellsId) throws SQLException;
	
	/**
	 * Dohleda v DB Associations. Jestli nenajde, vrati null
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @throws java.sql.SQLException if any.
	 * @return a {@link cz.semenko.word.persistent.Associations} object.
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws SQLException;

	/**
	 * Warning!<br>
	 * It cleans all data from database.<br>
	 * Its like dead and born.
	 * @throws java.sql.SQLException if any.
	 */
	public void deleteEverything() throws SQLException;

	/** @return number of {@link Associations}Associations objects in table
	 * @throws SQLException */
	public Long getAssociationsCount() throws SQLException;

	/** @return number of {@link Cell} objects in table
	 * @throws SQLException */
	public Long getCellsCount() throws SQLException;

	/** @return max id of {@link Cells} object in table
	 * @throws SQLException */
	public Long getMaxCellsId() throws SQLException;

	/** 
	 * This method always returns constant number of IDs. <br>
	 * {@link MemoryCleaner} marks {@link Cell} rows as available for reuse. These available IDs will by returned (see {@link DBViewer#getCellsIdMarkedAsAvailable()} method).
	 * In addition when its count is less then {@link Config#getDbViewer_numberOfAvailableCellsIdToReturn()}, then extra IDs will be generated.
	 * @return available for reuse IDs from {@link Cell} table plus new generated IDs in total numbers of {@link Config#getDbViewer_numberOfAvailableCellsIdToReturn()}
	 * @throws SQLException */
	public Collection<Long> getAvailableCellsIdList() throws SQLException;
	
	/**
	 * Don't use this method for obtain available IDs. Please use a {@link DBViewer#getAvailableCellsIdList()} method instead. 
	 * @return list of {@link Cell} IDs marked as available (linked to {@link Cell#DUMMY_TYPE}) row. But number of returned IDs is not greater then {@link Config#getDbViewer_numberOfAvailableCellsIdToReturn()}
	 */
	public Collection<Long> getCellsIdMarkedAsAvailable() throws SQLException;

	/** 
	 * This method always returns constant number of IDs. <br>
	 * {@link MemoryCleaner} marks {@link Associations} rows as available for reuse. These available IDs will by returned (see {@link DBViewer#getAssociationsIdMarkedAsAvailable()} method).
	 * In addition when its count is less then {@link Config#getDbViewer_numberOfAvailableAssociationsIdToReturn()}, then extra IDs will be generated.
	 * @return available for reuse IDs from {@link Associations} table plus new generated IDs in total numbers of {@link Config#getDbViewer_numberOfAvailableAssociationsIdToReturn()}
	 * @throws SQLException */
	public Collection<Long> getAvailableAssociationsIdList() throws SQLException;

	/**
	 * Don't use this method for obtain available IDs. Please use a {@link DBViewer#getAvailableAssociationsIdList()} method instead. 
	 * @return list of {@link Associations} IDs marked as available (linked to {@link Cell#DUMMY_CELL_ID}) row. But number of returned IDs is not greater then {@link Config#getDbViewer_numberOfAvailableAssociationsIdToReturn()}
	 */
	public Collection<Long> getAssociationsIdMarkedAsAvailable() throws SQLException;
}
