package cz.semenko.word.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;

/**
 * DAO object that direct calls java.sql API
 * @author Kyrylo Semenko
 *
 */
public class JdbcDBViewer implements DBViewer {
	private static final String COMMA = ",";
	// Interface field
	private Config config;
	// Private fields
	private Connection connection;
	private DBconnector dbConnector;
	private TablesManager tablesManager;
	
	private PreparedStatement selectWordSRC;
	private PreparedStatement selectWordID;
	private PreparedStatement selectLeftNeighbours;
	private PreparedStatement selectCellsToAssociation;
	private PreparedStatement selectCell;
	private PreparedStatement selectMaxLevel;
	private PreparedStatement selectSrc;
	
	public static Logger logger = Logger.getLogger(JdbcDBViewer.class);
	
	/** Constructor */
	public JdbcDBViewer(DBconnector dbConnector) throws SQLException {
		this.setDbConnector(dbConnector);
		this.connection = dbConnector.getConnection();
		prepareStatements();
	}

	private void prepareStatements() {
		try {
            selectWordSRC = connection.prepareStatement("SELECT src FROM " +
            		"cells WHERE id = ?");
            selectLeftNeighbours = connection.prepareStatement("SELECT src_id FROM " +
    				"associations WHERE tgt_id = ?");
            selectCellsToAssociation = connection.prepareStatement("SELECT src_id, tgt_id " +
            		"FROM associations WHERE id = ?");
            selectCell = connection.prepareStatement("SELECT src, type FROM cells " +
            		"WHERE id = ?"); 
            selectWordID = connection.prepareStatement("SELECT id FROM " +
    			"cells WHERE src LIKE ?");
		    selectMaxLevel = connection.prepareStatement("SELECT MAX(type) FROM cells " +
		    		"WHERE id = ? OR id = ?");
		    selectSrc = connection.prepareStatement("SELECT src FROM cells WHERE id = ?");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param config
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public String getSrc(Long cellId) throws Exception {
		String result = null;
		selectSrc.setLong(1, cellId);
		ResultSet rs = selectSrc.executeQuery();
		if (rs.next() == false) {
			rs.close();
			return null;
		} else {
			result = rs.getString("src");
		}
		if (rs.next()) {
			rs.close();
			throw new Exception("Multiple cells was found: id=" + cellId);
		}
		rs.close();
		return result;
	}
	
	@Override
	public String getSrc(Vector<Long> idVector) throws SQLException {
		StringBuilder result = new StringBuilder();
		//boolean goodProbability = false;
		Map<Long, Cell> cells = new TreeMap<Long, Cell>();
		while (true) {
			StringBuilder buff = new StringBuilder();
			/* Ziskame objekty z idecek */
			for (int i = 0; i < idVector.size(); i++) {
				Long nextId = idVector.get(i);
				if (cells.containsKey(nextId) == false) {
					buff.append(nextId + COMMA);
				}
			}
			if (buff.length() == 0) { // Jiz mame vsechny objekty s type, ktere maji neprazdne src
				break;
			}
			buff.insert(0, "SELECT * FROM cells WHERE id IN (");
			buff.delete(buff.length()-1, buff.length());
			buff.append(")");
			ResultSet rs = connection.createStatement().executeQuery(buff.toString());
			while (rs.next() == true) {
				Cell ob = new Cell(
						rs.getLong("id"), 
						rs.getString("src"), 
						rs.getLong("type"));
				cells.put(ob.getId(), ob);
			}
			rs.close();
			/* Pro ojekty vyssi nez typ, ktery jeste ma src, zjistime podobjekty */
			StringBuilder selectAssocBuff = new StringBuilder();
			for (int i = 0; i < idVector.size(); i++) {
				Long nextId = idVector.get(i);
				Cell nextOb = cells.get(nextId);
				if (nextOb == null) {
					return ("SRC nenalezeno. ID=" + nextId);
				}
				if (nextOb.getType() > 2) { //TODO vytvorit parametr v konfiguraku
					selectAssocBuff.append(nextOb.getId() + COMMA);
				}
			}
			if (selectAssocBuff.length() == 0) { // Jiz vsechny objekty maji neprazdne src
				break;
			}
			selectAssocBuff.insert(0, "SELECT cell_id, src_id, tgt_id FROM associations " +
					"WHERE cell_id IN (");
			selectAssocBuff.delete(selectAssocBuff.length()-1, selectAssocBuff.length());
			selectAssocBuff.append(")");
			ResultSet assocRS = connection.createStatement().executeQuery(selectAssocBuff.toString());
			Vector<Associations> associations = new Vector<Associations>();
			while(assocRS.next()) {
				Associations assoc = new Associations(
						null, 
						assocRS.getLong("cell_id"), 
						assocRS.getLong("src_id"),
						null, 
						assocRS.getLong("tgt_id"), 
						null, 
						null);
				associations.add(assoc);
			}
			assocRS.close();
			// Rozsirime idVector - nahradime id objektu ideckem src_id a tgt_id
			for (int i = 0; i < idVector.size(); i++) {
				Long nextId = idVector.get(i);
				for (int k = 0; k < associations.size(); k++) {
					Associations nextAssoc = associations.get(k);
					if (nextAssoc.getObjId().compareTo(nextId) == 0) {
						idVector.remove(i);
						idVector.insertElementAt(nextAssoc.getSrcId(), i);
						idVector.insertElementAt(nextAssoc.getTgtId(), i+1);
						continue;
					}
				}
			}
		}
		for (int i = 0; i < idVector.size(); i++) {
			Long nextId = idVector.get(i);
			result.append(cells.get(nextId).getSrc());
		}
		return result.toString();
	}
	
	@Override
	public String getSrc(String semicolonSeparategId) throws SQLException {
		String[] splitId = semicolonSeparategId.split(";");
		Vector<Long> idVector = new Vector<Long>();
		for (int i = 0; i < splitId.length; i++) {
			String val = splitId[i].trim();
			if (val.isEmpty()) {
				continue;
			}
			try {
				idVector.add(Long.valueOf(splitId[i]));
			} catch (NumberFormatException e) {
				return "Nespravny format cisla: " + val;
			}
		}
		return getSrc(idVector);
	}
	
	@Override
	public synchronized String getStringToTwoAssociations
		(Long lastAssoc, Long nextAssoc) throws Exception {
		if (lastAssoc == null || nextAssoc == null) {
			return null;
		}
		StringBuilder result = new StringBuilder();
		selectCellsToAssociation.setLong(1, lastAssoc);
		ResultSet rs = selectCellsToAssociation.executeQuery();
		ResultSet wordRS;
		if (rs.next() == true) {
			// first path
			Long firstObID = rs.getLong("src_id");
			Long secondObID = rs.getLong("tgt_id");
			selectWordSRC.setLong(1, firstObID);
			wordRS = selectWordSRC.executeQuery();
			if (wordRS.next() == false) {
				return null;
			}
			result.append(wordRS.getString("src"));
			result.append("|");
			// second path
			selectWordSRC.setLong(1, secondObID);
			wordRS = selectWordSRC.executeQuery();
			if (wordRS.next() == false) {
				return null;
			}
			result.append(wordRS.getString("src"));
			result.append("|");			
		} else {
			return null;
		}
		// third path
		selectCellsToAssociation.setLong(1, nextAssoc);
		rs = selectCellsToAssociation.executeQuery();
		if (rs.next() == false) {
			return null;
		}
		Long thirdObID = rs.getLong("src_id");
		selectWordSRC.setLong(1, thirdObID);
		wordRS = selectWordSRC.executeQuery();
		if (wordRS.next() == false) {
			wordRS.close();
			rs.close();
			return null;
		}
		result.append(wordRS.getString("src"));
		wordRS.close();
		rs.close();
		return result.toString();
	}

	@Override
	public synchronized String getSrcAndTgt(Long srcCell, Long tgtCell) throws Exception {
		String result;
		selectWordSRC.setLong(1, srcCell);
		ResultSet rs = selectWordSRC.executeQuery();
		if (rs.next() == true) {
			result = rs.getString("src");
		} else {
			result = "NULL";
		}
		selectWordSRC.setLong(1, tgtCell);
		rs = selectWordSRC.executeQuery();
		if (rs.next() == true) {
			result = result.concat("|" + rs.getString("src"));
		} else {
			result = result.concat("|NULL");
		}
		rs.close();
		return result;
	}
	
	@Override
	public Map<Long, String> getSrcToCells(Long[] inputCells) throws Exception {
		Map<Long, String> result = new TreeMap<Long, String>();
		for (int i = 0; i < inputCells.length; i++) {
			Long nextId = inputCells[i];
			String src = getSrc(nextId);
			result.put(nextId, src);
		}		
		return result;
	}

	@Override
	public synchronized Vector<Cell> getLeftNeighbours(String src) throws SQLException {
		Vector<Cell> result = new Vector<Cell>();
		selectWordID.setString(1, src);
		ResultSet rs = selectWordID.executeQuery();
		while (rs.next() == true) {
			Long id = rs.getLong("id");
			selectLeftNeighbours.setLong(1, id);
			ResultSet rsLeftNe = selectLeftNeighbours.executeQuery();
			while (rsLeftNe.next() == true) {
				Long obID = rsLeftNe.getLong(1);
				selectCell.setLong(1, obID);
				ResultSet rsOb = selectCell.executeQuery();
				rsOb.next();
				Cell node = new Cell();
				node.setId(obID);
				node.setSrc((String)rsOb.getString("src"));
				node.setType((Long)rsOb.getLong("type"));
				result.add(node);
			}
			rsLeftNe.close();
		}
		rs.close();
		return result;
	}
	
	@Override
	public Long getCell(Long srcCellID, Long tgtCellID, Long synteticProperty) throws Exception {
//		Long id = null;
//		selectCellForAssoc.setLong(1, srcCellID);
//		selectCellForAssoc.setLong(2, tgtCellID);
//		ResultSet rs = selectCellForAssoc.executeQuery();
//		rs.next();
//		if ((id = rs.getLong(1)) > 0) { // TODO co jsem to navyvijel?
//			// Increase used association cost
//			updateCostToAssoc.setLong(1, srcCellID);
//			updateCostToAssoc.setLong(2, tgtCellID);
//			if (updateCostToAssoc.executeUpdate() == 0) {
//				insertAssociation.setLong(1, srcCellID);
//				insertAssociation.setLong(2, 1);
//				insertAssociation.setLong(3, tgtCellID);
//				insertAssociation.setLong(4, 1);
//				insertAssociation.setLong(5, 1);
//				insertAssociation.execute();
//			}
//			// Add current synthetic property if not exists
//			if (synteticProperty != null) {
//				selectAssociation.setLong(1, id);
//				selectAssociation.setLong(2, synteticProperty);
//				ResultSet assRs = selectAssociation.executeQuery();
//				if (assRs.next()) {
//					// Nothing, association exists
//				} else {
//					// create a new one
//					insertAssociation.setLong(1, id);
//					insertAssociation.setLong(2, 1);
//					insertAssociation.setLong(3, synteticProperty);
//					insertAssociation.setLong(4, 1);
//					insertAssociation.setLong(5, 1);
//					insertAssociation.execute();
//				}
//				assRs.close();
//			}
//		} else {
//			id = null;
//		}
//		rs.close();
//		return id;
		return null;
	}
	
	@Override
	public int getMaxLevel(Long srcID, Long tgtID) throws Exception {
		selectMaxLevel.setLong(1, srcID);
		selectMaxLevel.setLong(2, tgtID);
		ResultSet rs = selectMaxLevel.executeQuery();
		if (rs.next() == true) {
			return rs.getInt(1);
		}
		rs.close();
		return 0;
	}
	
	@Override
	protected void finalize() throws Throwable {
	    connection.close();
	}

	@Override
	public void resetAssociationCost() throws SQLException {
		String sql = "UPDATE associations SET cost=0 WHERE cost > 0";
		connection.createStatement().executeUpdate(sql);
	}

	@Override
	public void cleanMemoryFromUselessCells() throws SQLException {
		int numRows = 200;
		for (int i = 0; i <= getTablesManager().getMaxCellsId(); i = i + numRows) {
			// Overujeme postupne sadu po sade
			StringBuilder ids = new StringBuilder();
			Vector<Long> cellsIdVector = new Vector<Long>();
			for (int k = i; k < i + numRows; k++) {
				ids.append(k + COMMA);
				cellsIdVector.add((long)k);
			}
			ids.delete(ids.length()-1, ids.length());
			String sqlSelectAssoc = "SELECT * from ASSOCIATIONS where " +
				"cell_id IN (" + ids.toString() + ") OR src_id IN (" + ids.toString()
				+ ") OR tgt_id IN (" + ids.toString() + ")";
			ResultSet selAssocRS = connection.createStatement().executeQuery(sqlSelectAssoc);
			Vector<Long> usedCells = new  Vector<Long>();
			// Ziskame vsechny objekty ktere se pouzivaji
			while (selAssocRS.next()) {
				Long cellId = selAssocRS.getLong("cell_id");
				Long srcId = selAssocRS.getLong("src_id");
				Long tgtId = selAssocRS.getLong("tgt_id");
				if (usedCells.contains(cellId) == false) {
					usedCells.add(cellId);
				}
				if (usedCells.contains(srcId) == false) {
					usedCells.add(srcId);
				}
				if (usedCells.contains(tgtId) == false) {
					usedCells.add(tgtId);
				}
			}
			selAssocRS.close();
			// Ziskame objekty k odstraneni
			cellsIdVector.removeAll(usedCells);
			if (cellsIdVector.size() == 0) {
				continue;
			}
			StringBuilder cellsToRemove = new StringBuilder();
			for (int k = 0; k < cellsIdVector.size(); k++) {
				cellsToRemove.append(cellsIdVector.get(k) + COMMA);
			}
			cellsToRemove.delete(cellsToRemove.length()-1, cellsToRemove.length());
			logger.info("Number of cells to remove: " + cellsToRemove.toString());
			String sqlDelete = "DELETE FROM cells WHERE id IN (" + cellsToRemove.toString()	+ ")";
			connection.createStatement().executeUpdate(sqlDelete);
		}		
	}

	@Override
	public ResultSet executeQuery(String sql) throws Exception {
		return connection.createStatement().executeQuery(sql);
	}
	
	@Override
	public void deleteCells(List<Long> cellsIdToDelete) throws SQLException {
		StringBuilder sql = new StringBuilder("DELETE FROM cells WHERE id IN (");
		for (Long nextId : cellsIdToDelete) {
			sql.append(nextId);
			sql.append(COMMA);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		
		connection.createStatement().execute(sql.toString());
		
	}

	@Override
	public Vector<Cell> insertNewCells(Vector<Thought> thoughtPairsToUnion) throws Exception {
		Vector<Cell> result = new Vector<Cell>();
		connection.setAutoCommit(false);
		try {
			StringBuilder buff = new StringBuilder();
			for (int i = 0; i < thoughtPairsToUnion.size()-1; i = i+2) {
				Thought th1 = thoughtPairsToUnion.get(i);
				Thought th2 = thoughtPairsToUnion.get(i+1);
				Long type = th1.getActiveCell().getType() + th2.getActiveCell().getType();
				// TODO odstranit po uspesnem testovani
				if (type > config.getKnowledge_relateThoughtsUpToCellType() * 2) {
					throw new Exception ("Typ noveho objektu je vyssi nez je povoleno v konfiguracnim souboru." +
							" Typ objektu = " + type + "\r\nThought 1 = " + th1 + "\r\nThought 2 = " + th2);
				}
				String src = "";
				if (type < config.getDbViewer_maxTextLengthToSave()) {
					src = th1.getActiveCell().getSrc() + th2.getActiveCell().getSrc();
					src = src.replaceAll("([^']|^)'([^']|$)", "$1''$2");
				}
				Long nextId = getTablesManager().getNextCellsId();
				buff.append("(" + nextId + ", '" + src + "', " + type + "), ");
				result.add(new Cell(nextId, src, type));
			}
			if (buff.length() == 0) {
				return result;
			}
			buff.delete(buff.length() - 2, buff.length());
			String sql = "INSERT INTO CELLS (id, src, type) VALUES " + buff.toString();
			connection.createStatement().executeUpdate(sql);
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			logger.error(e.getMessage(), e);
			System.out.println(e.getMessage() + e);
			System.exit(1);
		}		
		connection.setAutoCommit(true);
		return result;
	}

	@Override
	public Vector<Associations> insertAssociations(Vector<Thought> thoughtPairsToUnion,
			Vector<Cell> newCells) throws SQLException {
		Vector<Associations> result = new Vector<Associations>();
		Iterator<Cell> iter = newCells.iterator();
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < thoughtPairsToUnion.size() - 1; i = i+2) {
			Thought srcThought = thoughtPairsToUnion.get(i);
			Thought tgtThought = thoughtPairsToUnion.get(i+1);
			Cell ob = iter.next();
			long id = tablesManager.getNextAssociationsId();
			long src_id = srcThought.getActiveCell().getId();
			long src_tbl = 1L; // TODO pridat moznost ukladani do vice tabulek a databazi
			long tgt_id = tgtThought.getActiveCell().getId();
			long tgt_tbl = 1L;
			long cost = 0L;
			Long cell_id = ob.getId();
			buff.append("(" 
					+ id + ", "
					+ src_id + ", " 
					+ src_tbl + ", "
					+ tgt_id + ", "
					+ tgt_tbl + ", "
					+ cost + ", "
					+ cell_id + "), ");
			result.add(new Associations(id, cell_id, src_id, src_tbl, tgt_id, tgt_tbl, cost));
		}
		if (buff.length() == 0) {
			return result;
		}
		buff.delete(buff.length()-2, buff.length());
		String sql = "INSERT INTO associations (id, src_id, src_tbl, tgt_id, tgt_tbl, cost, cell_id) " + 
		"VALUES " + buff.toString();
		connection.createStatement().executeUpdate(sql);
		return result;
	}

	@Override
	public Vector<Cell> getNewPrimitiveCells(Vector<Character> nonExistent) throws Exception {
		Vector<Cell> result = new Vector<Cell>();
		Map<Character, Cell> tempMapOfCharsToId = new TreeMap<Character, Cell>();
		StringBuilder buff = new StringBuilder();
		
		for (int i = 0; i < nonExistent.size(); i++) {
			// Neukladame do DB zdvojene a opakujici se znaky
			Character nextCharacter = nonExistent.get(i);
			if (tempMapOfCharsToId.containsKey(nextCharacter)) {
				Cell ob = tempMapOfCharsToId.get(nextCharacter);
				result.add(ob);
				continue;
			}
			String character = nextCharacter.toString();
			character = character.replaceAll("'", "''");
			Long nextId = tablesManager.getNextCellsId();
			Cell ob = new Cell(nextId, nextCharacter.toString(), 1L);
			buff.append("(" + nextId + ", '" + character + "', 1), ");
			tempMapOfCharsToId.put(nextCharacter, ob);
			result.add(ob);
		}
		buff.delete(buff.length() - 2, buff.length());
		String sqlIns = "INSERT INTO cells (id, src, type) VALUES "; //(?, 1), (?, 1)...
		buff.insert(0, sqlIns);		
		connection.createStatement().executeUpdate(buff.toString());		
		return result;
	}

	@Override
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		if (associationsId.size() == 0) {
			return;
		}
		StringBuilder updateCostSql = new StringBuilder("UPDATE associations SET cost=cost+1 WHERE id IN (");
		for (int i = 0; i < associationsId.size(); i++) {
			updateCostSql.append(associationsId.get(i) + ", ");
		}
		updateCostSql.delete(updateCostSql.length()-2, updateCostSql.length());
		updateCostSql.append(")");
		connection.createStatement().executeUpdate(updateCostSql.toString());
	}

	@Override
	public void increaseAssociationsCostToCellsId(Long[] obIdArray) throws SQLException {
		if (obIdArray.length == 0) {
			return;
		}
		StringBuilder updateCostSql = new StringBuilder("UPDATE associations SET cost=cost+1 WHERE cell_id IN (");
		for (int i = 0; i < obIdArray.length; i++) {
			updateCostSql.append(obIdArray[i] + ", ");
		}
		updateCostSql.delete(updateCostSql.length()-2, updateCostSql.length());
		updateCostSql.append(")");
		connection.createStatement().executeUpdate(updateCostSql.toString());
	}

	@Override
	public Vector<Associations> getAllAssociations(Vector<Long> cellsId) throws Exception {
		Vector<Associations> result = new Vector<Associations>();
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < cellsId.size(); i++) {
			buff.append(cellsId.get(i) + ", ");
		}
		if (buff.length() > 0) {
			buff.delete(buff.length()-2, buff.length());
		} else {
			return result;
		}
		String sql = "SELECT * FROM associations WHERE src_id IN (" + buff.toString() + ")";
		ResultSet rs = connection.createStatement().executeQuery(sql);
		while (rs.next()) {
			Long id = rs.getLong("id");
			Long cellId = rs.getLong("cell_id");
			Long srcId = rs.getLong("src_id");
			Long srcTable = rs.getLong("src_tbl");
			Long tgtId = rs.getLong("tgt_id");
			Long tgtTable = rs.getLong("tgt_tbl");
			Long cost = rs.getLong("cost");
			Associations ass = new Associations(id, cellId, srcId, srcTable, tgtId, tgtTable, cost);
			result.add(ass);
		}
		rs.close();
		return result;
	}

