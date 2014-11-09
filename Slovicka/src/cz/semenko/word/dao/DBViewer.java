package cz.semenko.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.sleeping.MemoryCleaner;
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
	 * Get max id from Associations
	 *
	 * @return a {@link java.lang.Long} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Long getLastIdAssociationsTable() throws SQLException;

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
	 * <p>getNewPrimitiveCells.</p>
	 *
	 * @param nonExistent a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Cell> getNewPrimitiveCells(
			Vector<Character> nonExistent) throws Exception;

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
	 * <p>Create {@link java.util.Vector} of new {@link Cell} objects and save it to database.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Cell> getNewCells(
			Vector<Thought> thoughtPairsToUnion) throws Exception;

	/**
	 * <p>deleteCells.</p>
	 *
	 * @param idVector a {@link java.util.List} object.
	 * @throws java.sql.SQLException if any.
	 */
	public void deleteCells(List<Long> idVector) throws SQLException;

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
	public List<Associations> getAllAssociationsUpToCost(
			List<Long> cellsId, int lowestCostForLeaving) throws SQLException;

	/**
	 * Delete all assotiations from ID list
	 *
	 * @throws java.sql.SQLException if any.
	 * @param assocIdToDelete a {@link java.util.List} object.
	 */
	public void deleteAssociations(List<Long> assocIdToDelete) throws SQLException;

	/**
	 * Vytvori Vector Stringu s tim, ze nahore budou SRC pro src_id s objekty vyssiho Type.
	 * Dale nahore budou SRC s Asociacemi s vyssim COST
	 *
	 * @param associations
	 * @param cellsPool a {@link java.util.Map} object.
	 * @param associationsPool a {@link java.util.Map} object.
	 * @param paramStringsMap - Mapa textovych reprezentaci objektu, pro ktere hledame tgt_objekty
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<String> getStringVectorFromPools(Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Cell> cellsPool,
			Map<Long, Associations> associationsPool);

	/**
	 * Odstrani prazdne radky v tabulkach associations a cells - posune radku nahoru
	 *
	 * @throws java.sql.SQLException if any.
	 */
	public void removeEmptyRows() throws SQLException;
	
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
	public void cleanMemoryFromRedundantCells() throws SQLException;
	
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
	 * @throws java.lang.Exception if any.
	 * @return a {@link cz.semenko.word.persistent.Associations} object.
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws SQLException;
}
