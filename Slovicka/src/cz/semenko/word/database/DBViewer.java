package cz.semenko.word.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;

/**
 * Interface pro pristup k datum DB
 * @author k
 *
 */
public interface DBViewer {

	/** Get max id from Associations */
	public Long getLastIdAssociationsTable() throws SQLException;

	public Vector<Long> getSuperiorObjectsId(Vector<Long> pairsToFind)
			throws SQLException;

	public Vector<Associations> getAllAssociations(
			Vector<Long> objectsId) throws Exception;

	public void increaseAssociationsCostToObjectsId(Long[] obIdArray)
			throws SQLException;

	public void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception;

	public Vector<Objects> getNewPrimitiveObjects(
			Vector<Character> nonExistent) throws Exception;

	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException;

	public Vector<Objects> getNewObjects(
			Vector<Thought> thoughtPairsToUnion) throws Exception;

	public void deleteObjects(Vector<Long> idVector);

	public int getMaxLevel(Long srcID, Long tgtID) throws Exception;

	public Long getObject(Long srcObjectID, Long tgtObjectID,
			Long synteticProperty) throws Exception;

	public Vector<Objects> getLeftNeighbours(String src)
			throws SQLException;

	public Map<Long, String> getSrcToObjects(Long[] inputObjects)
			throws Exception;

	public String getSrcAndTgt(Long srcObject, Long tgtObject)
			throws Exception;

	public String getStringToTwoAssociations(Long lastAssoc,
			Long nextAssoc) throws Exception;

	public String getSrc(String semicolonSeparategId)
			throws SQLException;

	public String getSrc(Vector<Long> idVector) throws SQLException;

	public String getSrc(Long objId) throws Exception;

	public ResultSet executeQuery(String sql) throws Exception;

	/** Get list of Associations from min ID to max ID with COST is down to parameter */
	public List<Associations> getAssociations(long minId, long maxId,
			int lowestCostForLeaving) throws SQLException;

	/**
	 * Get Objects from DB
	 * @param missingChars. Set of chars.
	 * @return Set of Objects POJO
	 * @throws SQLException 
	 */
	public List<Objects> getPrimitiveObjects(
			List<Character> missingChars) throws SQLException;

	/** Get Assotiations from list, that have cost smaller then parameter */
	public List<Associations> getAllAssociationsUpToCost(
			List<Long> objectsId, int lowestCostForLeaving);

	/** Delete all assotiations from ID list */
	public void deleteAssociations(List<Long> assocIdToDelete);

	/**
	 * Sestavi Vector stringu z idecek tgt z parametru
	 * @param paramStringsMap - Mapa textovych reprezentaci objektu, pro ktere hledame tgt_objekty
	 * @param targetAssociations - Mapa asociaci, pro targety kterych hledame src
	 * @return Vector src stringu pro tgt_id asociaci z parametru
	 * @throws Exception
	 */
	public Vector<String> getSrc(Map<Long, String> paramStringsMap,
			Map<Long, Associations> targetAssociations) throws Exception;

	/**
	 * TODO
	 * @param idVector
	 * @param associationsPool
	 * @param selectAssocBuff
	 * @return
	 * @throws SQLException
	 */
	public Vector<Long> getAssociationsFromPool(Vector<Long> idVector,
			Map<Long, Associations> associationsPool,
			StringBuffer selectAssocBuff) throws SQLException;

	/**
	 * Vytvori Vector Stringu s tim, ze nahore budou SRC pro src_id s objekty vyssiho Type.
	 * Dale nahore budou SRC s Asociacemi s vyssim COST
	 * @param associations
	 * @param objectsPool
	 * @param associationsPool
	 * @param paramStringsMap - Mapa textovych reprezentaci objektu, pro ktere hledame tgt_objekty
	 * @return
	 */
	public Vector<String> getStringVectorFromPools(Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Objects> objectsPool,
			Map<Long, Associations> associationsPool);

	/**
	 * Odstrani prazdne radky v tabulkach associations a objects - posune radku nahoru
	 * @throws SQLException 
	 */
	public void removeEmptyRows() throws SQLException;

	/**
	 * Odstrani prazdne radky (posune neprazdne nahoru)
	 */
	public long removeEmptyRowsFromTable(int rowsToStatement, long lastExistsId,
			String tableName, String idRowName,
			Vector<String> preparedStatementStrings) throws SQLException;

	/**
	 * Nastavi cost u vsech objektu na 0.
	 * @throws SQLException 
	 */
	public void resetAssociationCost() throws SQLException;

	/**
	 * Procisti databazi od objektu, ktere nejsou spojene s zadnum jinym objektem
	 * @throws SQLException 
	 */
	public void cleanMemoryFromRedundantObjects() throws SQLException;

}