	@Override
	public Vector<Long> getSuperiorCellsId(Vector<Long> pairsToFind) throws SQLException {
		Vector<Long> result = new Vector<Long>();
		StringBuilder srcBuff = new StringBuilder();
		StringBuilder tgtBuff = new StringBuilder();
		for (int i = 0; i < pairsToFind.size()-1; i=i+2) {
			Long srcId = pairsToFind.get(i);
			Long tgtId = pairsToFind.get(i+1);
			srcBuff.append(srcId + COMMA);
			tgtBuff.append(tgtId + COMMA);
		}
		srcBuff.deleteCharAt(srcBuff.length()-1);
		tgtBuff.deleteCharAt(tgtBuff.length()-1);
		String sql = "SELECT cell_id, src_id, tgt_id FROM associations " +
				"WHERE src_id IN (" + srcBuff.toString() + ") AND " +
				"tgt_id IN (" + tgtBuff.toString() + ")";
		ResultSet rs = connection.createStatement().executeQuery(sql);
		Vector<long[]> rsVector = new Vector<long[]>();
		while (rs.next()) {
			Long srcId = rs.getLong("src_id");
			Long tgtId = rs.getLong("tgt_id");
			Long cellId = rs.getLong("cell_id");
			rsVector.add(new long[]{cellId, srcId, tgtId});
		}
		rs.close();
		// vyplnit result
		label:
		for (int i = 0; i < pairsToFind.size()-1; i=i+2) {
			Long srcId = pairsToFind.get(i);
			Long tgtId = pairsToFind.get(i+1);
			for (int m = 0; m < rsVector.size(); m++) {
				long[] next = rsVector.get(m);
				if (next[1] == srcId && next[2] == tgtId) {
					result.add(next[0]);
					continue label;
				}
			}
			result.add(null);
		}
		return result;
	}

