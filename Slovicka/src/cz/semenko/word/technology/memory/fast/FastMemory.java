package cz.semenko.word.technology.memory.fast;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.persistent.Tables;
import cz.semenko.word.technology.memory.slow.SlowMemory;

/**
 * Singleton. FastMemory is cached on. SlowMemory is saved in DB.
 *
 * The size of fast memory is set in first configuration parameter;
 * and later management system configuration parameters.
 * It's a DB cache and serves to reduce the number of requests to the database.
 * So that should contain the same data and have the same structure as the DB.
 * <p>Class diagram</p>
 * <img src="doc-files\class_diagram_FastMemory.png" />
 *
 * @author Kyrylo Semenko
 */
public class FastMemory {
	private Collection<Tables> tablesCollection;
	/** Constant <code>logger</code> */
	public static Logger logger = Logger.getLogger(FastMemory.class);
	/**
	 * Bude naplnovana a doplnovana dle nasledujicich pravidel:
	 * 	vector ma zacatek nahore - vector.get(0) je nahore;
	 * 	nove prvky se pridavaji na zacatek kolekce.
	 * 
	 * It will be implemented and supplemented by the following rules:
	 * Vector has a start up - vector.get (0) is up;
	 * new elements are added to the beginning of the collection.
	 */
	private Collection<Cell> cellsCollection;
	/**
	 * <ul>Bude naplnovana a doplnovana dle nasledujicich pravidel:
	 * 	<li>vector ma zacatek nahore - vector.get(0) je nahore;
	 * 	<li>nove prvky se pridavaji na zacatek kolekce.
	 * 
	 * <ul>To be implemented and supplemented by the following rules:
	 * 	<li>Vector has a start up - vector.get (0) is up;
	 * 	<li>new elements are added to the beginning of the collection.
	 */
	private Collection<Associations> associationsCollection;
	private SlowMemory slowMemory;
	private Config config;
	
	/**
	 * Constructor
	 *
	 * @throws java.sql.SQLException if any.
	 * @param slowMemory a {@link cz.semenko.word.technology.memory.slow.SlowMemory} object.
	 */
	public FastMemory(SlowMemory slowMemory) throws SQLException {
		this.slowMemory = slowMemory;
		associationsCollection = slowMemory.getAssociations();
		cellsCollection = slowMemory.getCells();
		tablesCollection = slowMemory.getTables(); // TODO vyhodit nebo spravovat Springem
	}

