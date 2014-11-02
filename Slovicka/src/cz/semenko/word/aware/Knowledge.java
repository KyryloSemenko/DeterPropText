package cz.semenko.word.aware;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.policy.CellCreationDecider;
import cz.semenko.word.aware.policy.ThoughtUnionDecider;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * <p>Knowledge.<br>
 * The place where thoughts are living. Knowledge differs from subconscious mind
 * ability to activate events and actions.
 * Knowledge process to more credible thoughts and scenario then subconscious mind.
 * 
 * <p>Vedomi.<br>
 * Misto kde se roji myslenky. Lisi se od podvedomi tim, ze muze aktivovat ciny, akce.
 * Lisi se od podvedomi tim, ze zpracovava vice verohodnejsi myslenky nebo scenare.
 * 
 * @author Kyrylo Semenko
 */
public class Knowledge {
	private Vector<Thought> thoughts = new Vector<Thought>();
	private FastMemory fastMemory;
	private ThoughtUnionDecider thoughtUnionDecider;
	private ThoughtsSaver thoughtsSaver;
	private CellCreationDecider cellsCreationDecider;
	private Config config;
	
	/**
	 * Empty constructor
	 */
	public Knowledge() {}
	
	/**
	 * Predicts of the continuation of the text.
	 * @param inputCells array of {@link Cell} of first type - signs.
	 */
	public void predict(Long[] inputCells) {
		
	}
	
	/**
	 * Rekurzivni metoda ktera vyhledava jiz zname objekty, vytvari nove, nastavuje COST
	 * u znamych Associations. Behem zpracovani vstupnich objektu tato metoda ovlivnuje
	 * stav Knowledge - vedomi.
	 */
	public void remember(Long[] inputCells) throws Exception {
		// Pripojit inputCells k predchozim myslenkam
		thoughts = attachToExistingThoughts(thoughts, inputCells);
		// Provest dohledani v DB. Pak vytvaret nove objekty do takove hloubky, jak je to
		// definovano v konfiguracni promenne.
		thoughts = getThoughtsToSomeDepth(thoughts);
		// Odkazat objekty na sebe vzajemne (svazat je logicky). Jak daleko a jak dlouho
		// budou objekty o sobe vedet bude definovano v configuracnich promennych.
		//TODO: thoughts = createLogicalRelations(thoughts);
	}