	@Override
	public Long getMaxAssociationsId() throws SQLException {
		String sql2 = "SELECT MAX(id) FROM associations";
	    ResultSet rs2 = connection.createStatement().executeQuery(sql2);
	    if (rs2.next()) {
	    	return rs2.getLong(1);
	    }
	    rs2.close();
	    return null;
	}

	@Override
	public List<Associations> getAssociations(long minId, long maxId,
			int lowestCostForLeaving) throws SQLException {
		String sql = "SELECT * FROM associations WHERE id >= " + minId
			+ " AND id <= " + maxId + " AND cost < " + lowestCostForLeaving;
		ResultSet rs = connection.createStatement().executeQuery(sql);
		List<Associations> result = new Vector<Associations>();
		while(rs.next()) {
			Long id = rs.getLong("id");
			Long cellId = rs.getLong("cell_id");
			Long srcId = rs.getLong("src_id");
			Long srcTable = rs.getLong("src_tbl");
			Long tgtId = rs.getLong("tgt_id");
			Long tgtTable = rs.getLong("tgt_tbl");
			Long cost = rs.getLong("cost");
			Associations assoc = new Associations(id, cellId, srcId, srcTable, tgtId, tgtTable, cost);
			result.add(assoc);
		}
		rs.close();
		return result;
	}

