package cz.semenko.word.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;

public abstract class AbstractDBViewer {
	private static AbstractDBViewer instance;

	protected static Logger logger = Logger.getRootLogger();

	public static AbstractDBViewer getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized(AbstractDBViewer.class) {
			AbstractDBViewer inst = instance;
			if (inst == null) {
				try {
					String dbViewerClassName = Config.getInstance().getDBViewerClassName();
					Class abstr = Class.forName(dbViewerClassName);
					instance = (AbstractDBViewer)(abstr.newInstance());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return instance;
	}

	AbstractDBViewer() {
		super();
	}
	/** Get max id from Associations */
	public abstract Long getLastIdAssociationsTable() throws SQLException;

	public abstract Vector<Long> getSuperiorObjectsId(Vector<Long> pairsToFind)
			throws SQLException;

	public abstract Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception;

	public abstract void increaseAssociationsCostToObjectsId(Long[] obIdArray)
			throws SQLException;

	public abstract void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception;

	public abstract Vector<Objects> getNewPrimitiveObjects(Vector<Character> nonExistent)
			throws Exception;

	public abstract Vector<Associations> insertAssociations(Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException;

	public abstract Vector<Objects> getNewObjects(Vector<Thought> thoughtPairsToUnion) throws Exception;

	public abstract void deleteObjects(Vector<Long> idVector);

	//public abstract ResultSet executeQuery(String sql) throws Exception;

	protected abstract void cleanMemoryFromRedundantObjects()
			throws SQLException;

	protected abstract void resetAssociationCost() throws SQLException;

	protected abstract long removeEmptyRowsFromTable(int rowsToStatement,
			long lastExistsId, String tableName, String idRowName, Vector<String> preparedStatementStrings)
			throws SQLException;

	protected abstract void removeEmptyRows() throws SQLException;

	protected abstract void finalize() throws Throwable;

	public abstract int getMaxLevel(Long srcID, Long tgtID)
			throws Exception;

	public abstract Long getObject(Long srcObjectID, Long tgtObjectID, Long synteticProperty)
			throws Exception;

	public abstract Vector<Objects> getLeftNeighbours(String src)
			throws SQLException;

	public abstract Map<Long, String> getSrcToObjects(Long[] inputObjects) throws Exception;

	public abstract Vector<String> getRightNeighbours(String src)
			throws Exception;

	public abstract String getSrcAndTgt(Long srcObject, Long tgtObject)
			throws Exception;

	public abstract String getStringToTwoAssociations(
			Long lastAssoc, Long nextAssoc) throws Exception;

	protected abstract Vector<String> getStringVectorFromPools(Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Objects> objectsPool, Map<Long, Associations> associationsPool);

	protected abstract Vector<Long> getAssociationsFromPool(Vector<Long> idVector,
			Map<Long, Associations> associationsPool, StringBuffer selectAssocBuff) throws SQLException;

	protected abstract Vector<String> getSrc(Map<Long, String> paramStringsMap, Map<Long, Associations> targetAssociations)
			throws Exception;

	public abstract String getSrc(String semicolonSeparategId) throws SQLException;

	public abstract String getSrc(Vector<Long> idVector) throws SQLException;

	public abstract String getSrc(Long objId) throws Exception;
	
	public abstract ResultSet executeQuery(String sql) throws Exception;

	/** Get list of Associations from min ID to max ID with COST is down to parameter */
	public abstract List<Associations> getAssotiations(long minId, long maxId, int lowestCostForLeaving) throws SQLException;
	
	/**
	 * Get Objects from DB
	 * @param missingChars. Set of chars.
	 * @return Set of Objects POJO
	 * @throws SQLException 
	 */
	public abstract List<Objects> getPrimitiveObjects(List<Character> missingChars) throws SQLException;
	
	/** Get Assotiations from list, that have cost smaller then parameter */
	public abstract List<Associations> getAllAssociationsUpToCost(List objectsId, int lowestCostForLeaving);

	/** Delete all assotiations from ID list */
	public abstract void deleteAssociations(List assocIdToDelete);
}