	/* Provest dohledani v DB. Pak vytvaret nove objekty do takove hloubky, jak je to
	definovano v configuracni promenne. TODO hloubku vytvareni objektu musi definovat
	a ridit zvlastni objekt. Tato hloubka by mela zalezet na moznostech hardwarovych
	prostredku a od nastavene politiky uchovavani objektu. 
	
	Novy objekt by mel mit reference na nasledujici objekt. Na druhou stranu, nove objekty
	se ctou ve blocich. Tak ze mozna by se meli zpracovavat stosem.
	
	Otazkou je, zda reference maji
	odkazovat na predchozi primitivni objekt, nebo na sbaleny objekt. Pak je otazkou, zda
	maji se uchovavat reference na predchazejici objekty a do jake hloubky.
	Reference na objekty dozadu nebo dopredu prez objekt mohou byt pouzite pro odhaleni chyb a navrh opravy.
	Soucasne reference dozadu nebo dopredu pomuzou odhalit pravidla a vyjimky z pravidel.
	
	Jednym z cilu je dohledani opakujicich se sekvenci.
	
	Zatim implementuji nasledujici scenar:
		Projit vsechny pary v celem thoughts2, zda nemaji assoc na nasledujici objekt.
		Spojit vsechny ktere maji, nacist nove vznikle Thought.
		
		Projit postupne vsechny objekty v thoughts2, a u Thought, ktere nemaji pozadovanou hloubku
		zapouzdreni (zabaleni) objektu, provest slouceni s nasledujicim objektem. Vyjimkou je posledni
		prvek.
	Podrobnejsi scenar:
		Projit vsechny pary v celem thoughts2, zda jsou vhodnymi klijenty pro spojeni.
		Jestli objekty pro spojeni jsou vedle sebe, rozhodnout ktery z objektu ma prioritu.
		Dostat z DB vsechny asociace pro nove objekty. Jestli asociace a novy objekt neexistuje, vytvorit
			novy Cell a Associations.
		Sestavit Thoughts pro nove objekty.
		Nahradit Spojene Thoughts novymi Thoughts.			
	*/
	private Vector<Thought> getThoughtsToSomeDepth(Vector<Thought> thoughts2) throws Exception {
		while (true) {
			/** Ziskame seznam pozici prvku v thoughts2 pro spojeni.*/
			Vector<Integer> positionsToRelation = thoughtUnionDecider.getPositionsToRelation(thoughts2);
			if (positionsToRelation.isEmpty()) {
				return thoughts2;
			}
			/** Vytvorime nove Associations, vytvorime nove Cell ktere drive neexistovali a zvysime Cost u stavajicich*/
			increaseAssociationsCost(thoughts2, positionsToRelation); // Ted vsechny objekty a associace existuji
			/** Sestavime pary pro spojovani. */
			Vector<Thought> thoughtsPairToUnion = new Vector<Thought>();
			for (int i = 0; i < positionsToRelation.size(); i++) {
				thoughtsPairToUnion.add(thoughts2.get(positionsToRelation.get(i)));
				thoughtsPairToUnion.add(thoughts2.get(positionsToRelation.get(i) + 1));
			}
			/** Ziskame idecka vsech novych objektu. Zde nemaji co delat prazdne objekty. */
			Vector<Long> unitedCellsId = getNewCellsId(thoughts2, positionsToRelation);
			if (unitedCellsId.contains(null)) {
				throw new Exception("Zde nemaji co delat prazdne objekty. " + unitedCellsId);
			}
			/** Ziskame vsechny asociace novych objektu. Nevytvarime nove. */
			Vector<Associations> allAssociations = fastMemory.getAllAssociations(unitedCellsId);
			/** Ziskame vsechny nove objekty. Nevytvarime nove. */
			Vector<Cell> newCells = fastMemory.getCells(unitedCellsId);
			/** sestavime Thoughts. */
			Vector<Thought> newThoughts = new Vector<Thought>();
			for (int n = 0; n < newCells.size(); n++) {
				Cell ob = newCells.get(n);
				Vector<Associations> assocVector = new Vector<Associations>();
				for (int i = 0; i < allAssociations.size(); i++) {
					Associations assoc = allAssociations.get(i);
					if (ob.getId() == assoc.getSrcId()) {
						assocVector.add(assoc);
						//continue; // TODO spravne musi skocit na for(int n = 0; n < allAssociations.size(); n++) {
					}
				}
				Thought thought = new Thought(ob, assocVector);
				newThoughts.add(thought);
			}
			/** nahradime stare spojovane pary v thoughs2 na nove spojene newThoughts. */
			for (int n = newThoughts.size()-1; n >= 0; n--) {
				int pos = positionsToRelation.remove(positionsToRelation.size()-1);
				thoughts2.remove(pos+1);
				thoughts2.remove(pos);
				thoughts2.add(pos, newThoughts.get(n));
			}
		}
	}

	/**
	 * Vrati Vektor IDecek, ktery muze obsahovat i nullove hodnoty
	 * @param thoughts2
	 * @param cellsToRelation Vektor pozici v thoughts2 pro spojeni s nasledujicimi objekty
	 * @return Vektor IDecek Objektu
	 * @throws Exception 
	 */
	private Vector<Long> getNewCellsId(Vector<Thought> thoughts2,
			Vector<Integer> positionsToRelation) throws Exception {
		Vector<Long> unitedCellsId = new Vector<Long>();
		for (int k = 0; k < positionsToRelation.size(); k++) {
			Thought srcThought = thoughts2.get(positionsToRelation.get(k));
			Thought tgtThought = thoughts2.get(positionsToRelation.get(k)+1);
			Associations assoc = fastMemory.getAssociation(srcThought, tgtThought);
			if (assoc == null) {
				unitedCellsId.add(null);
			} else {
				unitedCellsId.add(assoc.getObjId());
			}
		}
		return unitedCellsId;
	}

