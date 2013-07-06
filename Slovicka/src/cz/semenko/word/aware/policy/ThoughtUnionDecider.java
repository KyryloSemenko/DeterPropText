package cz.semenko.word.aware.policy;

import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Layers;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
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
	 * 			<li>Cim vetsi hloubka zapouzdreni, tim vetsi zobecneni jevu muze byt vytvoreno; vetsi databaze,
	 * 			<li>kterou nejde dobre cistit; mohla by zaviset na hardvarovych moznostech; delsi kus textu nebo
	 * 			<li>znalosti bude v Knowledge; ...
	 * 		</ul>
	 * </ul>
	 *
	 * @param thoughts - Vector jiz spojenych a nespojenych objektu.
	 * @return - Vector pozici v thoughts2, ktere musi byt spojeny.
	 * @throws java.lang.Exception if any.
	 */
	public Vector<Integer> getPositionsToRelation(Vector<Thought> thoughts) throws Exception {
		// Projit vsechny pary v celem thoughts2, zda nemaji assoc na nasledujici objekt
		Vector<Integer> result = getAllObjectsToRelation(thoughts);
		// Zde osetrime pripad kdyz nekolik objektu za sebou konkuruji ve vytvoreni asociace.
		Vector<Integer> doNotRelate = getDoNotRelate(thoughts, result);
		// az ted je pospojujeme
		// odstranime asociace ktere prohraly v konkurenci se sousednimi asociacemi
		for (int h = 0; h < doNotRelate.size(); h++) {
			result.remove(doNotRelate.get(h));
		}
		return result;
	}
	

	/**
	 * Projit vsechny pary v celem thoughts2 a oznacit pro spojeni Thoughts,
	 * ktere maji hloubku mensi nez objectsCreationDepth.
	 * Oznacuje pro spojeni i objekty z ruznych urovni TYPE
	 * @param thoughts - samotna Myslenka
	 * @param objectsCreationDepth
	 * @return
	 */
	private Vector<Integer> getAllObjectsToRelation(Vector<Thought> thoughts) {
		int objectsCreationDepth = config.getKnowledge_objectsCreationDepth();
		Vector<Integer> objectsToRelation = new Vector<Integer>();
		for (int i = 0; i < thoughts.size()-1; i++) {
			Thought nextThought = thoughts.get(i);
			Thought nextFollThought = thoughts.get(i+1);
			if (nextThought.getActiveObject().getType() < objectsCreationDepth 
					&&nextFollThought.getActiveObject().getType() < objectsCreationDepth) {
				objectsToRelation.add(i);
			}
		}
		return objectsToRelation;
	}

	/**
	 * Rozhodnout ktere objekty nespojovat. Konkurence. Tato metoda je rekurzivni, 
	 * aby nepustila ke spojeni blizke pary objektu.
	 * @param thoughts
	 * @param decideToRelateByObjectTypeOrAssocCost true = objectType, false = associationCost
	 * @param decideToRelateObjectsByHigherAssocCost
	 * @param decideToRelateObjectsByHigherObjectType
	 * @param objectsToRelation
	 * @return
	 * @throws Exception
	 */
	private Vector<Integer> getDoNotRelate(Vector<Thought> thoughts,
			Vector<Integer> objectsToRelation) throws Exception {
		boolean relateOnlyObjectsOfSameTypes = config.isKnowledge_relateOnlyObjectsOfSameTypes();
		boolean decideToRelateByObjectTypeOrAssocCost = config.isKnowledge_decideToRelateByObjectTypeOrAssocCost();
		boolean decideToRelateObjectsByHigherAssocCost = config.isKnowledge_decideToRelateObjectsByHigherAssocCost();
		boolean decideToRelateObjectsByHigherObjectType = config.isKnowledge_decideToRelateObjectsByHigherObjectType();
		Vector<Integer> doNotRelate = new Vector<Integer>(); // zde budou polozky z objectsToRelation ktere se nemaji spojovat.
		for (int i = 0; i < objectsToRelation.size()-1; i=i+2) {
			Integer nextThoughtKey = objectsToRelation.get(i);
			Integer nextThoughtFollowingKey = objectsToRelation.get(i+1);
			// Oznacit pro spojeni vsechny ktere maji assoc na nasled. objekt, nacist nove vznikle Thought a opakovat pokud budou nalezeny.
			// Budou spojene dle pravidla definovaneho v promenne bud s vetsim anebo s mensim objektem.
			if (nextThoughtFollowingKey - nextThoughtKey == 1) {
				Thought th1 = thoughts.get(nextThoughtKey);
				Thought th2 = thoughts.get(nextThoughtFollowingKey);
				Thought th3 = thoughts.get(nextThoughtFollowingKey+1);
				/* Jestli parametr narizuje spojeni jen objektu, ktere maji stejny typ (jsou ve stejne vrstve),
				 * nebudeme spojovat objekty s ruznym TYPE */
				if (relateOnlyObjectsOfSameTypes) {
					boolean filteredByType = false; /* Jestli bude rozhodnuto dle parametru
					relateOnlyObjectsOfSameTypes, neni co dale resit */ 
					if (th1.getActiveObject().getType() != th2.getActiveObject().getType()) {
						doNotRelate.add(nextThoughtKey);
						filteredByType = true;
					}
					if (th2.getActiveObject().getType() != th3.getActiveObject().getType()) {
						doNotRelate.add(nextThoughtFollowingKey);
						filteredByType = true;
					}
					if (filteredByType) {
						continue;
					}
				}
				// musime zvolit, zda spojovat s mensim nebo vetsim objektem, nebo podle hodnoty cost v assoc.
				if (decideToRelateByObjectTypeOrAssocCost) { // Rozhodovat dle typu objektu
					long firstObType = th1.getActiveObject().getType();
					long secondObType = th2.getActiveObject().getType();
					long thirdObType = th3.getActiveObject().getType();
					if (firstObType+secondObType == secondObType+thirdObType) {
						doNotRelate.add(nextThoughtFollowingKey);
					}
					if (decideToRelateObjectsByHigherObjectType) { 
						// TODO zkontrolovat zda skutecne spojuje dle uvedeneho parametru
						int key = firstObType+secondObType < secondObType+thirdObType?nextThoughtKey:nextThoughtFollowingKey;
						doNotRelate.add(key);
					} else {
						int key = firstObType+secondObType > secondObType+thirdObType?nextThoughtKey:nextThoughtFollowingKey;
						doNotRelate.add(key);
					}
				} else {
					// rozhodovat dle cost asociace. Jestli obe associace maji stejnou Cost, spoji dva prvni objekty.
					Associations assFirst = fastMemory.getAssociation(th1, th2);
					long firstAssocCost = (assFirst == null ? 0 : assFirst.getCost());
					Associations assSecond = fastMemory.getAssociation(th2, th3);
					long secondAssocCost = (assSecond == null ? 0 : assSecond.getCost());
					if (secondAssocCost == firstAssocCost) {
						doNotRelate.add(nextThoughtFollowingKey); 
						// TODO muze i takhle: jestli COST u associaci stejna, pak rozhodovat na zaklade TYPE objektu
						continue; // TODO v pripade jestli predchozi objectToRelation je vedle nasledujiciho, dat prednost jinemu spojeni.
					}
					if (decideToRelateObjectsByHigherAssocCost) {
						int key = firstAssocCost < secondAssocCost ? nextThoughtKey : nextThoughtFollowingKey;
						doNotRelate.add(key);
					} else {
						int key = firstAssocCost > secondAssocCost ? nextThoughtKey : nextThoughtFollowingKey;
						doNotRelate.add(key);
					}
				}
			}
		}
		/** Nepusti ke spojeni pary objektu, ktere jsou za sebou v thoughts2 */
		Vector<Integer> relatedPositions = (Vector<Integer>)objectsToRelation.clone();
		for (int i = 0; i < doNotRelate.size(); i++) {
			relatedPositions.remove(doNotRelate.get(i));
		}
		for (int i = 0; i < relatedPositions.size()-1; i++) {
			int firstPos = relatedPositions.get(i);
			int secondPos = relatedPositions.get(i+1);
			if (secondPos - firstPos == 1) {
				doNotRelate.add(secondPos);
				i++;
			}
		}
		return doNotRelate;
	}

	/**
	 * Nejdriv dohledat spicky pro rozhodovani, jake objekty spojovat
	 * a pospojovat tyto objekty. Pritom zvednout COST u asociaci,
	 * ktere vytvareji tyto spickove objekty.
	 *
	 * @param inputObjects an array of {@link java.lang.Long} objects.
	 * @throws java.lang.Exception if any.
	 * @return an array of {@link java.lang.Long} objects.
	 */
	public Long[] getTipsAndJoin(Long[] inputObjects) throws Exception {
		Layers layers = new Layers();
		for (int i = 0; i < inputObjects.length; i++) {
			layers.addId(inputObjects[i]);
		}
		while (layers.hasLastLayerPairs()) {
			int constant = layers.getCurrentConstant();
			Vector<Long> layer = layers.getCurrentLayer();
			Vector<Long> superiorLayer = fastMemory.getSuperiorObjectsId(layer, constant);
			layers.setLastLayer(superiorLayer);
		}
		// Sestavime objekty od spicek dolu
		Long[] result = layers.getHighlyObjects();
		fastMemory.increaseAssociationsCostToObjectsId(result);
		return result;
	}


}
