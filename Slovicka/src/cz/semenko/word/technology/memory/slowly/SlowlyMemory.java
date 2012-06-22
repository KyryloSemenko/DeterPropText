package cz.semenko.word.technology.memory.slowly;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.database.AbstractDBViewer;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;
import cz.semenko.word.persistent.Tables;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Třida reprezentuje pomalou paměť, která se nachází v DB.
 * 
 * @author k
 *
 */
public class SlowlyMemory {
	private static SlowlyMemory instance;
	/*private Set<Tables> t;
	private Set<Objects> o;
	private Set<Associations> a;*/
	
	public static SlowlyMemory getInstance() {
		if (instance == null) {
			synchronized(SlowlyMemory.class) {
				SlowlyMemory inst = instance;
				if (inst == null) {
					instance = new SlowlyMemory();
				}
			}
		}
		return instance;
	}
	
	private SlowlyMemory() {
		/*t = new Tables();
		o = new Objects();
		a = new Associations();*/
	}
	
	/**
	 * get Associations tables
	 * @return
	 * @throws SQLException
	 */
	public Collection<Associations> getAssociations() throws SQLException {
		//int tablesAssociationsSize = Config.getConfig().getInt("fastMemory.tablesObjectsAndAssociationsSize");
		Collection<Associations> result = new Vector<Associations>();
		//Collection<Associations> result = DBViewer.getInstance().getAssociations(tablesAssociationsSize);
		return result;
	}

	public Collection<Objects> getObjects() throws SQLException {
		//int tablesObjectsSize = Config.getConfig().getInt("fastMemory.tablesObjectsAndAssociationsSize");
		Collection<Objects> result = new Vector<Objects>();
		//Collection<Objects> result = DBViewer.getInstance().getObjects(tablesObjectsSize);
		return result;
	}
	

