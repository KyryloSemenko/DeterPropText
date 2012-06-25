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

@Deprecated
/*
 * Nepouzivat. Mito stareho pristupu s instanciovanim DBVieweru s typem definovanym v konfiguracnim souboru config.xml
 * nove bude pouzita primo implementujici trida, ktera je definovana v Spring kontejneru.
 */
public abstract class AbstractDBViewer implements DBViewer {
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
	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getLastIdAssociationsTable()
	 */
	public abstract Long getLastIdAssociationsTable() throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSuperiorObjectsId(java.util.Vector)
	 */
	public abstract Vector<Long> getSuperiorObjectsId(Vector<Long> pairsToFind)
			throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getAllAssociations(java.util.Vector)
	 */
	public abstract Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#increaseAssociationsCostToObjectsId(java.lang.Long[])
	 */
	public abstract void increaseAssociationsCostToObjectsId(Long[] obIdArray)
			throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#increaseAssociationsCost(java.util.Vector)
	 */
	public abstract void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getNewPrimitiveObjects(java.util.Vector)
	 */
	public abstract Vector<Objects> getNewPrimitiveObjects(Vector<Character> nonExistent)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#insertAssociations(java.util.Vector, java.util.Vector)
	 */
	public abstract Vector<Associations> insertAssociations(Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getNewObjects(java.util.Vector)
	 */
	public abstract Vector<Objects> getNewObjects(Vector<Thought> thoughtPairsToUnion) throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#deleteObjects(java.util.Vector)
	 */
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

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getMaxLevel(java.lang.Long, java.lang.Long)
	 */
	public abstract int getMaxLevel(Long srcID, Long tgtID)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getObject(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public abstract Long getObject(Long srcObjectID, Long tgtObjectID, Long synteticProperty)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getLeftNeighbours(java.lang.String)
	 */
	public abstract Vector<Objects> getLeftNeighbours(String src)
			throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSrcToObjects(java.lang.Long[])
	 */
	public abstract Map<Long, String> getSrcToObjects(Long[] inputObjects) throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getRightNeighbours(java.lang.String)
	 */
	public abstract Vector<String> getRightNeighbours(String src)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSrcAndTgt(java.lang.Long, java.lang.Long)
	 */
	public abstract String getSrcAndTgt(Long srcObject, Long tgtObject)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getStringToTwoAssociations(java.lang.Long, java.lang.Long)
	 */
	public abstract String getStringToTwoAssociations(
			Long lastAssoc, Long nextAssoc) throws Exception;

	protected abstract Vector<String> getStringVectorFromPools(Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Objects> objectsPool, Map<Long, Associations> associationsPool);

	protected abstract Vector<Long> getAssociationsFromPool(Vector<Long> idVector,
			Map<Long, Associations> associationsPool, StringBuffer selectAssocBuff) throws SQLException;

	protected abstract Vector<String> getSrc(Map<Long, String> paramStringsMap, Map<Long, Associations> targetAssociations)
			throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSrc(java.lang.String)
	 */
	public abstract String getSrc(String semicolonSeparategId) throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSrc(java.util.Vector)
	 */
	public abstract String getSrc(Vector<Long> idVector) throws SQLException;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getSrc(java.lang.Long)
	 */
	public abstract String getSrc(Long objId) throws Exception;
	
	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#executeQuery(java.lang.String)
	 */
	public abstract ResultSet executeQuery(String sql) throws Exception;

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getAssotiations(long, long, int)
	 */
	public abstract List<Associations> getAssotiations(long minId, long maxId, int lowestCostForLeaving) throws SQLException;
	
	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getPrimitiveObjects(java.util.List)
	 */
	public abstract List<Objects> getPrimitiveObjects(List<Character> missingChars) throws SQLException;
	
	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#getAllAssociationsUpToCost(java.util.List, int)
	 */
	public abstract List<Associations> getAllAssociationsUpToCost(List objectsId, int lowestCostForLeaving);

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.DBViewer#deleteAssociations(java.util.List)
	 */
	public abstract void deleteAssociations(List assocIdToDelete);
}