package cz.semenko.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Session;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;

/**
 * <p>HibernateDBViewer class.</p>
 *
 * @author k
 * @version $Id: $Id
 */
public class HibernateDBViewer implements DBViewer {


	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSuperiorObjectsId(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Long> getSuperiorObjectsId(Vector<Long> pairsToFind)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getAllAssociations(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Associations> getAllAssociations(Vector<Long> objectsId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#increaseAssociationsCostToObjectsId(java.lang.Long[])
	 */
	/** {@inheritDoc} */
	@Override
	public void increaseAssociationsCostToObjectsId(Long[] obIdArray)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#increaseAssociationsCost(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getNewPrimitiveObjects(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Objects> getNewPrimitiveObjects(Vector<Character> nonExistent)
			throws Exception {
		Vector<Objects> result = new Vector<Objects>();
		Session sess = getSession();
		Query q = sess.createQuery("from Objects where src in (:param)");
		q.setParameterList("param", nonExistent);
		result = (Vector<Objects>)q.list();
		return result;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#insertAssociations(java.util.Vector, java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getNewObjects(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Objects> getNewObjects(Vector<Thought> thoughtPairsToUnion)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#deleteObjects(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public void deleteObjects(List<Long> idVector) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#cleanMemoryFromRedundantObjects()
	 */
	/** {@inheritDoc} */
	@Override
	public void cleanMemoryFromRedundantObjects() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#resetAssociationCost()
	 */
	/** {@inheritDoc} */
	@Override
	public void resetAssociationCost() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#removeEmptyRows()
	 */
	/** {@inheritDoc} */
	@Override
	public void removeEmptyRows() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#finalize()
	 */
	/** {@inheritDoc} */
	@Override
	public void finalize() throws Throwable {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getMaxLevel(java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public int getMaxLevel(Long srcID, Long tgtID) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getObject(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public Long getObject(Long srcObjectID, Long tgtObjectID,
			Long synteticProperty) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getLeftNeighbours(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Objects> getLeftNeighbours(String src) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrcToObjects(java.lang.Long[])
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Long, String> getSrcToObjects(Long[] inputObjects)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrcAndTgt(java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSrcAndTgt(Long srcObject, Long tgtObject) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getStringToTwoAssociations(java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public String getStringToTwoAssociations(Long lastAssoc, Long nextAssoc)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getStringVectorFromPools(java.util.Map, java.util.Vector, java.util.Map, java.util.Map)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<String> getStringVectorFromPools(
			Map<Long, String> paramStringsMap,
			Vector<Associations> associations, Map<Long, Objects> objectsPool,
			Map<Long, Associations> associationsPool) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getAssociationsFromPool(java.util.Vector, java.util.Map, java.lang.StringBuffer)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Long> getAssociationsFromPool(Vector<Long> idVector,
			Map<Long, Associations> associationsPool,
			StringBuffer selectAssocBuff) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrc(java.util.Map, java.util.Map)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<String> getSrc(Map<Long, String> paramStringsMap,
			Map<Long, Associations> targetAssociations) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrc(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSrc(String semicolonSeparategId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrc(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSrc(Vector<Long> idVector) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrc(java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSrc(Long objId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#executeQuery(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public ResultSet executeQuery(String sql) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<Objects> getPrimitiveObjects(List<Character> missingChars) {
		List<String> missingStrings = new Vector<String>();
		for (int i = 0; i < missingChars.size(); i++) {
			missingStrings.add(Character.toString(missingChars.get(i)));
		}
		Session session = getSession();
		Query q = session.createQuery("from Objects where src in (:param) and type = 1");
		q.setParameterList("param", missingStrings);
		return q.list();
	}
	
	private Session getSession() {
		return HibernateUtil.getSessionFactory().openSession();
	}

	/** {@inheritDoc} */
	@Override
	public Long getLastIdAssociationsTable() throws SQLException {
		Session session = getSession();
		Query q = session.createQuery("select max(id) from Associations");
		return (Long)q.list().get(0);
	}

	/** {@inheritDoc} */
	@Override
	public List<Associations> getAssociations(long minId, long maxId,
			int lowestCostForLeaving) {
		Session s = getSession();
		Query q = s.createQuery("from Associations where id > :minId and id <= :maxId and cost < :lowestCost")
			.setLong("maxId", maxId)
			.setLong("minId", minId)
			.setInteger("lowestCost", lowestCostForLeaving);
		List<Associations> result = (List<Associations>)q.list();
		return result;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public List<Associations> getAllAssociationsUpToCost(List<Long> objectsId,
			int lowestCostForLeaving) {
		Session s = getSession();
		Query q = s.createQuery("from Associations where cost < :lowestCost and id in (:objectsId)")
			.setParameterList("objectsId", objectsId)
			.setParameter("lowestCost", (long)lowestCostForLeaving);
		return (List<Associations>)q.list();
	}

	/** {@inheritDoc} */
	@Override
	public void deleteAssociations(List<Long> assocIdToDelete) {
		Session s = getSession();
		s.createQuery("delete from Associations where id in :param")
			.setParameterList("param", assocIdToDelete)
			.executeUpdate();
		return;
	}

	/** {@inheritDoc} */
	@Override
	public Vector<Objects> getObjects(Vector<Long> missingObjectsId)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Associations getAssociation(Thought srcThought, Thought tgtThought)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