	public List<Cell> getPrimitiveCells(List<Character> missingChars) throws SQLException {
		List<Cell> result = new Vector<Cell>();
		if (missingChars.size() == 0) {
			return result;
		}
		String query = "SELECT * FROM cells " +
    		"WHERE type = 1 AND src IN (";
		StringBuilder param = new StringBuilder();
		char singleQuote = '\'';
		for (Character next : missingChars) {
			if (next.compareTo(singleQuote) == 0) {
				param.append("'''',");
			} else {
				param.append("'" + next + "',");
			}
		}
		param.deleteCharAt(param.length()-1);
		try {
			ResultSet rs = connection.createStatement().executeQuery(query + param.toString() + ")");
			while (rs.next()) {
				Long id = rs.getLong("id");
				String src = rs.getString("src");
				Long type = rs.getLong("type");
				Cell nextOb = new Cell(id, src, type);
				result.add(nextOb);
			}
			rs.close();
		} catch (SQLSyntaxErrorException e) {
			logger.error(e.getMessage() + " Query: " + query + param.toString() + ")", e);
			throw new SQLException(e);
		}
		return result;
	}

	@Override
	public List<Associations> getAllAssociationsLowerThenCost(List<Long> cellsId, int lowestCostForLeaving) throws SQLException {
		StringBuilder sql = new StringBuilder("SELECT * FROM associations WHERE cell_id IN (");
		for (Long nextId : cellsId) {
			sql.append(nextId);
			sql.append(COMMA);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") AND cost < ");
		sql.append(lowestCostForLeaving);
		
		ResultSet rs;
		List<Associations> result = new Vector<Associations>();
				
		rs = connection.createStatement().executeQuery(sql.toString());
		while(rs.next()) {
			Long id = rs.getLong("id");
			Long cellId = rs.getLong("cell_id");
			Long srcId = rs.getLong("src_id");
			Long srcTable = rs.getLong("src_tbl");
			Long tgtId = rs.getLong("tgt_id");
			Long tgtTable = rs.getLong("tgt_tbl");
			Long cost = rs.getLong("cost");
			Associations assoc = new Associations(id, cellId, srcId, srcTable, tgtId, tgtTable, cost);
			result.add(assoc);
		}
		rs.close();		
		
		return result;
	}

