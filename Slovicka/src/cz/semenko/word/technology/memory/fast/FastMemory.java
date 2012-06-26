package cz.semenko.word.technology.memory.fast;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;
import cz.semenko.word.persistent.Tables;
import cz.semenko.word.technology.memory.slowly.SlowlyMemory;

/**
 * Singleton. FastMemory is cached on. SlowlyMemory is saved in DB.
 * 
 * The size of fast memory is set in first configuration parameter;
  * and later management system configuration parameters.
  * It's a DB cache and serves to reduce the number of requests to the database.
  * So that should contain the same data and have the same structure as the DB.
 * @author k
 *
 */
public class FastMemory {
	private Collection<Tables> tablesCollection;
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
	private Collection<Objects> objectsCollection;
	/**
	 * Bude naplnovana a doplnovana dle nasledujicich pravidel:
	 * 	vector ma zacatek nahore - vector.get(0) je nahore;
	 * 	nove prvky se pridavaji na zacatek kolekce.
	 * 
	 * To be implemented and supplemented by the following rules:
	 * Vector has a start up - vector.get (0) is up;
	 * new elements are added to the beginning of the collection.
	 */
	private Collection<Associations> associationsCollection;
	/** Objekt pod spravou Spring kontejneru */
	private SlowlyMemory slowlyMemory;
	private Config config;
	
