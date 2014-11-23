package cz.semenko.word.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
	private HibernateSessionFactory hibernateSessionFactory;
	private TablesManager tablesManager;

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

	@Override
	public Vector<Cell> createNewPrimitiveCells(Vector<Character> newChars)	throws Exception {
		Vector<Cell> result = new Vector<Cell>();
		Map<String, Cell> map = new TreeMap<String, Cell>();
		
		Session sess = getSession();
		for (Character nextChar : newChars) {
			Cell cell = map.get(nextChar.toString());
			if (cell == null) {
				cell = new Cell();
				Long nextId = tablesManager.getNextCellsId();
				cell.setId(nextId);
				cell.setSrc(nextChar.toString());
				cell.setType(Cell.TYPE_PRIMITIVE);
				map.put(nextChar.toString(), cell);
				try {
					sess.save(cell);
				} catch (Exception e) {
					tablesManager.moveBackNextCellsId(nextId);
					throw e;
				}
			}
			result.add(cell);
		}
		
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
	public void markCellsAsAvailableForReuse(List<Long> idVector) {
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
		return getHibernateSessionFactory().getSessionFactory().openSession();
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
	public void markAssociationsAsAvailableForReuse(List<Long> assocIdToDelete) {
		Session s = getSession();
		s.createQuery("update Associations a set a.cell_id = 0, a.srsId = 0, a.tgtId = 0, cost = 0 where id in :param")
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
		s.createQuery("delete from Cell where id > " + Cell.DUMMY_CELL_ID).executeUpdate();
	}

	@Override
	public Long getAssociationsCount() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select count(id) from Associations").uniqueResult();
	}

	@Override
	public Long getCellsCount() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select count(id) from Cell").uniqueResult();
	}

	@Override
	public Long getMaxCellsId() throws SQLException {
		Session s = getSession();
		return (Long)s.createQuery("select max(id) from Cell").uniqueResult();
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

	@Override
	public Collection<Long> getAvailableCellsIdList() throws SQLException {
		int numberOfAvailableCellsIdToReturn = getConfig().getDbViewer_numberOfAvailableCellsIdToReturn();
		Long maxCellsId = getMaxCellsId();
		Collection<Long> result = getCellsIdMarkedAsAvailable();
		// If there are no enough free IDs
		while (result.size() < numberOfAvailableCellsIdToReturn) {
			result.add(++maxCellsId);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> getCellsIdMarkedAsAvailable() throws SQLException {
		int numberOfAvailableCellsIdToReturn = getConfig().getDbViewer_numberOfAvailableCellsIdToReturn();
		ArrayList<Long> result = new ArrayList<Long>(numberOfAvailableCellsIdToReturn);
		// Select with limit
		Query query = getSession().createQuery("FROM Cell c WHERE c.type = 0");
		query.setMaxResults(numberOfAvailableCellsIdToReturn);
		result.addAll(query.list());
		return result;
	}

	@Override
	public Collection<Long> getAvailableAssociationsIdList()
			throws SQLException {
		int numberOfAvailableAssociationsIdToReturn = getConfig().getDbViewer_numberOfAvailableAssociationsIdToReturn();
		Long maxAssociationsId = getMaxAssociationsId();
		ArrayList<Long> result = getAssociationsIdMarkedAsAvailable();
		// If there are no enough free IDs
		while (result.size() < numberOfAvailableAssociationsIdToReturn) {
			result.add(++maxAssociationsId);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Long> getAssociationsIdMarkedAsAvailable()
			throws SQLException {
		int numberOfAvailableAssociationsIdToReturn = getConfig().getDbViewer_numberOfAvailableAssociationsIdToReturn();
		ArrayList<Long> result = new ArrayList<Long>(numberOfAvailableAssociationsIdToReturn);
		// Select with limit
		Query query = getSession().createQuery("FROM Associations a WHERE a.type = 0");
		query.setMaxResults(numberOfAvailableAssociationsIdToReturn);
		result.addAll(query.list());
		return result;
	}

	/**
	 * @return the {@link HibernateSessionFactory}<br>
	 * See {@link HibernateDBViewer#hibernateSessionFactory}
	 */
	public HibernateSessionFactory getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	/**
	 * @param hibernateSessionFactory the {@link HibernateSessionFactory} to set<br>
	 * See {@link HibernateDBViewer#hibernateSessionFactory}
	 */
	public void setHibernateSessionFactory(HibernateSessionFactory hibernateSessionFactory) {
		this.hibernateSessionFactory = hibernateSessionFactory;
	}

	/**
	 * @return the {@link TablesManager}<br>
	 * See {@link HibernateDBViewer#tablesManager}
	 */
	public TablesManager getTablesManager() {
		return tablesManager;
	}

	/**
	 * @param tablesManager the {@link TablesManager} to set<br>
	 * See {@link HibernateDBViewer#tablesManager}
	 */
	public void setTablesManager(TablesManager tablesManager) {
		this.tablesManager = tablesManager;
	}
}