	public Collection<Tables> getTables() throws SQLException {
		//int tablesTablesSize = Config.getConfig().getInt("fastMemory.tablesTablesSize");
		//Collection<Tables> result = DBViewer.getInstance().getTables(tablesTablesSize);
		Collection<Tables> result = new Vector<Tables>();
		return result;
	}
	/**
	 * Nalezne v DB idecka masivu znaku. Jestli znak neexistuje - vytvori ho. Zdvojene a opakujici se znaky nevytvari nove,
	 * ale vrati jejich id.
	 * @param missingChars
	 * @return Pole Idecek znaku
	 * @throws SQLException 
	 */
	public Long[] getCharsId(Vector<Character> missingChars) throws Exception {
		Long[] result = new Long[missingChars.size()];
		AbstractDBViewer db = AbstractDBViewer.getInstance();
		List<Objects> primiteveObjects = db.getPrimitiveObjects((List<Character>)missingChars);
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
			Vector<Objects> newObjects = AbstractDBViewer.getInstance().getNewPrimitiveObjects(nonExistent);
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
	 * @param missingObjectsId
	 * @return
	 * @throws Exception
	 */
	public Vector<Objects> getObjects(Vector<Long> missingObjectsId) throws Exception {
		Vector<Objects> result = new Vector<Objects>();
		result.setSize(missingObjectsId.size());
		AbstractDBViewer dbViewer = AbstractDBViewer.getInstance();
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
	 * @param srcThought
	 * @param tgtThought
	 * @return Objects
	 * @throws Exception
	 */
	public Objects getNewObject(Thought srcThought, Thought tgtThought) throws Exception {
		Objects result = null;
		Vector<Thought> thoughtPairsToUnion = new Vector<Thought>();
		thoughtPairsToUnion.add(srcThought);
		thoughtPairsToUnion.add(tgtThought);
		Vector<Objects> objects = AbstractDBViewer.getInstance().getNewObjects(thoughtPairsToUnion);
		result = objects.firstElement();
		return result;
	}

	/**
	 * Vytvori nove Associations.
	 * @param sql
	 * @param numItems
	 * @param tableName
	 * @param idColumnName
	 * @return
	 * @throws Exception
	 
	public Vector<Associations> getNewAssociations(String sql, int numItems,
			String tableName, String idColumnName) throws Exception {
		DBViewer dbViewer = DBViewer.getInstance();
		Vector<Long> idVector = dbViewer.executeUpdate(sql, numItems, tableName, idColumnName);
		Vector<Associations> result = new Vector<Associations>();
		StringBuffer select = new StringBuffer("SELECT * FROM associations WHERE id IN (");
		for (int i = 0; i < idVector.size(); i++) {
			select.append(idVector.get(i) + ", ");
		}
		select.delete(select.length()-2, select.length());
		select.append(")");
		ResultSet rs = dbViewer.executeQuery(select.toString());
		while(rs.next()) {
			long id = rs.getLong("id");
			long srcId = rs.getLong("src_id");
			long srcTable = rs.getLong("src_tbl");
			long tgtId = rs.getLong("tgt_id");
			long tgtTable = rs.getLong("tgt_tbl");
			long cost = rs.getLong("cost");
			Long objId = rs.getLong("obj_id");
			result.add(new Associations(id, objId, srcId, srcTable, tgtId, tgtTable, cost));
		}
		
		return result;
	}*/

	/**
	 * Dohleda v DB Associations. Jestli nenajde, vrati null
	 * @param srcThought
	 * @param tgtThought
	 * @return
	 * @throws Exception
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		AbstractDBViewer dbViewer = AbstractDBViewer.getInstance();
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
	 * @param associationsId - IDecka associaci v kterych se ma zvednout ID
	 * @throws Exception 
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		AbstractDBViewer.getInstance().increaseAssociationsCost(associationsId);
	}

	/**
	 * Zvysi o jednicku COST asociaci v DB.
	 * @param obIdArray IDecka objektu obj_id v associations
	 * @throws SQLException 
	 */
	public void increaseAssociationsCostToObjectsId(Long[] obIdArray) throws SQLException {
		AbstractDBViewer.getInstance().increaseAssociationsCostToObjectsId(obIdArray);
		
	}

	/**
	 * Dohleda v DB associations pro pary objektu. Nevytvari nove.
	 * @param thoughtsPairToUnion - pary Thought pro spojeni
	 * @param notFoundPositions - pozice prvniho objektu kazdeho paru pro dohledani
	 * @return Vector<Associations> dohledanych associaci. Chybejici associations obsahuji null.
	 * @throws Exception 
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
			ResultSet rs = AbstractDBViewer.getInstance().executeQuery(sql);
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
	 * Vytvori nove Objects, nove Associations a z Objects - nove Thoughts.
	 * Objects a associations prida do cashe FastMemory.
	 * @param thoughtPairsToUnion - pary Thought, 
	 * ktere zarucene nemaji Associations ani spolecny Objects.
	 * @return
	 * @throws Exception 
	 */
	public Vector<Thought> createNewThoughtsFromPairs(
			Vector<Thought> thoughtPairsToUnion) throws Exception {
		Vector<Thought> result = new Vector<Thought>();
		/** Vytvori nove objects */
		Vector<Objects> newObjects = AbstractDBViewer.getInstance().getNewObjects(thoughtPairsToUnion);
		/** Prida nove objects do FastMemory */
		FastMemory.getInstance().addObjects(newObjects);
		/** Vytvori Thoughts z objektu */
		for (int i = 0; i < newObjects.size(); i++) {
			Objects nextOb = newObjects.get(i);
			Thought th = new Thought(nextOb, new Vector<Associations>());
			result.add(th);
		}
		/** Vytvori nove associations */
		Vector<Associations> newAssociations = 
			AbstractDBViewer.getInstance().insertAssociations(thoughtPairsToUnion, newObjects);
		/** Prida nove associations do FastMemory */
		FastMemory.getInstance().addAssociations(newAssociations);

		return result;
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru.
	 * @param objectsId
	 * @return
	 * @throws Exception 
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception {
		Vector<Associations> result = AbstractDBViewer.getInstance().getAllAssociations(objectsId);		
		return result;
	}

	/**
	 * Dohleda mozne Objekty, ktere jsou vytvorene na zaklade objektu z parametru.
	 * @param layer - id objektu, pro ktere hledame nadrazene objekty.
	 * @param constant - hledame spojeni jen pro pary objektu, ktere jsou definovany
	 * v konstante (prvni-treti..., prvni-sedmy..., prvni-...). Mohou obsahovat null.
	 * 
	 * Vzhledem k tomu, ze se jedna o rychle dohledani i s moznosti neuplne
	 * informace, nebudeme pouzivat Memory pro ulozeni objektu a associaci v cashe.
	 * 
	 * @return Vector id nadrazenych objektu nebo null-objektu.
	 * @throws SQLException
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
		Vector<Long> superiorObjectsId = AbstractDBViewer.getInstance().getSuperiorObjectsId(pairsToFind);
		// doplnit null hodnoty pro zachovani struktury vysledku
		for (int i = 0; i < positionsOfNull.size(); i++) {
			superiorObjectsId.add(positionsOfNull.get(i), null);
			// TODO zkontrolovat zda spravne vyplnuje
		}
		return superiorObjectsId;
	}
}
