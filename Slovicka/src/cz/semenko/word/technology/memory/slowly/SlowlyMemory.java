package cz.semenko.word.technology.memory.slowly;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.dao.DBViewer;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.persistent.Tables;

/**
 * Třida reprezentuje pomalou paměť, která se nachází v DB.
 *
 * @author Kyrylo Semenko
 */
public class SlowlyMemory {
	/** Objekt pod spravou Spring FW */
	private DBViewer dbViewer;

	/**
	 * <p>Constructor for SlowlyMemory.</p>
	 *
	 * @param dbViewer a {@link cz.semenko.word.dao.DBViewer} object.
	 */
	public SlowlyMemory(DBViewer dbViewer) {
		this.dbViewer = dbViewer;
	}

	/**
	 * get Associations tables
	 *
	 * @throws java.sql.SQLException if any.
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<Associations> getAssociations() throws SQLException {
		//int tablesAssociationsSize = Config.getConfig().getInt("fastMemory.tablesCellsAndAssociationsSize");
		Collection<Associations> result = new Vector<Associations>();
		//Collection<Associations> result = DBViewer.getInstance().getAssociations(tablesAssociationsSize);
		return result;
	}

	/**
	 * <p>getCells.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Collection<Cell> getCells() throws SQLException {
		//int tablesCellsSize = Config.getConfig().getInt("fastMemory.tablesCellsAndAssociationsSize");
		Collection<Cell> result = new Vector<Cell>();
		//Collection<Cell> result = DBViewer.getInstance().getCells(tablesCellsSize);
		return result;
	}
	

	/**
	 * <p>getTables.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Collection<Tables> getTables() throws SQLException {
		//int tablesTablesSize = Config.getConfig().getInt("fastMemory.tablesTablesSize");
		//Collection<Tables> result = DBViewer.getInstance().getTables(tablesTablesSize);
		Collection<Tables> result = new Vector<Tables>();
		return result;
	}
	/**
	 * Nalezne v DB idecka masivu znaku. Jestli znak neexistuje - vytvori ho. Zdvojene a opakujici se znaky nevytvari nove,
	 * ale vrati jejich id.
	 *
	 * @param missingChars a {@link java.util.Vector} object.
	 * @return Pole Idecek znaku
	 * @throws SQLException if any.
	 * @throws java.lang.Exception if any.
	 */
	public Long[] getCharsId(Vector<Character> missingChars) throws Exception {
		Long[] result = new Long[missingChars.size()];
		List<Cell> primiteveCells = dbViewer.getPrimitiveCells((List<Character>)missingChars);
		Map<String, Cell> primCellsMap = new TreeMap<String, Cell>();
		for (Cell ob : primiteveCells) {
			primCellsMap.put(ob.getSrc(), ob);
		}
		// Get nonexists caracters
		Vector<Character> nonExistent = new Vector<Character>();// vector nenalezenych znaku
		for (Character ch : missingChars) {
			if (primCellsMap.containsKey(Character.toString(ch)) == false) {
				nonExistent.add(ch);
			}
		}
		if (nonExistent.size() > 0) {
			// Doplni objekty ktere jeste nejsou v DB
			// Ziskame klice vlozenych znaku
			Vector<Cell> newCells = dbViewer.getNewPrimitiveCells(nonExistent);
			for (Cell ob : newCells) {
				primCellsMap.put(ob.getSrc(), ob);
			}
		}
		for (int i = 0; i < missingChars.size(); i++) {
			String nextChar = Character.toString(missingChars.get(i));
			result[i] = primCellsMap.get(nextChar).getId();
		}
		return result;
	}

	/**
	 * Dostane z DB objekty dle zadanych ID. Nevytvari nove.
	 *
	 * @param missingCellsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Cell> getCells(Vector<Long> missingCellsId) throws Exception {
		return dbViewer.getCells(missingCellsId);
	}

	/**
	 * Vytvori novy Objekts pro par Thought.
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @return Cell
	 * @throws java.lang.Exception if any.
	 */
	public Cell getNewCell(Thought srcThought, Thought tgtThought) throws Exception {
		Cell result = null;
		Vector<Thought> thoughtPairsToUnion = new Vector<Thought>();
		thoughtPairsToUnion.add(srcThought);
		thoughtPairsToUnion.add(tgtThought);
		Vector<Cell> cells = getNewCells(thoughtPairsToUnion);
		result = cells.firstElement();
		return result;
	}

	/**
	 * Dohleda v DB Associations. Jestli nenajde, vrati null
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link cz.semenko.word.persistent.Associations} object.
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		return dbViewer.getAssociation(srcThought, tgtThought);
	}

	/**
	 * Zvysi COST associaci o jednicku jak v DB, tak i v cache
	 *
	 * @param associationsId - IDecka associaci v kterych se ma zvednout ID
	 * @throws java.lang.Exception if any.
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		dbViewer.increaseAssociationsCost(associationsId);
	}

	/**
	 * Zvysi o jednicku COST asociaci v DB.
	 *
	 * @param obIdArray IDecka objektu cell_id v associations
	 * @throws java.sql.SQLException if any.
	 */
	public void increaseAssociationsCostToCellsId(Long[] obIdArray) throws SQLException {
		dbViewer.increaseAssociationsCostToCellsId(obIdArray);
		
	}