	@Override
	public void deleteAssociations(List<Long> assocIdToDelete) throws SQLException {
		StringBuilder sql = new StringBuilder("DELETE FROM associations WHERE id IN (");
		for (Long nextId : assocIdToDelete) {
			sql.append(nextId);
			sql.append(COMMA);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		
		connection.createStatement().execute(sql.toString());
	}

	@Override
	public Vector<Cell> getCells(Vector<Long> missingCellsId)
			throws SQLException {
		Vector<Cell> result = new Vector<Cell>();
		result.setSize(missingCellsId.size());
		StringBuffer sql = new StringBuffer("SELECT * FROM cells WHERE id IN (");
		for (int i = 0; i < missingCellsId.size(); i++) {
			sql.append(missingCellsId.get(i) + ", ");
		}
		sql.delete(sql.length()-2, sql.length());
		sql.append(")");
		ResultSet rs = connection.createStatement().executeQuery(sql.toString());
		Map<Long, Cell> cellsMap = new TreeMap<Long, Cell>();
		while (rs.next()) {
			Long id = rs.getLong("id");
			String src = rs.getString("src");
			Long type = rs.getLong("type");
			Cell ob = new Cell(id, src, type);
			cellsMap.put(id, ob);
		}
		// Vyplni result
		for (int i = 0; i < missingCellsId.size(); i++) {
			Long id = missingCellsId.get(i);
			result.set(i, cellsMap.get(id));
		}
		return result;
	}

	@Override
	public Associations getAssociation(Thought srcThought, Thought tgtThought)
			throws SQLException {
		Associations result = null;
		String sql = "SELECT * FROM ASSOCIATIONS WHERE src_id = " + srcThought.getActiveCell().getId()
			+ " AND tgt_id = " + tgtThought.getActiveCell().getId();
		ResultSet rs = connection.createStatement().executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			if (i > 0) {
				throw new SQLException("V tabulce ASSOCIATIONS byly nalezeny dva vyskyty Associace se stejmymi src_id a tgt_id: " + result.toString());
			}
			long id = rs.getLong("id");
			long srcId = rs.getLong("src_id");
			long srcTable = rs.getLong("src_tbl");
			long tgtId = rs.getLong("tgt_id");
			long tgtTable = rs.getLong("tgt_tbl");
			long cost = rs.getLong("cost");
			Long cellId = rs.getLong("cell_id");
			result = new Associations(id, cellId, srcId, srcTable, tgtId, tgtTable, cost);
			i++;
		}
		return result;
	}

