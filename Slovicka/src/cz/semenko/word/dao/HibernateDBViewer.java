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
import cz.semenko.word.persistent.Cell;

/**
 * <p>HibernateDBViewer class.</p>
 *
 * @author k
 * @version $Id: $Id
 */
public class HibernateDBViewer implements DBViewer {


	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSuperiorCellsId(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Long> getSuperiorCellsId(Vector<Long> pairsToFind)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getAllAssociations(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Associations> getAllAssociations(Vector<Long> cellsId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#increaseAssociationsCostToCellsId(java.lang.Long[])
	 */
	/** {@inheritDoc} */
	@Override
	public void increaseAssociationsCostToCellsId(Long[] obIdArray)
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
	 * @see cz.semenko.word.database.AbstractDBViewer#getNewPrimitiveCells(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Cell> getNewPrimitiveCells(Vector<Character> nonExistent)
			throws Exception {
		Vector<Cell> result = new Vector<Cell>();
		Session sess = getSession();
		Query q = sess.createQuery("from Cell where src in (:param)");
		q.setParameterList("param", nonExistent);
		result = (Vector<Cell>)q.list();
		return result;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#insertAssociations(java.util.Vector, java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Cell> newCells)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getNewCells(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Cell> getNewCells(Vector<Thought> thoughtPairsToUnion)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#deleteCells(java.util.Vector)
	 */
	/** {@inheritDoc} */
	@Override
	public void deleteCells(List<Long> idVector) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#cleanMemoryFromRedundantCells()
	 */
	/** {@inheritDoc} */
	@Override
	public void cleanMemoryFromRedundantCells() throws SQLException {
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
	 * @see cz.semenko.word.database.AbstractDBViewer#getCell(java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public Long getCell(Long srcCellID, Long tgtCellID,
			Long synteticProperty) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getLeftNeighbours(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	public Vector<Cell> getLeftNeighbours(String src) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrcToCells(java.lang.Long[])
	 */
	/** {@inheritDoc} */
	@Override
	public Map<Long, String> getSrcToCells(Long[] inputCells)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.database.AbstractDBViewer#getSrcAndTgt(java.lang.Long, java.lang.Long)
	 */
	/** {@inheritDoc} */
	@Override
	public String getSrcAndTgt(Long srcCell, Long tgtCell) throws Exception {
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
			Vector<Associations> associations, Map<Long, Cell> cellsPool,
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
	public List<Cell> getPrimitiveCells(List<Character> missingChars) {
		List<String> missingStrings = new Vector<String>();
		for (int i = 0; i < missingChars.size(); i++) {
			missingStrings.add(Character.toString(missingChars.get(i)));
		}
		Session session = getSession();
		Query q = session.createQuery("from Cell where src in (:param) and type = 1");
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
	public List<Associations> getAllAssociationsUpToCost(List<Long> cellsId,
			int lowestCostForLeaving) {
		Session s = getSession();
		Query q = s.createQuery("from Associations where cost < :lowestCost and id in (:cellsId)")
			.setParameterList("cellsId", cellsId)
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
	public Vector<Cell> getCells(Vector<Long> missingCellsId)
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