	/**
	 * <p>Setter for the field <code>config</code>.</p>
	 *
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * Najde idecka masivu znaku. Jestli ne, tak sahne na SlowMemory.
	 * Seradi elementy v cellsTableCollection dle vyuzivanosti.
	 *
	 * Finds ID of solid characters. If not, feel the SlowMemory.
	 * Sorts the elements in cellsTableCollection by frequency utilized.
	 *
	 * @return array of Id
	 * @throws SQLException if any.
	 * @param chars an array of char.
	 * @throws java.lang.Exception if any.
	 */
	public Long[] getCharsId(char[] chars) throws Exception {
		Vector<Cell> localCellsTable = (Vector<Cell>)cellsCollection;
		Long[] result = new Long[chars.length];
		char ch;
		Cell object;
		Cell tempCell;
		// dohleda idecka znaku v cellsTableCollection
		lab:
		for (int i = 0; i < chars.length; i++) {
			ch = chars[i];
			for (int k = 0; k < localCellsTable.size(); k++) {
				object = localCellsTable.get(k);
				if (object.getSrc().compareTo(Character.toString(ch)) == 0) {
					result[i] = object.getId();
					// Posunout object o jednu pozici nahoru jestli neni jiz nahore
					if (k > 0) {
						tempCell = localCellsTable.get(k - 1);
						localCellsTable.set(k - 1, object);
						localCellsTable.set(k, tempCell);
					}
					continue lab;
				}
			}
		}
		// jestli nejake ze znaku nebyly nalezeny, dohleda je v databazi.
		Vector<Character> missingChars = new Vector<Character>();
		for (int i = 0; i < result.length; i++) {
			if (result[i] == null) {
				missingChars.add(chars[i]);
			}
		}
		if (missingChars.size() > 0) {
			Long[] findingChars = slowMemory.getCharsId(missingChars);			
			// doplni chybejici znaky v result z findingChars
			int pos = 0;
			int tableCellsSize = config.getFastMemory_tablesCellSize();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null) {
					result[i] = findingChars[pos];
					Cell newCell = new Cell(findingChars[pos], missingChars.get(pos).toString(), 1L);
					// Jestli objekt jiz existuje v localCellsTable, znamena to ze jde o zdvojena nebo opakujici se pismena. Nezvedame je nahoru.
					if (localCellsTable.contains(newCell)) {
						pos++;
						continue;
					}
					if (localCellsTable.size() < tableCellsSize) { // doplni na zacatek localO nalezeny Cell.
						localCellsTable.add(0, newCell);
					} else {
						// vlozi na zacatek vektora sadu z posledniho cteni, napriklad osum poslednich misto starych zaznamu
						localCellsTable.add(0, newCell);
						localCellsTable.setSize(config.getFastMemory_tablesCellSize());
					}
					pos++;
				}
			}
		}
		for (int i = 0; i < result.length; i++) {
			if (result[i] == null) {
				String error = "Nedovoleny objekt null. Result: " + result;
				logger.error(error);
				System.exit(1);
			}
		}
		return result;
	}

	/**
	 * Najde Objekts pro Vektor IDecek. Jestli ne, tak sahne na SlowMemory.
	 * Pri hledani rozbali objekty do prvniho Levelu a zkusi najit podobnost.
	 * Seradi elementy v cellsTableCollection dle vyuzivanosti.
	 *
	 * Find the Objekts to ID vector. If not, feel the SlowMemory.
	 * When searching, expand the first level and try to find similarities.
	 * Sorts the elements in cellsTableCollection by used frequency.
	 *
	 * @param cellsId a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Cell> getCells(Vector<Long> cellsId) throws Exception {
		Vector<Cell> result = new Vector<Cell>();
		Vector<Cell> localCellsTable = (Vector<Cell>)cellsCollection;
		Vector<Long> missingCellsId = new Vector<Long>();
		// zkusime dohledat objekty v rychle pameti
		label:
		for (int i = 0; i < cellsId.size(); i++) {
			for (int k = 0; k < localCellsTable.size(); k++) {
				Cell ob = localCellsTable.get(k);
				if (ob.getId().compareTo(cellsId.get(i)) == 0) {
					result.add(i, ob);
					// zvednout o jednu pozici nalezeny objekt
					if (k > 0) {
						Cell tempObj = localCellsTable.get(k-1);
						localCellsTable.set(k-1, ob);
						localCellsTable.set(k, tempObj);
					}
					continue label;
				}
			}
			result.add(i, null);
			missingCellsId.add(cellsId.get(i));
		}
		if (missingCellsId.size() > 0) { // dohlesat objekty v DB
			Vector<Cell> cellsFromSlowMemory = slowMemory.getCells(missingCellsId);
			// Vyplnit chybejici objekty v result, doplnit cellsTableCollection
			int pos = 0;
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i) == null) {
					result.set(i, cellsFromSlowMemory.get(pos));
					pos++;
				}
			}
			if (result.contains(null)) {
				throw new Exception("result contains null element. " + result.toString());
			}
			// doplnit FastMemory cerstvymi Objekty
			addCells(result);
		}
		return result;
	}

	/* Viz. Memory getNewCell(...) */
	/**
	 * <p>getNewCell.</p>
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @return a {@link cz.semenko.word.persistent.Cell} object.
	 * @throws java.lang.Exception if any.
	 */
	public Cell getNewCell(Thought srcThought, Thought tgtThought) throws Exception {
		Cell result = slowMemory.getNewCell(srcThought, tgtThought);
		// pridat object na konec kolekce
		Vector<Cell> vector = new Vector<Cell>();
		vector.add(result);
		addCells(vector);
		return result;
	}

	/**
	 * Dohleda Association ve FastMemory. Jestli nenajde, zkusi najit ve SlowMemory.
	 *
	 * Supervision Association in FastMemory. If not found, try to find the SlowMemory.
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link cz.semenko.word.persistent.Associations} object.
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		// Jestli srcThought je spojena associaci s tgtThought, nalezne a vrati tuto Association
		Vector<Associations> assocVector = srcThought.getConsequenceAssociations();
		Associations result = null;
		for (int i = 0; i < assocVector.size(); i++) {
			result = assocVector.get(i);
			if (result.getTgtId().compareTo(tgtThought.getActiveCell().getId()) == 0) {
				return result;
			}
		}
		// Kdyz asociace nenalezena, dohledat Association v Memory
		assocVector = (Vector<Associations>)associationsCollection;
		long srcCellId = srcThought.getActiveCell().getId();
		long tgtCellId = tgtThought.getActiveCell().getId();
		for (int i = 0; i < assocVector.size(); i++) {
			Associations nextAssoc = assocVector.get(i);
			if (nextAssoc.getSrcId() == srcCellId && nextAssoc.getTgtId() == tgtCellId) {
				if (i > 0) { // Zvednout o jednu pozici pouzitou Associations
					Associations tempAssoc = assocVector.get(i-1);
					assocVector.set(i-1, nextAssoc);
					assocVector.set(i, tempAssoc);
				}
				return nextAssoc;
			}
		}
		// Nenalezeno. Zkusit najit v SlowMemory
		result = slowMemory.getAssociation(srcThought, tgtThought);
		if (result == null) {
			return null;
		}
		/** Pridat novou Asociaci k associationsCollection */
		Vector<Associations> vector = new Vector<Associations>();
		vector.add(result);
		addAssociations(vector);
		return result;
	}

	/**
	 * Zvysi COST u asociaci, ktere maji cell_id z parametru jak v DB,
	 * tak i v cache
	 *
	 * increase the associations COST, which are cell_id parameter of both DB
	 * as well as in cash
	 *
	 * @param arrayOfCellsId - array of Cell ID
	 * @throws java.sql.SQLException if any.
	 */
	public void increaseAssociationsCostToCellsId(Long[] arrayOfCellsId) throws SQLException {
		Vector<Associations> localAssocTable = (Vector<Associations>)associationsCollection;
		for (int i = 0; i < localAssocTable.size(); i++) {
			Associations nextAssoc = localAssocTable.get(i);
			for (int k = 0; k < arrayOfCellsId.length; k++) {
				if (nextAssoc.getObjId() == arrayOfCellsId[k]) {
					nextAssoc.setCost(nextAssoc.getCost() + 1);
				}
			}
		}
		slowMemory.increaseAssociationsCostToCellsId(arrayOfCellsId);
	}

	/**
	 * Zvysi COST associaci o jednicku jak v DB, tak i v cache
	 *
	 * COST Associations will increase by one in both the DB and in Cash
	 *
	 * @param associationsId - Associations \id, in which the COST is to lift
	 * @throws java.lang.Exception if any.
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		slowMemory.increaseAssociationsCost(associationsId);
		Vector<Associations> localAssocTable = (Vector<Associations>)associationsCollection;
		for (int i = 0; i < localAssocTable.size(); i++) {
			Associations nextAssoc = localAssocTable.get(i);
			if (associationsId.contains(nextAssoc.getId())) {
				nextAssoc.setCost(nextAssoc.getCost() + 1);
			}
		}
	}

	/**
	 * Viz. increaseAssociationsCost(Vector<Long> associationsId)
	 *
	 * @param result an array of {@link java.lang.Long} cells.
	 * @throws java.lang.Exception if any.
	 */
	public void increaseAssociationsCost(Long[] result) throws Exception {
		Vector<Long> param = new Vector<Long>();
		for (int i = 0; i < result.length; i++) {
			param.add(result[i]);
		}
		increaseAssociationsCost(param);
	}

	/**
	 * Dohleda associations. Nevytvari nove.
	 * Zvedne nahoru nalezene.
	 * Nenalezene dohleda v SlowMemory a prida k FastMemory cache.
	 *
	 * Find associations. Does not create new.
	 * He picks up finding.
	 * Found in the supervisors and adding SlowMemory FastMemory Cash.
	 *
	 * @param thoughtsPairToUnion a {@link java.util.Vector} object.
	 * @return Vector<Associations> Nenalezene pozice obsahuji null
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getAssociations(
			Vector<Thought> thoughtsPairToUnion) throws Exception {
		Vector<Associations> result = new Vector<Associations>();
		result.setSize(thoughtsPairToUnion.size() / 2);
		Vector<Associations> tempAssocTable = (Vector<Associations>)associationsCollection;
		Vector<Integer> notFoundPositions = new Vector<Integer>();
		int elevate = config.getDataProvider_numCharsReadsFromInput();
		lab:
		// Dohledat associations zde
		for (int i = 0; i < thoughtsPairToUnion.size()-1; i = i+2) {
			Thought th1 = thoughtsPairToUnion.get(i);
			Thought th2 = thoughtsPairToUnion.get(i+1);
			for (int k = 0; k < tempAssocTable.size(); k++) {
				Associations nextAssoc = tempAssocTable.get(k);
				if (nextAssoc.getSrcId() == th1.getActiveCell().getId() 
						&& nextAssoc.getTgtId() == th2.getActiveCell().getId()) {
					result.set((i+1) / 2, nextAssoc);
					// Zvednout pozici nalezene Associations o elevate
					if (k - elevate > 0) {
						nextAssoc = tempAssocTable.remove(k);
						tempAssocTable.add(k - elevate, nextAssoc);
					}
					continue lab;
				}
			}
			notFoundPositions.add(i);
		}
		if (notFoundPositions.size() == 0) {
			return result;
		}
		// dohledat associations v SlowMemory
		Vector<Associations> assocFromSlowMemory = 
			slowMemory.getAssociations(thoughtsPairToUnion, notFoundPositions);
		// Pripojit doledane assocFromSlowMemory k resultu
		for (int i = 0; i < assocFromSlowMemory.size(); i++) {
			Associations nextAssoc = assocFromSlowMemory.get(i);
			if (nextAssoc != null) {
				result.set(i, nextAssoc);
			}
			
		}
		// Odstranit prazdne polozky z assocFromSlowMemory
		while (assocFromSlowMemory.contains(null)) {
			assocFromSlowMemory.remove(null);
		}		
		// pridat associace na konec associationsTableCollections
		addAssociations(result);
		return result;
	}

	/**
	 * Vytvori nove objekty a asociace. Prida je do cellsTableCollection a associationsTableCollection
	 *
	 * @param thoughtPairsToUnion - pary Thought, ktere zarucene nemaji Associations ani spolecny Cell
	 * @throws java.lang.Exception if any.
	 */
	public void createNewAssociationsAndCells(
			Vector<Thought> thoughtPairsToUnion) throws Exception {

		Vector<Cell> newCells = slowMemory.getNewCells(thoughtPairsToUnion);
		Vector<Thought> newThoughts = new Vector<Thought>();
		
		/** Prida nove cells do FastMemory */
		addCells(newCells);
		
		/** Vytvori Thoughts z objektu */
		for (int i = 0; i < newCells.size(); i++) {
			Cell nextOb = newCells.get(i);
			Thought th = new Thought(nextOb, new Vector<Associations>());
			newThoughts.add(th);
		}
		
		Vector<Associations> newAssociations = slowMemory.insertAssociations(
				thoughtPairsToUnion, newCells);
		// Prida nove associations k FastMemory cache
		getAssociations(thoughtPairsToUnion);
		
		// Prida nove associations do FastMemory
		addAssociations(newAssociations);
		
		// Prida aktivni objekty do FastMemory cache
		addCells(newThoughts);
		
		// TODO vymyslit test
	}

	/**
	 * <p>addCells.</p>
	 *
	 * @param newThoughts a {@link java.util.Vector} object.
	 */
	public void addCells(Vector<Thought> newThoughts) {
		Vector<Cell> activeCells = new Vector<Cell>();
		for (int i = 0; i < newThoughts.size(); i++) {
			Thought nextThought = newThoughts.get(i);
			activeCells.add(nextThought.getActiveCell());
		}
		// Prida nove Cell do FastMemory
		addCells(activeCells);
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru.
	 * Jestli pro aspon jeden z objektu nic nenajde, sahne na SlowMemory.
	 * Zde zavadim neco jako Hluboke vyhledavani a Melke vyhledavani,
	 * Melke - hledat jen ve FastMemory.
	 * 	uspokojit se, kdyz ve FastMemory nalezena aspon jedna Association,
	 * 	kdyz aspon jedna chybi -
	 * 		dohledat associations u vsech prvku,
	 * 		nebo
	 * 		dohledat associations jen u chybejicich prvku.
	 * Hluboke - dohledat vzdy v SlowMemory.
	 *
	 * @param cellsId - Vector idecek Objektu.
	 * @return - Vector<Associations> bud melke, stredni nebo hluboke dle nastavenych parametru
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> cellsId) throws Exception {
		boolean deepSearch = config.isFastMemory_alwaysSearchToAssociationsDeepInTheMemory();
		Vector<Associations> tempAssociations = (Vector<Associations>)associationsCollection;
		Vector<Associations> result = new Vector<Associations>();
		if (deepSearch) { // TODO porovnat s Melkym vyhledavanim v ruznych rezimech.
			result = slowMemory.getAllAssociations(cellsId);
		} else {
			boolean searchAtAllElements = config.isFastMemory_searchToAssociationsAtAllElements();
			Vector<Long> notFoundCellsId = (Vector<Long>)cellsId.clone();
			for(int i = 0; i < cellsId.size(); i++) {
				Long nextId = cellsId.get(i);
				boolean found = false;
				for (int k = 0; k < tempAssociations.size(); k++) {
					Associations ass = tempAssociations.get(k);
					if (nextId == ass.getSrcId()) {
						while (notFoundCellsId.contains(nextId)) {
							notFoundCellsId.removeElement(nextId);
						}
						result.add(ass);
						found = true;
					}
				}
				if (found == false) {
					/*kdyz aspon jedna chybi - 
					 * 		dohledat associations u vsech prvku,
					 * 		nebo
					 * 		dohledat associations jen u chybejicich prvku.*/
					if (searchAtAllElements) {
						// Zahodit nalezene a vyhledat vse v SlowMemory
						result = slowMemory.getAllAssociations(cellsId);
					} else {
						// Pokracovat ve filtrovani notFoundCellsId, dohledat jen chybejici a pridat k resultu
						continue;
					}
				}
				Vector<Associations> missingAssociations = slowMemory.getAllAssociations(notFoundCellsId);
				result.addAll(missingAssociations);
			}
		}
		/** Pridat associations do associationsCollection */
		addAssociations(result);
		return result;
	}

	/**
	 * Prida associations na zacatek associationsCollection.
	 * Jestli assocoation existuje - zvedne ji o jednu pozici nahoru.
	 *
	 * @param newAssociations a {@link java.util.Collection} object.
	 */
	public void addAssociations(Collection<Associations> newAssociations) {
		/** Odstrani null hodnoty z parametru */
		while (newAssociations.contains(null)) {
			newAssociations.remove(null);
		}
		if (newAssociations.size() == 0) {
			return;
		}
		Vector<Associations> tempAssociations = (Vector<Associations>)associationsCollection;
		for (Associations ass : newAssociations) {
			int pos = tempAssociations.indexOf(ass);
			if (pos == -1) { // associace nenalezena. Pridat na zacatek.
				tempAssociations.add(0, ass);
				continue;
			}
			if (pos > 0) { // associace jiz existuje v tempAssociations. Zvednout nahoru.
				Associations tempAss = tempAssociations.get(pos-1);
				tempAssociations.set(pos-1, ass);
				tempAssociations.set(pos, tempAss);
			} else { // associace jiz ma prvni pozici - nic neprovadet.
				;
			}
		}
		int maxAssociationsSize = config.getFastMemory_tablesAssociationsSize();
		if (tempAssociations.size() > maxAssociationsSize) {
			tempAssociations.setSize(maxAssociationsSize);
		}
	}

	/**
	 * Prida cells na zacatek cellsCollection.
	 * Jestli cells existuje - zvedne ho o jednu pozici nahoru.
	 *
	 * @param newCells a {@link java.util.Collection} object.
	 */
	public void addCells(Collection<Cell> newCells) {
		Vector<Cell> tempCells = (Vector<Cell>)cellsCollection;
		for (Cell ob : newCells) {
			int pos = tempCells.indexOf(ob);
			if (pos == -1) { // associace nenalezena. Pridat na zacatek.
				tempCells.add(0, ob);
			}
			if (pos > 0) { // associace jiz existuje v tempAssociations. Zvednout nahoru.
				Cell tempOb = tempCells.get(pos-1);
				tempCells.set(pos-1, ob);
				tempCells.set(pos, tempOb);
			} else { // associace jiz je na zacatku. Nic neprovadet.
				;
			}
		}
		int maxCellsSize = config.getFastMemory_tablesCellSize();
		if (tempCells.size() > maxCellsSize) {
			tempCells.setSize(maxCellsSize);
		}
	}

	/**
	 * Dohleda spolecny Thought pro par. Nic nevytvari.
	 *
	 * @param th1 a {@link cz.semenko.word.aware.Thought} object.
	 * @param th2 a {@link cz.semenko.word.aware.Thought} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link cz.semenko.word.aware.Thought} object.
	 */
	public Thought getThought(Thought th1, Thought th2) throws Exception {
		Associations ass = th1.getAssociation(th2);
		Long cellId = ass.getObjId();
		Vector<Long> vector = new Vector<Long>();
		vector.add(cellId);
		Vector<Cell> cellsVector = getCells(vector);
		Cell activeCell = null;
		if (cellsVector.size() > 0) {
			activeCell = cellsVector.firstElement();
		}
		Vector<Associations> consequenceAssociations = getAllAssociations(vector);
		return new Thought(activeCell, consequenceAssociations);
	}
	
	/**
	 * Dohleda vsechny objekty. Nevytvari nove. Zvedne pozici objektu v cache jestli existuje.
	 * Prida object do cache jestli neexistuje.
	 *
	 * @param inputCells an array of {@link java.lang.Long} cells.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Cell> getCells(Long[] inputCells) throws Exception {
		Vector<Long> unitedCellsId = new Vector<Long>();
		for (int i = 0; i < inputCells.length; i++) {
			unitedCellsId.add(inputCells[i]);
		}
		Vector<Cell> result = getCells(unitedCellsId);
		return result;
	}

	/**
	 * Vytvori nove Associations pro pary Thought.
	 *
	 * @param nonExistsPairs - pary Thought pro spojeni, ktere zarucene
	 * nemaji Cell ani Associations.
	 * @return Vector<Associations> serazeny stejne jako vstupni data.
	 * @throws java.sql.SQLException if any.
	 * @throws java.sql.SQLException if any.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Associations> getNewAssociations(
			Vector<Thought> nonExistsPairs) throws SQLException, Exception {
		createNewAssociationsAndCells(nonExistsPairs);
		Vector<Associations> result = getAssociations(nonExistsPairs);
		return result;
	}
	
	/**
	 * Dohleda vsechny asociace. Nevytvari nove. Zvedne pozici asociace v cache jestli existuje.
	 * Prida asociaci do cache jestli neexistuje.
	 *
	 * @param cells a {@link java.util.Vector} object.
	 * @throws java.lang.Exception if any.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Associations> getAssociationsToCells(Vector<Cell> cells) throws Exception {
		Vector<Long> cellsId = new Vector<Long>();
		for (int i = 0; i < cells.size(); i++) {
			cellsId.add(cells.get(i).getId());
		}
		Vector<Associations> result = getAllAssociations(cellsId);		
		return result;
	}
	
	/**
	 * Vyhleda id znaku ve FastMemory, kdyz nenajde tak v SlowMemory,
	 * kdyz nenajde tak zalozi novy znak ve SlowMemory.
	 *
	 * @param inputChars an array of char.
	 * @return an array of {@link java.lang.Long} cells.
	 * @throws java.lang.Exception if any.
	 */
	public Long[] getCells(char[] inputChars) throws Exception {
		Long[] result = getCharsId(inputChars);
		return result;
	}
	
	/**
	 * <p>getSuperiorCellsId.</p>
	 *
	 * @param layer a {@link java.util.Vector} object.
	 * @param constant a int.
	 * @return a {@link java.util.Vector} object.
	 * @throws java.sql.SQLException if any.
	 */
	public Vector<Long> getSuperiorCellsId(Vector<Long> layer, int constant) throws SQLException {
		return slowMemory.getSuperiorCellsId(layer, constant);
	}
}
