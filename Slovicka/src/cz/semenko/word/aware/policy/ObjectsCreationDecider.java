package cz.semenko.word.aware.policy;

import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Byznys process, který rozhoduje, zda vytvářet objekty nebo nikoliv.
 * Business process to decide for object creation.
 * 
 * @author Kyrylo Semenko
 *
 */
public class ObjectsCreationDecider {
// Komponenty doda Spring FW
private FastMemory fastMemory;
private Config config;
	
	/**
	 * Empty constructor
	 */
	public ObjectsCreationDecider() {}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setFastMemory(FastMemory fastMemory) {
		this.fastMemory = fastMemory;
	}
	/**
	 * Rozhodnout ktere Thoughts sloucit na zaklade konfiguracnich parametru.
	 * Decide which Thoughts to be merge. Decision is based on the configuration parameters.
	 * 
	 * @param thoughts - Jiz spojene a/nebo nove Thoughts.
	 * Already merged and/or new Thoughts.
	 * 
	 * @return kolekce pozici prvnich polozek thoughts, ktere povazuje za vhodne spojit se sousednimy vpravo.
	 * Collection of positions of first items, that will be merged to their right neighbours.
	 * 
	 * @throws Exception 
	 */
	public Vector<Integer> getPositionsToCreateNewObjects(
			Vector<Thought> thoughts) throws Exception {
		Vector<Integer> result = new Vector<Integer>();
		boolean isCreateNewObjectsToAllPairs = config.isObjectsCreationDecider_createNewObjectsToAllPairs();
		if (isCreateNewObjectsToAllPairs == false) {
			return result;
		}

		int createNewObjectsToAllPairsDepth = config.getObjectsCreationDecider_createNewObjectsToAllPairsDepth();
		// Dohledat vsechny mozne kombinace pro spojeni
		// Trace all possible combinations to merge
		Vector<Thought> thoughtsPairToUnion = new Vector<Thought>();
		for (int i = 0; i < thoughts.size()-1; i++) {
			Thought th = thoughts.get(i);
			Thought nextTh = thoughts.get(i+1);
			if (th.getActiveObject().getType() <= createNewObjectsToAllPairsDepth 
					&& nextTh.getActiveObject().getType() <= createNewObjectsToAllPairsDepth) {
				thoughtsPairToUnion.add(th);
				thoughtsPairToUnion.add(nextTh);
			}			
		}
		if (thoughtsPairToUnion.size() > 0) {
			// Dohledat existujici asociace
			// Trace an existing associations
			Vector<Associations> associations = fastMemory.getAssociations(thoughtsPairToUnion);
			if (associations.contains(null)) {
				for (int i = 0; i < associations.size(); i++) {
					if (associations.get(i) == null) {
						result.add(i * 2); // Pozice v thoughts. Position in thoughts
					}
				}
			}
		}
		return result;
	}
}