	/**
	 * Zvysi cost associaci spojovanych objektu. Jestli association neexistuje,
	 * vytvori novou. Vytvari nove objety.
	 * @param thoughts2
	 * @param cellsToRelation
	 * @return Vector<Associations> novych vytvorenych associations
	 * @throws Exception
	 */
	private Vector<Associations> increaseAssociationsCost(Vector<Thought> thoughts2,
			Vector<Integer> cellsToRelation) throws Exception {
		// doplnit chybejici associations
		Vector<Thought> nonExistsPairs = new Vector<Thought>();
		Vector<Associations> result = new Vector<Associations>();
		for (Iterator<Integer> it = cellsToRelation.iterator(); it.hasNext(); ) {
			Integer nextKey = it.next();
			Thought srcThought = thoughts2.get(nextKey);
			Thought tgtThought = thoughts2.get(nextKey+1);
			Associations assoc = fastMemory.getAssociation(srcThought, tgtThought);
			if (assoc == null) {
				nonExistsPairs.add(srcThought);
				nonExistsPairs.add(tgtThought);
			}
		}
		if (nonExistsPairs.size() > 0) {
			result = fastMemory.getNewAssociations(nonExistsPairs);
		}		
		// zvysit cost associations ktere jiz existovaly v DB
		Vector<Long> associationsToIncrease = new Vector<Long>();
		for (Iterator<Integer> it = cellsToRelation.iterator(); it.hasNext(); ) {
			Integer nextKey = it.next();
			Thought srcThought = thoughts2.get(nextKey);
			Thought tgtThought = thoughts2.get(nextKey+1);
			Associations assoc = fastMemory.getAssociation(srcThought, tgtThought);
			if (assoc != null) {
				associationsToIncrease.add(assoc.getId());
			}
		}
		if (associationsToIncrease.size() > 0) {
			fastMemory.increaseAssociationsCost(associationsToIncrease);
		}
		return result;
	}

	/** Pripojit inputCells k predchozim myslenkam.
	 * Rozhodnout ktere Thoughts sloucit na zaklade konfiguracnich parametru.
	 * Vytvorit nove objekty.
	 * Omezit velikost vektoru myslenek aby nedochazelo k preteceni.
	 **/
	private Vector<Thought> attachToExistingThoughts(Vector<Thought> thoughts2,
			Long[] inputCells) throws Exception {
		/* Nejdriv dohledat spicky pro rozhodovani, jake objekty spojovat 
		 * a pospojovat tyto objekty. Pritom zvednout COST u asociaci,
		 * ktere vytvareji tyto spickove objekty */
		inputCells = thoughtUnionDecider.getTipsAndJoin(inputCells);
		// Vytvorit vektor Thoughts
		Vector<Thought> newThoughts = new Vector<Thought>();
		newThoughts = getExistsThoughts(inputCells);
		// posledni element z thoughts2 prehodit k newThoughts
		if (thoughts2.size() > 0) {
			newThoughts.add(0, thoughts2.remove(thoughts2.size()-1));
		}
		// pospojovat jiz existujici pary
		newThoughts = recognizeKnownThoughts(newThoughts);
		thoughts2.addAll(newThoughts);
		// Rozhodnout ktere Thoughts sloucit na zaklade konfiguracnich parametru.
		Vector<Integer> positionsToCreateNewCells = 
			cellsCreationDecider.getPositionsToCreateNewCells(thoughts2);
		// Vytvorime nove cells a associations
		Vector<Thought> thoughtPairsToMerge = new Vector<Thought>();
		for (int i = 0; i < positionsToCreateNewCells.size(); i++) {
			thoughtPairsToMerge.add(thoughts2.get(i));
			thoughtPairsToMerge.add(thoughts2.get(i+1));
		}
		if (thoughtPairsToMerge.size() > 0) {
			fastMemory.createNewAssociationsAndCells(thoughtPairsToMerge);
		}
		// Orezat thoughts2 na pozadovanou velikost
		int maxKnowledgeSize = config.getKnowledge_knowledgeSize();
		boolean saveThoughtsToFile = config.isKnowledge_saveThoughtsToFile();
		if (maxKnowledgeSize < thoughts2.size()) {
			int num = thoughts2.size() - maxKnowledgeSize;
			for (int i = 0; i < num; i++) {
				if (saveThoughtsToFile) {
					thoughtsSaver.saveCellId(thoughts2.get(0).getActiveCell().getId());
				}
				thoughts2.remove(0);
			}
		}
		return thoughts2;
	}

