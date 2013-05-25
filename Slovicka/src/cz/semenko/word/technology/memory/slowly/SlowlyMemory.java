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
import cz.semenko.word.persistent.Objects;
import cz.semenko.word.persistent.Tables;

/**
 * Třida reprezentuje pomalou paměť, která se nachází v DB.
 *
 * @author k
 * @version $Id: $Id
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
		//int tablesAssociationsSize = Config.getConfig().getInt("fastMemory.tablesObjectsAndAssociationsSize");
		Collection<Associations> result = new Vector<Associations>();
		//Collection<Associations> result = DBViewer.getInstance().getAssociations(tablesAssociationsSize);
		return result;
	}

	/**
	 * <p>getObjects.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Collection<Objects> getObjects() throws SQLException {
		//int tablesObjectsSize = Config.getConfig().getInt("fastMemory.tablesObjectsAndAssociationsSize");
		Collection<Objects> result = new Vector<Objects>();
		//Collection<Objects> result = DBViewer.getInstance().getObjects(tablesObjectsSize);
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
		List<Objects> primiteveObjects = dbViewer.getPrimitiveObjects((List<Character>)missingChars);
		Map<String, Objects> primObjectsMap = new TreeMap<String, Objects>();
		for (Objects ob : primiteveObjects) {
			primObjectsMap.put(ob.getSrc(), ob);
		}
		// Get nonexists caracters
		Vector<Character> nonExistent = new Vector<Character>();// vector nenalezenych znaku
		for (Character ch : missingChars) {
			if (primObjectsMap.containsKey(Character.toString(ch)) == false) {
				nonExistent.add(ch);
			}
		}
		if (nonExistent.size() > 0) {
			// Doplni objekty ktere jeste nejsou v DB
			// Ziskame klice vlozenych znaku
			Vector<Objects> newObjects = dbViewer.getNewPrimitiveObjects(nonExistent);
			for (Objects ob : newObjects) {
				primObjectsMap.put(ob.getSrc(), ob);
			}
		}
		for (int i = 0; i < missingChars.size(); i++) {
			String nextChar = Character.toString(missingChars.get(i));
			result[i] = primObjectsMap.get(nextChar).getId();
		}
		return result;
	}

	/**
	 * Dostane z DB objekty dle zadanych ID. Nevytvari nove.
	 *
	 * @param missingObjectsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Objects> getObjects(Vector<Long> missingObjectsId) throws Exception {
		Vector<Objects> result = new Vector<Objects>();
		result.setSize(missingObjectsId.size());
		StringBuffer sql = new StringBuffer("SELECT * FROM objects WHERE id IN (");
		for (int i = 0; i < missingObjectsId.size(); i++) {
			sql.append(missingObjectsId.get(i) + ", ");
		}
		sql.delete(sql.length()-2, sql.length());
		sql.append(")");
		ResultSet rs = dbViewer.executeQuery(sql.toString());
		Map<Long, Objects> objectsMap = new TreeMap<Long, Objects>();
		while (rs.next()) {
			Long id = rs.getLong("id");
			String src = rs.getString("src");
			Long type = rs.getLong("type");
			Objects ob = new Objects(id, src, type);
			objectsMap.put(id, ob);
		}
		// Vyplni result
		for (int i = 0; i < missingObjectsId.size(); i++) {
			Long id = missingObjectsId.get(i);
			result.set(i, objectsMap.get(id));
		}
		return result;
	}

	/**
	 * Vytvori novy Objekts pro par Thought.
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @return Objects
	 * @throws java.lang.Exception if any.
	 */
	public Objects getNewObject(Thought srcThought, Thought tgtThought) throws Exception {
		Objects result = null;
		Vector<Thought> thoughtPairsToUnion = new Vector<Thought>();
		thoughtPairsToUnion.add(srcThought);
		thoughtPairsToUnion.add(tgtThought);
		Vector<Objects> objects = getNewObjects(thoughtPairsToUnion);
		result = objects.firstElement();
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
		Associations result = null;
		String sql = "SELECT * FROM ASSOCIATIONS WHERE src_id = " + srcThought.getActiveObject().getId()
			+ " AND tgt_id = " + tgtThought.getActiveObject().getId();
		ResultSet rs = dbViewer.executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			if (i > 0) {
				throw new Exception("V tabulce ASSOCIATIONS byly nalezeny dva vyskyty Associace se stejmymi src_id a tgt_id: " + result.toString());
			}
			long id = rs.getLong("id");
			long srcId = rs.getLong("src_id");
			long srcTable = rs.getLong("src_tbl");
			long tgtId = rs.getLong("tgt_id");
			long tgtTable = rs.getLong("tgt_tbl");
			long cost = rs.getLong("cost");
			Long objId = rs.getLong("obj_id");
			result = new Associations(id, objId, srcId, srcTable, tgtId, tgtTable, cost);
			i++;
		}
		return result;
	}

	/**
	 * Zvysi COST associaci o jednicku jak v DB, tak i v cashe
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
	 * @param obIdArray IDecka objektu obj_id v associations
	 * @throws java.sql.SQLException if any.
	 */
	public void increaseAssociationsCostToObjectsId(Long[] obIdArray) throws SQLException {
		dbViewer.increaseAssociationsCostToObjectsId(obIdArray);
		
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
			buff.append("(src_id = " + th1.getActiveObject().getId() + " AND tgt_id = "
					+ th2.getActiveObject().getId() + ") OR ");
		}
		Vector<Associations> tempAssociations = new Vector<Associations>();
		if (buff.length() > 0) {
			buff.delete(buff.length() - 4, buff.length());
			String sql = "SELECT * FROM associations WHERE " + buff.toString();
			ResultSet rs = dbViewer.executeQuery(sql);
			while (rs.next()) {
				Long id = rs.getLong("id");
				Long objId = rs.getLong("obj_id");
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
				if (nextAssoc.getSrcId() == th1.getActiveObject().getId() 
						&& nextAssoc.getTgtId() == th2.getActiveObject().getId()) {
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
	 * @param newObjects a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Associations> insertAssociations(
			Vector<Thought> thoughtPairsToUnion, Vector<Objects> newObjects)
			throws SQLException {
		/** Vytvori nove associations */
		Vector<Associations> newAssociations = 
			dbViewer.insertAssociations(thoughtPairsToUnion, newObjects);
		return newAssociations;
	}

	/**
	 * <p>getNewObjects.</p>
	 *
	 * @param thoughtPairsToUnion a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Objects> getNewObjects(Vector<Thought> thoughtPairsToUnion)
			throws Exception {
		/** Vytvori nove objects */
		Vector<Objects> newObjects = dbViewer.getNewObjects(thoughtPairsToUnion);
		return newObjects;
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru.
	 *
	 * @param objectsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception {
		Vector<Associations> result = dbViewer.getAllAssociations(objectsId);		
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
	 * informace, nebudeme pouzivat Memory pro ulozeni objektu a associaci v cashe.
	 * @return Vector id nadrazenych objektu nebo null-objektu.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Long> getSuperiorObjectsId(Vector<Long> layer, int constant) throws SQLException {
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
		// get Superior objects from DB
		Vector<Long> superiorObjectsId = dbViewer.getSuperiorObjectsId(pairsToFind);
		// doplnit null hodnoty pro zachovani struktury vysledku
		for (int i = 0; i < positionsOfNull.size(); i++) {
			superiorObjectsId.add(positionsOfNull.get(i), null);
			// TODO zkontrolovat zda spravne vyplnuje
		}
		return superiorObjectsId;
	}
}
