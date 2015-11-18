package cz.semenko.word.aware.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.LayersManager;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Business process to decide, if TODO
 * @author Kyrylo Semenko
 *
 */
public class ThoughtUnionDecider {
	
	// Komponenty doda Spring FW
	private FastMemory fastMemory;
	private Config config;


	/**
	 * <p>Setter for the field <code>fastMemory</code>.</p>
	 *
	 * @param fastMemory the fastMemory to set
	 */
	public void setFastMemory(FastMemory fastMemory) {
		this.fastMemory = fastMemory;
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
	 * <p>Constructor for ThoughtUnionDecider.</p>
	 */
	public ThoughtUnionDecider() {

	}

	/**
	 * Tento objekt rozhoduje o zpusobu spojeni objektu v thoughts. Existuje nekolik politik
	 * pro spojovani elementu. Elementy jsou spojovany proto, aby v pameti zabiraly mene mista.
	 * Dale diky spojovani muzeme odhalit pravidelnosti ve vyskytech prvku. S touto informaci
	 * bychom meli byt schopny doplnit chybejici prvky, odhalit chyby v textu a navrhovat autodoplneni
	 * textu.<br>
	 * Spojovani je podobne zapouzdreni, jen v pameti nezustavaji stopy zapouzdrenych objektu.
	 * Spojovani je podobne slozkam a podslozkam v pocitaci.
	 *
	 * <ul>Jake by mohly byt rozhodujici faktory?
	 * 	<li>Sirka vytvarenych asociaci a novych objektu (sirku by mel ridit jiny objekt):
	 *		<ul>
	 * 			<li>Vytvaret objekty jen pro ty pary, ktere budou spojovany, nebo pro vsechny pary do hloubky?
	 * 			<li>Jestli do hloubky - databaze bude rychle rust; podkladu pro dalsi spojovani bude vice;
	 * 			<li>analyza textu bude trvat dele; mensi zavistlost na poradi a nahode; ...
	 * 			<li>Musim odzkouset ruzne postupy.
	 * 		</ul>
	 *	<li>Hloubka zapouzdreni:
	 *		<ul>
	 * 			<li>Cim vetsi hloubka zapouzdreni, tim vetsi zobecneni jevu muze byt vytvoreno;<br>
	 * 			vetsi databaze, kterou nejde dobre cistit;<br>
	 * 			mohla by zaviset na hardvarovych moznostech;<br>
	 * 			delsi kus textu nebo znalosti bude v Knowledge; ...
	 * 		</ul>
	 * </ul>
	 *
	 * @param thoughts - Vector jiz spojenych a nespojenych objektu.
	 * @return - Vector pozici v thoughts2, ktere musi byt spojeny.
	 * @throws java.lang.Exception if any.
	 */
	public List<Integer> getPositionsToRelation(Vector<Thought> thoughts) throws Exception {
		// Find pairs of Thoughts to create new Cells
		List<Integer> resultPositions = getCanditatesToRelation(thoughts);
		// Zde osetrime pripad kdyz nekolik objektu za sebou konkuruji ve vytvoreni asociace.
		List<Integer> doNotRelatePositions = new ArrayList<Integer>();
		getDoNotRelatePositions(doNotRelatePositions, thoughts, resultPositions);
		// odstranime asociace ktere prohraly v konkurenci se sousednimi asociacemi
		resultPositions.removeAll(doNotRelatePositions);
		return resultPositions;
	}
	

	/**
	 * Find out {@link Thought} with {@link Cell#getType()} less or equals to {@link Config#getCellsCreationDecider_createNewCellsToAllPairsDepth()}
	 * @param thoughts - Vector of {@link Thought} objects
	 * @return positions of elements in thoughts Vector which could create relation
	 */
	private List<Integer> getCanditatesToRelation(Vector<Thought> thoughts) {
		int relateThoughtsUpToCellType = config.getCellsCreationDecider_createNewCellsToAllPairsDepth();
		List<Integer> positionsToRelation = new ArrayList<Integer>();
		for (int i = 0; i < thoughts.size()-1; i++) {
			Thought nextThought = thoughts.get(i);
			Thought nextFollThought = thoughts.get(i+1);
			if (nextThought.getActiveCell().getType() <= relateThoughtsUpToCellType && nextFollThought.getActiveCell().getType() <= relateThoughtsUpToCellType) {
				positionsToRelation.add(i);
			}
		}
		return positionsToRelation;
	}

	/**
	 * Recursive method. Decide which elements must not be associated. Competition.<br>
	 * Rozhodnout které objekty nespojovat. Konkurence.
	 * @param doNotRelatePositions result, can not be null
	 * @param thoughts context
	 * @param positionsToRelation positions of {@link Thought} to decide
	 */
	private void getDoNotRelatePositions(List<Integer> doNotRelatePositions, Vector<Thought> thoughts, List<Integer> positionsToRelation) {
		List<Integer> copyOfPositionsToRelation = new ArrayList<Integer>(positionsToRelation);
		copyOfPositionsToRelation.removeAll(doNotRelatePositions);
		if (!hasNeighbours(copyOfPositionsToRelation)) {
			return;
		}
		if (config.isThoughtUnionDecider_competitionAllowed()) {
			boolean theSameTypesOnly = config.isKnowledge_relateOnlyCellsOfTheSameTypes();
			boolean byHigherAssocCost = config.isKnowledge_decideToRelateCellsByHigherAssocCost();
			boolean byHigherCellType = config.isKnowledge_decideToRelateCellsByHigherCellType();
			int minAssocCostToRelate = config.getKnowledge_minAssocCostToRelate();
			for (int i = 0; i < copyOfPositionsToRelation.size(); i++) {
				int first = copyOfPositionsToRelation.get(i);
				Thought th1 = thoughts.get(first);
				// Chars without relation always relate to next cell
				if (th1.getActiveCell().getType() == 1 && th1.getConsequenceAssociations().size() == 0) {
					doNotRelatePositions.add(first+1);
					i++;
					continue;
				}
				Thought th2 = thoughts.get(first+1);
				if (theSameTypesOnly) {
					if (!th1.getActiveCell().getType().equals(th2.getActiveCell().getType())) {
						doNotRelatePositions.add(first);
						continue;
					}
				}
				if (copyOfPositionsToRelation.size() == i+1) {
					break;
				}
				int second = copyOfPositionsToRelation.get(i+1);
				if (first == (second - 1)) {
					long firstImportance = 0;
					long secondImportance = 0;
					if (byHigherAssocCost) {
						Associations association = th1.getAssociation(th2);
						if (association != null) {
							firstImportance += association.getCost();
						}
						Associations association2 = th2.getAssociation(thoughts.get(second+1));
						if (association2 != null) {
							secondImportance += association2.getCost();
						}
					}
					if (byHigherCellType) {
						firstImportance += th1.getActiveCell().getType();
						secondImportance += th2.getActiveCell().getType();
					}
					if (firstImportance >= minAssocCostToRelate || secondImportance >= minAssocCostToRelate) {
						if (firstImportance < secondImportance) {
							doNotRelatePositions.add(first);
							i++;
							continue;
						}
						if (firstImportance > secondImportance) {
							doNotRelatePositions.add(second);
							i++;
							continue;
						}
					}
					doNotRelatePositions.add(first);
					doNotRelatePositions.add(second);
					i++;
				}
			}
		} else {
			// Nedovolit spojení jedné pozice dvakrát, například nelze: 1+2, 2+3, 3+4 ale lze: 1+2, 3+4
			for (int i = 0; i < copyOfPositionsToRelation.size()-1; i++) {
				int first = copyOfPositionsToRelation.get(i);
				int second = copyOfPositionsToRelation.get(i+1);
				if (first == (second - 1)) {
					doNotRelatePositions.add(second);
					i++;
				}
			}
		}
		getDoNotRelatePositions(doNotRelatePositions, thoughts, copyOfPositionsToRelation);
		return;
	}
	
	/**
	 * @return true if there is at least one neighbour position, for example 2,3
	 */
	private boolean hasNeighbours(List<Integer> positionsToRelation) {
		for (int i = 0; i < positionsToRelation.size()-1; i++) {
			int first = positionsToRelation.get(i);
			int second = positionsToRelation.get(i+1);
			if (first + 1 == second) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Nejdriv dohledat spicky pro rozhodovani, jake objekty spojovat
	 * a pospojovat tyto objekty. Pritom zvednout COST u asociaci,
	 * ktere vytvareji tyto spickove objekty.
	 *
	 * @param inputCells IDs of {@link cz.semenko.word.persistent.Cell} objects that has been read in one hit,
	 * see {@link cz.semenko.word.Config#dataProvider_numCharsReadsFromInput}<br>
	 * @throws java.lang.Exception if any.
	 * @return an array of {@link java.lang.Long} cells.
	 */
	public Long[] getTipsAndJoinCells(Long[] inputCells) throws Exception {
		LayersManager layers = new LayersManager();
		layers.setFirstLayer(inputCells);
		while (layers.lastLayerHasPairs()) {
			int constant = layers.getCurrentPositionForUnion();
			Vector<Long> layer = layers.getCurrentLayer();
			Vector<Long> superiorLayer = fastMemory.getSuperiorCellsId(layer, constant);
			layers.setLastLayer(superiorLayer);
		}
		// Sestavime objekty od spicek dolu
		Long[] result = layers.getBottomCells();
		fastMemory.increaseAssociationsCost(result);
		return result;
	}


}