	/**
	 * Provede spojeni jiz existujicich paru do hloubky. Behem spojeni pouzije strategii
	 * pro optimalni nejrychlejsi nalezeni. Zvysuje COST u asociaci. 
	 * @param newThoughts
	 * @return
	 * @throws Exception 
	 */
	private Vector<Thought> recognizeKnownThoughts(Vector<Thought> newThoughts) throws Exception {
		// Associations pro zvyseni COST
		Vector<Long> assocVector = new Vector<Long>();
		// Nejdriv dostneme doporuceni ke spojeni
		ThoughtUnionDecider desider = thoughtUnionDecider;
		while(true) {
			Vector<Integer> positionsToRelation = desider.getPositionsToRelation(newThoughts);
			// Pak zjistime ktere thoughts jiz maji spolecnou asociaci a zaroven doporucene ke spojeni z predchoziho filtru	
			Map<Integer, Thought> existsMergedThoughts = new TreeMap<Integer, Thought>();
			for (int i = 0; i < newThoughts.size()-1; i++) {
				if (positionsToRelation.contains(i)) {
					Thought nextTh = newThoughts.get(i);
					Thought nextFollowingTh = newThoughts.get(i+1);
					Associations assoc = nextTh.getAssociation(nextFollowingTh);
					if (assoc != null) {
						assocVector.add(assoc.getId());
						existsMergedThoughts.put(i, fastMemory.getThought(nextTh, nextFollowingTh));
					}
				}
			}
			if (existsMergedThoughts.size() == 0) {
				break;
			}
			// Pospojovat Thoughts ve newThoughts
			Set<Integer> positions = (Set<Integer>)existsMergedThoughts.keySet();
			int[] posArr = new int[positions.size()];
			Iterator<Integer> posIter = positions.iterator();
			for (int i = posArr.length-1; i >= 0; i--) {
				posArr[i] = posIter.next();
			}
			for (int i = 0; i < posArr.length; i++) {
				int posToMerge = posArr[i];
				newThoughts.remove(posToMerge);
				newThoughts.remove(posToMerge);
				newThoughts.insertElementAt(existsMergedThoughts.get(posToMerge), posToMerge);
			}
		}		
		// Zvysime COST asociaci jak puvodnich, tak i spojenych Thoughts
		fastMemory.increaseAssociationsCost(assocVector);
		return newThoughts;
	}

	/** 
	 * Ziska z Memory Cell a Associations a vytvori nove Thoughts.
	 * 
	 * @param inputCells
	 * @return
	 * @throws Exception 
	 */
	private Vector<Thought> getExistsThoughts(Long[] inputCells) throws Exception {
		// Get cells
		Vector<Cell> cells = fastMemory.getCells(inputCells);
		// Get associations
		Vector<Associations> associations = fastMemory.getAssociationsToCells(cells);
		// Create thought
		Vector<Thought> result = new Vector<Thought>();
		for (int i = 0; i < cells.size(); i++) {
			Cell ob = cells.get(i);
			Vector<Associations> ass = new Vector<Associations>();
			for (int k = 0; k < associations.size(); k++) {
				Associations nextAss = associations.get(k);
				if (nextAss.getSrcId().compareTo(ob.getId()) == 0) {
					ass.add(nextAss);
				}
			}
			Thought th = new Thought(ob, ass);
			result.add(th);
		}
		return result;
	}
	
	public Vector<Thought> getThoughts() {
		return thoughts;
	}
	
	public void setFastMemory(FastMemory fastMemory) {
		this.fastMemory = fastMemory;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setCellsCreationDecider(
			CellCreationDecider cellsCreationDecider) {
		this.cellsCreationDecider = cellsCreationDecider;
	}

	public void setThoughtsSaver(ThoughtsSaver thoughtsSaver) {
		this.thoughtsSaver = thoughtsSaver;
	}

	public void setThoughtUnionDecider(ThoughtUnionDecider thoughtUnionDecider) {
		this.thoughtUnionDecider = thoughtUnionDecider;
	}
}