	@Override
	public void deleteEverything() throws SQLException {
		String sql = "DELETE FROM associations";
		connection.createStatement().executeUpdate(sql);
		sql = "DELETE FROM cells";
		connection.createStatement().executeUpdate(sql);
	}

	@Override
	public Long getAssociationsCount() throws SQLException {
		Long result = null;
		String sql = "SELECT COUNT(id) FROM associations";
		ResultSet rs = connection.createStatement().executeQuery(sql);
		rs.next();
		result = rs.getLong(1);
		rs.close();
		return result;
	}

	@Override
	public Long getCellsCount() throws SQLException {
		Long result = null;
		String sql = "SELECT COUNT(id) FROM cells";
		ResultSet rs = connection.createStatement().executeQuery(sql);
		rs.next();
		result = rs.getLong(1);
		rs.close();
		return result;
	}

	@Override
	public Long getMaxCellsId() throws SQLException {
		Long result = null;
	    String sql = "SELECT MAX(id) FROM cells";
	    ResultSet rs = connection.createStatement().executeQuery(sql);
	    if (rs.next()) {
	    	result = rs.getLong(1);
	    }
	    rs.close();
	    return result;
	}

	@Override
	public Collection<Long> getAvailableCellsIdList() throws SQLException {
		int numberOfAvailableCellsIdToReturn = config.getDbViewer_numberOfAvailableCellsIdToReturn();
		Long maxCellsId = getMaxCellsId();
		ArrayList<Long> result = new ArrayList<Long>(numberOfAvailableCellsIdToReturn);
		// Select with limit
		String sql = 
			"SELECT tmp.id FROM ("
			    + "SELECT ROW_NUMBER() OVER() AS rownum, cells.id "
			    + "FROM cells WHERE type = 0"
			+ ") AS tmp "
			+ "WHERE rownum <= " + numberOfAvailableCellsIdToReturn;
		ResultSet rs = connection.createStatement().executeQuery(sql);
		while (rs.next()) {
			result.add(rs.getLong("id"));
		}
		// If there are no enough free IDs
		while (result.size() < numberOfAvailableCellsIdToReturn) {
			result.add(++maxCellsId);
		}
		return result;
	}