	/**
	 * Constucor
	 * @throws SQLException
	 */
	public FastMemory(SlowlyMemory slowlyMemory) throws SQLException {
		this.slowlyMemory = slowlyMemory;
		associationsCollection = slowlyMemory.getAssociations();
		objectsCollection = slowlyMemory.getObjects();
		tablesCollection = slowlyMemory.getTables();
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * Najde idecka masivu znaku. Jestli ne, tak sahne na SlowlyMemory. 
	 * Seradi elementy v objectsTableCollection dle vyuzivanosti.
	 * 
	 * Finds ID of solid characters. If not, feel the SlowlyMemory.
	 * Sorts the elements in objectsTableCollection by frequency utilized.
	 * 
	 * @param array of charcters
	 * @return array of Id
	 * @throws SQLException 
	 */
	public Long[] getCharsId(char[] chars) throws Exception {
		Vector<Objects> localObjectsTable = (Vector<Objects>)objectsCollection;
		Long[] result = new Long[chars.length];
		char ch;
		Objects object;
		Objects tempObject;
		// dohleda idecka znaku v objectsTableCollection
		lab:
		for (int i = 0; i < chars.length; i++) {
			ch = chars[i];
			for (int k = 0; k < localObjectsTable.size(); k++) {
				object = localObjectsTable.get(k);
				if (object.getSrc().compareTo(Character.toString(ch)) == 0) {
					result[i] = object.getId();
					// Posunout object o jednu pozici nahoru jestli neni jiz nahore
					if (k > 0) {
						tempObject = localObjectsTable.get(k - 1);
						localObjectsTable.set(k - 1, object);
						localObjectsTable.set(k, tempObject);
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
			Long[] findingChars = slowlyMemory.getCharsId(missingChars);			
			// doplni chybejici znaky v result z findingChars
			int pos = 0;
			int tableObjectsSize = config.getFastMemory_tablesObjectsSize();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null) {
					result[i] = findingChars[pos];
					Objects newObject = new Objects(findingChars[pos], missingChars.get(pos).toString(), 1L);
					// Jestli objekt jiz existuje v localObjectsTable, znamena to ze jde o zdvojena nebo opakujici se pismena. Nezvedame je nahoru.
					if (localObjectsTable.contains(newObject)) {
						pos++;
						continue;
					}
					if (localObjectsTable.size() < tableObjectsSize) { // doplni na zacatek localO nalezeny Objects.
						localObjectsTable.add(0, newObject);
					} else {
						// vlozi na zacatek vektora sadu z posledniho cteni, napriklad osum poslednich misto starych zaznamu
						localObjectsTable.add(0, newObject);
						localObjectsTable.setSize(config.getFastMemory_tablesObjectsSize());
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
	 * Najde Objekts pro Vektor IDecek. Jestli ne, tak sahne na SlowlyMemory.
	 * Pri hledani rozbali objekty do prvniho Levelu a zkusi najit podobnost.
	 * Seradi elementy v objectsTableCollection dle vyuzivanosti.
	 * 
	 * Find the Objekts to ID vector. If not, feel the SlowlyMemory.
	 * When searching, expand the first level and try to find similarities.
	 * Sorts the elements in objectsTableCollection by used frequency.
	 * 
	 * @param objectsId
	 * @return
	 * @throws Exception
	 */
	public Vector<Objects> getObjects(Vector<Long> objectsId) throws Exception {
		Vector<Objects> result = new Vector<Objects>();
		Vector<Objects> localObjectsTable = (Vector<Objects>)objectsCollection;
		Vector<Long> missingObjectsId = new Vector<Long>();
		// zkusime dohledat objekty v rychle pameti
		label:
		for (int i = 0; i < objectsId.size(); i++) {
			for (int k = 0; k < localObjectsTable.size(); k++) {
				Objects ob = localObjectsTable.get(k);
				if (ob.getId().compareTo(objectsId.get(i)) == 0) {
					result.add(i, ob);
					// zvednout o jednu pozici nalezeny objekt
					if (k > 0) {
						Objects tempObj = localObjectsTable.get(k-1);
						localObjectsTable.set(k-1, ob);
						localObjectsTable.set(k, tempObj);
					}
					continue label;
				}
			}
			result.add(i, null);
			missingObjectsId.add(objectsId.get(i));
		}
		if (missingObjectsId.size() > 0) { // dohlesat objekty v DB
			Vector<Objects> objectsFromSlowlyMemory = slowlyMemory.getObjects(missingObjectsId);
			// Vyplnit chybejici objekty v result, doplnit objectsTableCollection
			int pos = 0;
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i) == null) {
					result.set(i, objectsFromSlowlyMemory.get(pos));
					pos++;
				}
			}
			if (result.contains(null)) {
				throw new Exception("result contains null element. " + result.toString());
			}
			// doplnit FastMemory cerstvymi Objekty
			addObjects(result);
		}
		return result;
	}

	/* Viz. Memory getNewObject(...) */
	public Objects getNewObject(Thought srcThought, Thought tgtThought) throws Exception {
		Objects result = slowlyMemory.getNewObject(srcThought, tgtThought);
		// pridat object na konec kolekce
		Vector<Objects> vector = new Vector<Objects>();
		vector.add(result);
		addObjects(vector);
		return result;
	}

	/**
	 * Dohleda Association ve FastMemory. Jestli nenajde, zkusi najit ve SlowlyMemory.
	 * 
	 * Supervision Association in FastMemory. If not found, try to find the SlowlyMemory.
	 * 
	 * @param srcThought
	 * @param tgtThought
	 * @return
	 * @throws Exception 
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		Vector<Associations> assocTable = (Vector<Associations>)associationsCollection;
		long srcObjectId = srcThought.getActiveObject().getId();
		long tgtObjectId = tgtThought.getActiveObject().getId();
		for (int i = 0; i < assocTable.size(); i++) {
			Associations nextAssoc = assocTable.get(i);
			if (nextAssoc.getSrcId() == srcObjectId && nextAssoc.getTgtId() == tgtObjectId) {
				if (i > 0) { // Zvednout o jednu pozici pouzitou Associations
					Associations tempAssoc = assocTable.get(i-1);
					assocTable.set(i-1, nextAssoc);
					assocTable.set(i, tempAssoc);
				}
				return nextAssoc;
			}
		}
		// Nenalezeno. Zkusit najit v SlowlyMemory
		Associations result = slowlyMemory.getAssociation(srcThought, tgtThought);
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
	 * Zvysi COST u asociaci, ktere maji obj_id z parametru jak v DB,
	 * tak i v cashe
	 * 
	 * increase the associations COST, which are obj_id parameter of both DB
	 * as well as in cash
	 * 
	 * @param obIdArray - array of Objects ID
	 * @throws SQLException 
	 */
	public void increaseAssociationsCostToObjectsId(Long[] obIdArray) throws SQLException {
		Vector<Associations> localAssocTable = (Vector<Associations>)associationsCollection;
		for (int i = 0; i < localAssocTable.size(); i++) {
			Associations nextAssoc = localAssocTable.get(i);
			for (int k = 0; k < obIdArray.length; k++) {
				if (nextAssoc.getObjId() == obIdArray[k]) {
					nextAssoc.setCost(nextAssoc.getCost() + 1);
				}
			}
		}
		slowlyMemory.increaseAssociationsCostToObjectsId(obIdArray);
	}

	/**
	 * Zvysi COST associaci o jednicku jak v DB, tak i v cashe
	 * 
	 * COST Associations will increase by one in both the DB and in Cash
	 * 
	 * @param associationsId - Associations \id, in which the COST is to lift
	 * @throws Exception 
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		slowlyMemory.increaseAssociationsCost(associationsId);
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
	 * @param result
	 * @throws Exception 
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
	 * Nenalezene dohleda v SlowlyMemory a prida k FastMemory cashe.
	 * 
	 * Find associations. Does not create new.
	 * He picks up finding.
	 * Found in the supervisors and adding SlowlyMemory FastMemory Cash.
	 * 
	 * @param thoughtsPairToUnion
	 * @return Vector<Associations> Nenalezene pozice obsahuji null
	 * @throws Exception 
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
				if (nextAssoc.getSrcId() == th1.getActiveObject().getId() 
						&& nextAssoc.getTgtId() == th2.getActiveObject().getId()) {
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
		// dohledat associations v SlowlyMemory
		Vector<Associations> assocFromSlowlyMemory = 
			slowlyMemory.getAssociations(thoughtsPairToUnion, notFoundPositions);
		// Pripojit doledane assocFromSlowlyMemory k resultu
		for (int i = 0; i < assocFromSlowlyMemory.size(); i++) {
			Associations nextAssoc = assocFromSlowlyMemory.get(i);
			if (nextAssoc != null) {
				result.set(i, nextAssoc);
			}
			
		}
		// Odstranit prazdne polozky z assocFromSlowlyMemory
		while (assocFromSlowlyMemory.contains(null)) {
			assocFromSlowlyMemory.remove(null);
		}		
		// pridat associace na konec associationsTableCollections
		addAssociations(result);
		return result;
	}

	/**
	 * Vytvori nove objekty a asociace. Prida je do objectsTableCollection a associationsTableCollection
	 * @param thoughtPairsToUnion - pary Thought, ktere zarucene nemaji Associations ani spolecny Objects
	 * @throws Exception 
	 */
	public void createNewAssociationsAndObjects(
			Vector<Thought> thoughtPairsToUnion) throws Exception {

		Vector<Objects> newObjects = slowlyMemory.getNewObjects(thoughtPairsToUnion);
		Vector<Thought> newThoughts = new Vector<Thought>();
		
		/** Prida nove objects do FastMemory */
		addObjects(newObjects);
		
		/** Vytvori Thoughts z objektu */
		for (int i = 0; i < newObjects.size(); i++) {
			Objects nextOb = newObjects.get(i);
			Thought th = new Thought(nextOb, new Vector<Associations>());
			newThoughts.add(th);
		}
		
		Vector<Associations> newAssociations = slowlyMemory.insertAssociations(
				thoughtPairsToUnion, newObjects);
		// Prida nove associations k FastMemory cashe
		getAssociations(thoughtPairsToUnion);
		
		// Prida nove associations do FastMemory
		addAssociations(newAssociations);
		
		// Prida aktivni objekty do FastMemory cashe
		addObjects(newThoughts);
		
		// TODO vymyslit test
	}

	public void addObjects(Vector<Thought> newThoughts) {
		Vector<Objects> activeObjects = new Vector<Objects>();
		for (int i = 0; i < newThoughts.size(); i++) {
			Thought nextThought = newThoughts.get(i);
			activeObjects.add(nextThought.getActiveObject());
		}
		// Prida nove Objects do FastMemory
		addObjects(activeObjects);
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru.
	 * Jestli pro aspon jeden z objektu nic nenajde, sahne na SlowlyMemory.
	 * Zde zavadim neco jako Hluboke vyhledavani a Melke vyhledavani,
	 * Melke - hledat jen ve FastMemory.
	 * 	uspokojit se, kdyz ve FastMemory nalezena aspon jedna Association,
	 * 	kdyz aspon jedna chybi - 
	 * 		dohledat associations u vsech prvku,
	 * 		nebo
	 * 		dohledat associations jen u chybejicich prvku.
	 * Hluboke - dohledat vzdy v SlowlyMemory.  
	 * @param objectsId - Vector idecek Objektu.
	 * @return - Vector<Associations> bud melke, stredni nebo hluboke dle nastavenych parametru
	 * @throws Exception 
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception {
		boolean deepSearch = config.isFastMemory_alwaysSearchToAssociationsDeepInTheMemory();
		Vector<Associations> tempAssociations = (Vector<Associations>)associationsCollection;
		Vector<Associations> result = new Vector<Associations>();
		if (deepSearch) { // TODO porovnat s Melkym vyhledavanim v ruznych rezimech.
			result = slowlyMemory.getAllAssociations(objectsId);
		} else {
			boolean searchAtAllElements = config.isFastMemory_searchToAssociationsAtAllElements();
			Vector<Long> notFoundObjectsId = (Vector<Long>)objectsId.clone();
			for(int i = 0; i < objectsId.size(); i++) {
				Long nextId = objectsId.get(i);
				boolean found = false;
				for (int k = 0; k < tempAssociations.size(); k++) {
					Associations ass = tempAssociations.get(k);
					if (nextId == ass.getSrcId()) {
						while (notFoundObjectsId.contains(nextId)) {
							notFoundObjectsId.removeElement(nextId);
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
						// Zahodit nalezene a vyhledat vse v SlowlyMemory
						result = slowlyMemory.getAllAssociations(objectsId);
					} else {
						// Pokracovat ve filtrovani notFoundObjectsId, dohledat jen chybejici a pridat k resultu
						continue;
					}
				}
				Vector<Associations> missingAssociations = slowlyMemory.getAllAssociations(notFoundObjectsId);
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
	 * @param newAssociations
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
	 * Prida objects na zacatek objectsCollection.
	 * Jestli objects existuje - zvedne ho o jednu pozici nahoru.
	 * @param newObjects
	 */
	public void addObjects(Collection<Objects> newObjects) {
		Vector<Objects> tempObjects = (Vector<Objects>)objectsCollection;
		for (Objects ob : newObjects) {
			int pos = tempObjects.indexOf(ob);
			if (pos == -1) { // associace nenalezena. Pridat na zacatek.
				tempObjects.add(0, ob);
			}
			if (pos > 0) { // associace jiz existuje v tempAssociations. Zvednout nahoru.
				Objects tempOb = tempObjects.get(pos-1);
				tempObjects.set(pos-1, ob);
				tempObjects.set(pos, tempOb);
			} else { // associace jiz je na zacatku. Nic neprovadet.
				;
			}
		}
		int maxObjectsSize = config.getFastMemory_tablesObjectsSize();
		if (tempObjects.size() > maxObjectsSize) {
			tempObjects.setSize(maxObjectsSize);
		}
	}

	/**
	 * Dohleda spolecny Thought pro par. Nic nevytvari.
	 * @param th1
	 * @param th2
	 * @return
	 * @throws Exception
	 */
	public Thought getThought(Thought th1, Thought th2) throws Exception {
		Associations ass = th1.getAssociation(th2);
		Long objId = ass.getObjId();
		Vector<Long> vector = new Vector<Long>();
		vector.add(objId);
		Vector<Objects> objectsVector = getObjects(vector);
		Objects activeObject = null;
		if (objectsVector.size() > 0) {
			activeObject = objectsVector.firstElement();
		}
		Vector<Associations> consequenceAssociations = getAllAssociations(vector);
		return new Thought(activeObject, consequenceAssociations);
	}
	
	/**
	 * Dohleda vsechny objekty. Nevytvari nove. Zvedne pozici objektu v cashe jestli existuje.
	 * Prida object do cashe jestli neexistuje.
	 * @param inputObjects
	 * @return
	 * @throws Exception
	 */
	public Vector<Objects> getObjects(Long[] inputObjects) throws Exception {
		Vector<Long> unitedObjectsId = new Vector<Long>();
		for (int i = 0; i < inputObjects.length; i++) {
			unitedObjectsId.add(inputObjects[i]);
		}
		Vector<Objects> result = getObjects(unitedObjectsId);
		return result;
	}

	/**
	 * Vytvori nove Associations pro pary Thought.
	 * @param nonExistsPairs - pary Thought pro spojeni, ktere zarucene
	 * nemaji Objects ani Associations.
	 * @return Vector<Associations> serazeny stejne jako vstupni data.
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public Vector<Associations> getNewAssociations(
			Vector<Thought> nonExistsPairs) throws SQLException, Exception {
		createNewAssociationsAndObjects(nonExistsPairs);
		Vector<Associations> result = getAssociations(nonExistsPairs);
		return result;
	}
	
	/**
	 * Dohleda vsechny asociace. Nevytvari nove. Zvedne pozici asociace v cashe jestli existuje.
	 * Prida asociaci do cashe jestli neexistuje.
	 * @param objects
	 * @return
	 * @throws Exception 
	 */
	public Vector<Associations> getAssociationsToObjects(Vector<Objects> objects) throws Exception {
		Vector<Long> objectsId = new Vector<Long>();
		for (int i = 0; i < objects.size(); i++) {
			objectsId.add(objects.get(i).getId());
		}
		Vector<Associations> result = getAllAssociations(objectsId);		
		return result;
	}
	
	/**
	 * Vyhleda id znaku ve FastMemory, kdyz nenajde tak v SlowlyMemory,
	 * kdyz nenajde tak zalozi novy znak ve SlowlyMemory.
	 */
	public Long[] getObjects(char[] inputChars) throws Exception {
		Long[] result = getCharsId(inputChars);
		return result;
	}
	
	public Vector<Long> getSuperiorObjectsId(Vector<Long> layer, int constant) throws SQLException {
		return slowlyMemory.getSuperiorObjectsId(layer, constant);
	}
}