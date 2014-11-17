package cz.semenko.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.hibernate.Query;
import org.hibernate.Session;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;

/**
 * <p>HibernateDBViewer class is DAO object that use Hibernate to supply data from DB.</p>
 *
 * @author Kyrylo Semenko
 */
public class HibernateDBViewer implements DBViewer {
	/** Application configuration */
	private Config config;

	@Override
	public Vector<Long> getSuperiorCellsId(Vector<Long> pairsToFind)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Associations> getAllAssociations(Vector<Long> cellsId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increaseAssociationsCostToCellsId(Long[] obIdArray)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseAssociationsCost(Vector<Long> associationsId)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
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

	@Override
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Cell> newCells)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Cell> insertNewCells(Vector<Thought> thoughtPairsToUnion)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCells(List<Long> idVector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanMemoryFromUselessCells() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAssociationCost() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEmptyRows() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalize() throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxLevel(Long srcID, Long tgtID) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long getCell(Long srcCellID, Long tgtCellID,
			Long synteticProperty) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Cell> getLeftNeighbours(String src) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getSrcToCells(Long[] inputCells)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSrcAndTgt(Long srcCell, Long tgtCell) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringToTwoAssociations(Long lastAssoc, Long nextAssoc)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSrc(String semicolonSeparategId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSrc(Vector<Long> idVector) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSrc(Long cellId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet executeQuery(String sql) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
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

	@Override
	public Long getMaxAssociationsId() throws SQLException {
		Session session = getSession();
		Query q = session.createQuery("select max(id) from Associations");
		return (Long)q.list().get(0);
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Associations> getAllAssociationsLowerThenCost(List<Long> cellsId,
			int lowestCostForLeaving) {
		Session s = getSession();
		Query q = s.createQuery("from Associations where cost < :lowestCost and id in (:cellsId)")
			.setParameterList("cellsId", cellsId)
			.setParameter("lowestCost", (long)lowestCostForLeaving);
		return (List<Associations>)q.list();
	}

	@Override
	public void deleteAssociations(List<Long> assocIdToDelete) {
		Session s = getSession();
		s.createQuery("delete from Associations where id in :param")
			.setParameterList("param", assocIdToDelete)
			.executeUpdate();
		return;
	}

	@Override
	public Vector<Cell> getCells(Vector<Long> missingCellsId)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Associations getAssociation(Thought srcThought, Thought tgtThought)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteEverything() {
		Session s = getSession();
		s.createQuery("delete from Associations").executeUpdate();
		s.createQuery("delete from Cell").executeUpdate();
	}

	@Override
	public Long getAssociationsCount() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select count(id) from Associations").uniqueResult();
	}

	@Override
	public Long getCellsCount() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select count(id) from Cells").uniqueResult();
	}

	@Override
	public Long getMaxCellsId() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select max(id) from Cells").uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> getAvailableCellsIdList() throws SQLException {
		int numberOfAvailableCellsIdToReturn = getConfig().getDbViewer_numberOfAvailableCellsIdToReturn();
		Long maxCellsId = getMaxCellsId();
		ArrayList<Long> result = new ArrayList<Long>(numberOfAvailableCellsIdToReturn);
		// Select with limit
		Query query = getSession().createQuery("FROM Cells c WHERE type = 0");
		query.setMaxResults(numberOfAvailableCellsIdToReturn);
		query.executeUpdate();
		result.addAll(query.list());
		// If there are no enough free IDs
		while (result.size() < numberOfAvailableCellsIdToReturn) {
			result.add(++maxCellsId);
		}
		return result;
	}

	/**
	 * @return the {@link Config}<br>
	 * See {@link HibernateDBViewer#config}
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * @param config the {@link Config} to set<br>
	 * See {@link HibernateDBViewer#config}
	 */
	public void setConfig(Config config) {
		this.config = config;
	}
}
