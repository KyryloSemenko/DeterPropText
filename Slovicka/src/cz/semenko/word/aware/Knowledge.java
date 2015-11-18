package cz.semenko.word.aware;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
 * The place where thoughts were living. Knowledge differs from subconscious mind
 * ability to activate events and actions.
 * Knowledge process to more credible thoughts and scenario then subconscious mind.
 * 
 * <p>Vedomi.<br>
 * Misto kde se roji myslenky. Lisi se od podvedomi tim, ze muze aktivovat ciny, akce.
 * Lisi se od podvedomi tim, ze zpracovava vice verohodnejsi myslenky nebo scenare.
 * <p>Class diagram</p>
 * <img src="doc-files\class_diagram_Knowledge.png"/>
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
	 * Predicts a continuation of a text.
	 * @param input - array of {@link Cell#id} has been read from input
	 */
	public List<Cell> suggest(Long[] input) {
		List<Cell> result = new ArrayList<Cell>();
		// TODO naimplementovat. Spravovat logiku dle patternu MVC nebo Chain of responsibility.
		return result;
	}
	
	/**
	 * Metoda ktera vyhledava jiz zname objekty, vytvari nove, nastavuje COST u znamych Associations. Behem zpracovani vstupnich objektu tato metoda
	 * ovlivnuje stav Knowledge - vedomi.
	 * 
	 * @param inputCells IDs of {@link cz.semenko.word.persistent.Cell} objects that has been read in one hit, see
	 * {@link cz.semenko.word.Config#dataProvider_numCharsReadsFromInput}
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

	/**
	 * <p>
	 * Provest dohledani v DB. Pak vytvaret nove objekty do takove hloubky, jak je to definovano v configuracni promenne. TODO hloubku vytvareni
	 * objektu musi definovat a ridit zvlastni objekt. Tato hloubka by mela zalezet na moznostech hardwarovych prostredku a od nastavene politiky
	 * uchovavani objektu.<br />
	 * <br />
	 * Novy objekt by mel mit reference na nasledujici objekt. Na druhou stranu, nove objekty se ctou ve blocich. Tak ze mozna by se meli zpracovavat
	 * stosem.<br />
	 * <br />
	 * Otazkou je, zda reference maji odkazovat na predchozi primitivni objekt, nebo na sbaleny objekt. Pak je otazkou, zda maji se uchovavat
	 * reference na predchazejici objekty a do jake hloubky.
	 * </p>
	 * 
	 * <p>
	 * Reference na objekty dozadu nebo dopredu prez objekt mohou byt pouzite pro odhaleni chyb a navrh opravy.Soucasne reference dozadu nebo dopredu
	 * pomuzou odhalit pravidla a vyjimky z pravidel.<br />
	 * <br />
	 * Jednym z cilu je dohledani opakujicich se sekvenci.
	 * </p>
	 * 
	 * <p>
	 * Zatim implementuji nasledujici scenar:
	 * </p>
	 * 
	 * <ul>
	 * <li>Projit vsechny pary v celem thoughts2, zda nemaji assoc na nasledujici objekt.</li>
	 * <li>Spojit vsechny ktere maji, nacist nove vznikle Thought.</li>
	 * </ul>
	 * 
	 * <p>
	 * Projit postupne vsechny objekty v thoughts2, a u Thought, ktere nemaji pozadovanou hloubku zapouzdreni (zabaleni) objektu, provest slouceni s
	 * nasledujicim objektem. Vyjimkou je posledni prvek.
	 * </p>
	 * 
	 * <p>
	 * Podrobnejsi scenar:
	 * </p>
	 * 
	 * <ul>
	 * <li>Projit vsechny pary v celem oldThoughts, zda jsou vhodnymi klijenty pro spojeni</li>
	 * <li>Jestli objekty pro spojeni jsou vedle sebe, rozhodnout ktery z objektu ma prioritu</li>
	 * <li>Dostat z DB vsechny asociace pro nove objekty. Jestli asociace a novy objekt neexistuje, vytvorit novy Cell a Associations</li>
	 * <li>Sestavit Thoughts pro nove objekty</li>
	 * <li>Nahradit Spojene Thoughts novymi Thoughts</li>
	 * </ul>
	 */
	private Vector<Thought> getThoughtsToSomeDepth(Vector<Thought> oldThoughts) throws Exception {
		while (true) {
			/** Ziskame seznam pozici prvku v thoughts2 pro spojeni.*/
			List<Integer> positionsToRelation = thoughtUnionDecider.getPositionsToRelation(oldThoughts);
			/** Vytvorime nove Associations, vytvorime nove Cell ktere drive neexistovali a zvysime Cost u stavajicich*/
			increaseAssociationsCost(oldThoughts, positionsToRelation); // Ted vsechny objekty a associace existuji
			if (positionsToRelation.isEmpty()) {
				return oldThoughts;
			}
			/** Sestavime pary pro spojovani. */
			Vector<Thought> thoughtsPairToUnion = new Vector<Thought>();
			for (int i = 0; i < positionsToRelation.size(); i++) {
				thoughtsPairToUnion.add(oldThoughts.get(positionsToRelation.get(i)));
				thoughtsPairToUnion.add(oldThoughts.get(positionsToRelation.get(i) + 1));
			}
			/** Ziskame idecka vsech novych objektu. Zde nemaji co delat prazdne objekty. */
			Vector<Long> unitedCellsId = getNewCellsId(oldThoughts, positionsToRelation);
			if (unitedCellsId.contains(null)) {
				throw new RuntimeException("Empty cells are not allowed in this place. " + unitedCellsId);
			}
			/** Ziskame vsechny asociace novych objektu. Nevytvarime nove. */
			Set<Associations> allAssociations = fastMemory.getAllAssociations(unitedCellsId);
			/** Ziskame vsechny nove objekty. Nevytvarime nove. */
			Vector<Cell> newCells = fastMemory.getCells(unitedCellsId);
			/** sestavime Thoughts. */
			Vector<Thought> newThoughts = new Vector<Thought>();
			for (int n = 0; n < newCells.size(); n++) {
				Cell ob = newCells.get(n);
				Set<Associations> assocVector = new HashSet<Associations>();
				for (Associations assoc : allAssociations) {
					if (ob.getId() == assoc.getSrcId()) {
						assocVector.add(assoc);
					}
				}
				Thought thought = new Thought(ob, assocVector);
				newThoughts.add(thought);
			}
			// nahradime stare spojovane pary v thoughs2 na nove spojene newThoughts.
			for (int n = newThoughts.size()-1; n >= 0; n--) {
				int pos = positionsToRelation.remove(positionsToRelation.size()-1);
				oldThoughts.remove(pos+1);
				oldThoughts.remove(pos);
				oldThoughts.add(pos, newThoughts.get(n));
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
			List<Integer> positionsToRelation) throws Exception {
		Vector<Long> unitedCellsId = new Vector<Long>();
		for (int k = 0; k < positionsToRelation.size(); k++) {
			Thought srcThought = thoughts2.get(positionsToRelation.get(k));
			Thought tgtThought = thoughts2.get(positionsToRelation.get(k)+1);
			Associations assoc = fastMemory.getAssociation(srcThought, tgtThought);
			if (assoc == null) {
				unitedCellsId.add(null);
			} else {
				unitedCellsId.add(assoc.getCellId());
			}
		}
		return unitedCellsId;
	}

	/**
	 * Zvysi cost associaci spojovanych objektu. Jestli association neexistuje,
	 * vytvori novou. Vytvari nove objety.
	 * @param thoughts2
	 * @param positionsToRelation
	 * @return Vector<Associations> novych vytvorenych associations
	 * @throws Exception
	 */
	private Vector<Associations> increaseAssociationsCost(Vector<Thought> thoughts2,
			List<Integer> positionsToRelation) throws Exception {
		// doplnit chybejici associations
		Vector<Thought> nonExistsPairs = new Vector<Thought>();
		Vector<Associations> result = new Vector<Associations>();
		for (Iterator<Integer> it = positionsToRelation.iterator(); it.hasNext(); ) {
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
		for (Iterator<Integer> it = positionsToRelation.iterator(); it.hasNext(); ) {
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
	 * @param inputCells IDs of {@link cz.semenko.word.persistent.Cell} objects that has been read in one hit,
	 * see {@link cz.semenko.word.Config#dataProvider_numCharsReadsFromInput}<br>
	 **/
	private Vector<Thought> attachToExistingThoughts(Vector<Thought> thoughts2,
			Long[] inputCells) throws Exception {
		/* Nejdriv dohledat spicky pro rozhodovani, jake objekty spojovat 
		 * a pospojovat tyto objekty. Pritom zvednout COST u asociaci,
		 * ktere vytvareji tyto spickove objekty */
		// TODO zrychlit
		inputCells = thoughtUnionDecider.getTipsAndJoinCells(inputCells);
		// Vytvorit vektor Thoughts
		Vector<Thought> newThoughts = new Vector<Thought>();
		newThoughts = getExistsThoughts(inputCells);
		// posledni element z thoughts2 prehodit k newThoughts
		if (thoughts2.size() > 0) {
			newThoughts.add(0, thoughts2.remove(thoughts2.size()-1));
		}
		// pospojovat jiz existujici pary
		// TODO zrychlit
		newThoughts = recognizeKnownThoughts(newThoughts);
		thoughts2.addAll(newThoughts); 
		// Rozhodnout ktere Thoughts sloucit na zaklade konfiguracnich parametru.
		Vector<Integer> positionsToCreateNewCells = cellsCreationDecider.getPositionsToCreateNewCells(thoughts2);
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
		while(true) {
			// Nejdriv dostneme doporuceni ke spojeni
			List<Integer> positionsToRelation = thoughtUnionDecider.getPositionsToRelation(newThoughts);
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
//TODO Smazat		// Zvysime COST asociaci jak puvodnich, tak i spojenych Thoughts
//		fastMemory.increaseAssociationsCost(assocVector);
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
		Set<Associations> associations = fastMemory.getAssociationsToCells(cells);
		// Create thought
		Vector<Thought> result = new Vector<Thought>();
		for (int i = 0; i < cells.size(); i++) {
			Cell activeCell = cells.get(i);
			Set<Associations> consequenceAssociations = new HashSet<Associations>();
			for (Associations nextAss : associations) {
				if (nextAss.getSrcId().compareTo(activeCell.getId()) == 0) {
					consequenceAssociations.add(nextAss);
				}
			}
			Thought th = new Thought(activeCell, consequenceAssociations);
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
