package cz.semenko.word.dao;

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
	 * <p>getSuperiorObjectsId.</p>
	 *
	 * @param pairsToFind a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Long> getSuperiorObjectsId(Vector<Long> pairsToFind)
			throws SQLException;

	/**
	 * <p>getAllAssociations.</p>
	 *
	 * @param objectsId a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getAllAssociations(
			Vector<Long> objectsId) throws Exception;

	/**
	 * <p>increaseAssociationsCostToObjectsId.</p>
	 *
	 * @param obIdArray an array of {@link java.lang.Long} objects.
	 * @throws java.sql.SQLException if any.
	 */
	public void increaseAssociationsCostToObjectsId(Long[] obIdArray)
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
	 * <p>getNewPrimitiveObjects.</p>
	 *
	 * @param nonExistent a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Objects> getNewPrimitiveObjects(
			Vector<Character> nonExistent) throws Exception;

	/**
	 * <p>insertAssociations.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @param newObjects a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException;

	/**
	 * <p>getNewObjects.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Objects> getNewObjects(
			Vector<Thought> thoughtPairsToUnion) throws Exception;

	/**
	 * <p>deleteObjects.</p>
	 *
	 * @param idVector a {@link java.util.List} object.
	 * @throws java.sql.SQLException if any.
	 */
	public void deleteObjects(List<Long> idVector) throws SQLException;

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
	 * <p>getObject.</p>
	 *
	 * @param srcObjectID a {@link java.lang.Long} object.
	 * @param tgtObjectID a {@link java.lang.Long} object.
	 * @param synteticProperty a {@link java.lang.Long} object.
	 * @return a {@link java.lang.Long} object.
	 * @throws java.lang.Exception if any.
	 */
	public Long getObject(Long srcObjectID, Long tgtObjectID,
			Long synteticProperty) throws Exception;

	/**
	 * <p>getLeftNeighbours.</p>
	 *
	 * @param src a {@link java.lang.String} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Objects> getLeftNeighbours(String src)
			throws SQLException;

	/**
	 * <p>getSrcToObjects.</p>
	 *
	 * @param inputObjects an array of {@link java.lang.Long} objects.
	 * @return a {@link java.util.Map} object.
	 * @throws java.lang.Exception if any.
	 */
	public Map<Long, String> getSrcToObjects(Long[] inputObjects)
			throws Exception;

	/**
	 * <p>getSrcAndTgt.</p>
	 *
	 * @param srcObject a {@link java.lang.Long} object.
	 * @param tgtObject a {@link java.lang.Long} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public String getSrcAndTgt(Long srcObject, Long tgtObject)
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
	 * @param objId a {@link java.lang.Long} object.
	 * @return a {@link java.lang.String} object.
	 * @throws java.lang.Exception if any.
	 */
	public String getSrc(Long objId) throws Exception;

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
	 * Get Objects from DB
	 *
	 * @return Set of Objects POJO
	 * @throws java.sql.SQLException if any.
	 * @param missingChars a {@link java.util.List} object.
	 */
	public List<Objects> getPrimitiveObjects(
			List<Character> missingChars) throws SQLException;

			/**
			 * Get Assotiations from list, that have cost smaller then parameter
			 *
			 * @throws java.sql.SQLException if any.
			 * @param objectsId a {@link java.util.List} object.
			 * @param lowestCostForLeaving a int.
			 * @return a {@link java.util.List} object.
			 */
	public List<Associations> getAllAssociationsUpToCost(
			List<Long> objectsId, int lowestCostForLeaving) throws SQLException;

	/**
	 * Delete all assotiations from ID list
	 *
	 * @throws java.sql.SQLException if any.
	 * @param assocIdToDelete a {@link java.util.List} object.
	 */
	public void deleteAssociations(List<Long> assocIdToDelete) throws SQLException;
/**
 * Sestavi Vector stringu z idecek tgt z parametru
 *
 * @param paramStringsMap - Mapa textovych reprezentaci objektu, pro ktere hledame tgt_objekty
 * @param targetAssociations - Mapa asociaci, pro targety kterych hledame src
 * @return Vector src stringu pro tgt_id asociaci z parametru
 * @throws java.lang.Exception if any.
 */
	public Vector<String> getSrc(Map<Long, String> paramStringsMap,
			Map<Long, Associations> targetAssociations) throws Exception;

			/**
			 * TODO
			 *
			 * @param idVector a {@link java.util.Vector} object.
			 * @param associationsPool a {@link java.util.Map} object.
			 * @param selectAssocBuff a {@link java.lang.StringBuffer} object.
			 * @throws java.sql.SQLException if any.
			 * @return a {@link java.util.Vector} object.
			 */
	public Vector<Long> getAssociationsFromPool(Vector<Long> idVector,
			Map<Long, Associations> associationsPool,
			StringBuffer selectAssocBuff) throws SQLException;

			/**
			 * Vytvori Vector Stringu s tim, ze nahore budou SRC pro src_id s objekty vyssiho Type.
			 * Dale nahore budou SRC s Asociacemi s vyssim COST
			 *
			 * @param associations
			 * @param objectsPool a {@link java.util.Map} object.
			 * @param associationsPool a {@link java.util.Map} object.
			 * @param paramStringsMap - Mapa textovych reprezentaci objektu, pro ktere hledame tgt_objekty
			 * @return a {@link java.util.Vector} object.
			 */
	public Vector<String> getStringVectorFromPools(Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Objects> objectsPool,
			Map<Long, Associations> associationsPool);

	/**
	 * Odstrani prazdne radky v tabulkach associations a objects - posune radku nahoru
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
	public void cleanMemoryFromRedundantObjects() throws SQLException;
	
	/**
	 * Dostane z DB objekty dle zadanych ID. Nevytvari nove.
	 *
	 * @param missingObjectsId a {@link java.util.Vector} of {@link cz.semenko.word.persistent.Objects}
	 * @throws java.sql.SQLException if any.
	 * @return a {@link java.util.Vector} of {@link cz.semenko.word.persistent.Objects}
	 */
	public Vector<Objects> getObjects(Vector<Long> missingObjectsId) throws SQLException;
	
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