	/**
	 * Dohleda v DB associations pro pary objektu. Nevytvari nove.
	 *
	 * @param thoughtsPairToUnion - pary Thought pro spojeni
	 * @param notFoundPositions - pozice prvniho objektu kazdeho paru pro dohledani
	 * @return Vector<Associations> dohledanych associaci. Chybejici associations obsahuji null.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getAssociations(
			Vector<Thought> thoughtsPairToUnion,
			Vector<Integer> notFoundPositions) throws Exception {
		Vector<Associations> result = new Vector<Associations>();
		result.setSize(thoughtsPairToUnion.size() / 2);
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < notFoundPositions.size(); i++) {
			int pos = notFoundPositions.get(i);
			Thought th1 = thoughtsPairToUnion.get(pos);
			Thought th2 = thoughtsPairToUnion.get(pos+1);
			buff.append("(src_id = " + th1.getActiveCell().getId() + " AND tgt_id = "
					+ th2.getActiveCell().getId() + ") OR ");
		}
		Vector<Associations> tempAssociations = new Vector<Associations>();
		if (buff.length() > 0) {
			buff.delete(buff.length() - 4, buff.length());
			String sql = "SELECT * FROM associations WHERE " + buff.toString();
			ResultSet rs = dbViewer.executeQuery(sql);
			while (rs.next()) {
				Long id = rs.getLong("id");
				Long objId = rs.getLong("cell_id");
				Long srcId = rs.getLong("src_id");
				Long srcTable = rs.getLong("src_tbl");
				Long tgtId = rs.getLong("tgt_id");
				Long tgtTable = rs.getLong("tgt_tbl");
				Long cost = rs.getLong("cost");
				Associations nextAssoc = new Associations(id, objId, srcId, srcTable, tgtId, tgtTable, cost);
				tempAssociations.add(nextAssoc);
			}
		}
		// nastrkame nalezene associations do resultu
		for (int i = 0; i < thoughtsPairToUnion.size()-1; i = i+2) {
			Thought th1 = thoughtsPairToUnion.get(i);
			Thought th2 = thoughtsPairToUnion.get(i+1);
			for (int k = 0; k < tempAssociations.size(); k++) {
				Associations nextAssoc = tempAssociations.get(k);
				if (nextAssoc.getSrcId() == th1.getActiveCell().getId() 
						&& nextAssoc.getTgtId() == th2.getActiveCell().getId()) {
					result.set(((i + 1) / 2), nextAssoc);
				}
			}			
		}		
		return result;
	}

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
			throws SQLException {
		/** Vytvori nove associations */
		Vector<Associations> newAssociations = 
			dbViewer.insertAssociations(thoughtPairsToUnion, newCells);
		return newAssociations;
	}

	/**
	 * <p>getNewCells.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Cell> getNewCells(Vector<Thought> thoughtPairsToUnion)
			throws Exception {
		/** Vytvori nove cells */
		Vector<Cell> newCells = dbViewer.getNewCells(thoughtPairsToUnion);
		return newCells;
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru.
	 *
	 * @param cellsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> cellsId) throws Exception {
		Vector<Associations> result = dbViewer.getAllAssociations(cellsId);		
		return result;
	}

	/**
	 * Dohleda mozne Objekty, ktere jsou vytvorene na zaklade objektu z parametru.
	 *
	 * @param layer - id objektu, pro ktere hledame nadrazene objekty.
	 * @param constant - hledame spojeni jen pro pary objektu, ktere jsou definovany
	 * v konstante (prvni-treti..., prvni-sedmy..., prvni-...). Mohou obsahovat null.
	 *
	 * Vzhledem k tomu, ze se jedna o rychle dohledani i s moznosti neuplne
	 * informace, nebudeme pouzivat Memory pro ulozeni objektu a associaci v cache.
	 * @return Vector id nadrazenych objektu nebo null-objektu.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Long> getSuperiorCellsId(Vector<Long> layer, int constant) throws SQLException {
		Vector<Long> pairsToFind = new Vector<Long>();
		Vector<Integer> positionsOfNull = new Vector<Integer>();
		for (int i = 0; i < layer.size()-constant; i++) {
			Long src = layer.get(i);
			Long tgt = layer.get(i+constant);
			if (src != null && tgt != null) {
				pairsToFind.add(src);
				pairsToFind.add(tgt);
			} else {
				positionsOfNull.add(i);
			}
		}
		// get Superior cells from DB
		Vector<Long> superiorCellsId = dbViewer.getSuperiorCellsId(pairsToFind);
		// doplnit null hodnoty pro zachovani struktury vysledku
		for (int i = 0; i < positionsOfNull.size(); i++) {
			superiorCellsId.add(positionsOfNull.get(i), null);
			// TODO zkontrolovat zda spravne vyplnuje
		}
		return superiorCellsId;
	}
}