	/**
	 * @return the {@link TablesManager}<br>
	 * See {@link JdbcDBViewer#tablesManager}
	 */
	public TablesManager getTablesManager() {
		return tablesManager;
	}

	/**
	 * @param tablesManager the {@link TablesManager} to set<br>
	 * See {@link JdbcDBViewer#tablesManager}
	 */
	public void setTablesManager(TablesManager tablesManager) {
		this.tablesManager = tablesManager;
	}

	/**
	 * @return the {@link DBconnector}<br>
	 * See {@link JdbcDBViewer#dbConnector}
	 */
	public DBconnector getDbConnector() {
		return dbConnector;
	}

	/**
	 * @param dbConnector the {@link DBconnector} to set<br>
	 * See {@link JdbcDBViewer#dbConnector}
	 */
	public void setDbConnector(DBconnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	@Override
	public Collection<Long> getAvailableAssociationsIdList()
			throws SQLException {
		int numberOfAvailableAssociationsIdToReturn = config.getDbViewer_numberOfAvailableAssociationsIdToReturn();
		Long maxAssociationsId = getMaxAssociationsId();
		ArrayList<Long> result = new ArrayList<Long>(numberOfAvailableAssociationsIdToReturn);
		// Select with limit
		String sql = 
			"SELECT tmp.id FROM ("
			    + "SELECT ROW_NUMBER() OVER() AS rownum, associations.id "
			    + "FROM associations WHERE src_id = 0 AND tgt_id = 0"
			+ ") AS tmp "
			+ "WHERE rownum <= " + numberOfAvailableAssociationsIdToReturn;
		ResultSet rs = connection.createStatement().executeQuery(sql);
		while (rs.next()) {
			result.add(rs.getLong("id"));
		}
		// If there are no enough free IDs
		while (result.size() < numberOfAvailableAssociationsIdToReturn) {
			result.add(++maxAssociationsId);
		}
		return result;
	}
}
