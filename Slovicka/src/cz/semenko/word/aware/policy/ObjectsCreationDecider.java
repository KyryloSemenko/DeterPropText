package cz.semenko.word.aware.policy;

import java.util.Vector;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.technology.memory.fast.FastMemory;

public class ObjectsCreationDecider {
// Komponenty doda Spring FW
private FastMemory fastMemory;
private Config config;
	
	public ObjectsCreationDecider() {
		;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * @param fastMemory the fastMemory to set
	 */
	public void setFastMemory(FastMemory fastMemory) {
		this.fastMemory = fastMemory;
	}
	/**
	 * Rozhodnout ktere Thoughts sloucit na zaklade konfiguracnich parametru.
	 * @param thoughts2 - Jiz spojene a nove Thoughts.
	 * @return Vector pozici prvnich polozek thoughts2, ktere povazuje za vhodne spojit s nasledujicimy.
	 * @throws Exception 
	 */
	public Vector<Integer> getPositionsToCreateNewObjects(
			Vector<Thought> thoughts2) throws Exception {
		Vector<Integer> result = new Vector<Integer>();
		boolean isCreateNewObjectsToAllPairs = 
			config.isObjectsCreationDecider_createNewObjectsToAllPairs();
		int createNewObjectsToAllPairsDepth =
			config.getObjectsCreationDecider_createNewObjectsToAllPairsDepth();
		if (isCreateNewObjectsToAllPairs == false) {
			return result;
		}
		// Else
		/** Dohledat vsechny mozne kombinace pro spojeni */
		StringBuffer buff = new StringBuffer();
		Vector<Thought> thoughtsPairToUnion = new Vector<Thought>();
		for (int i = 0; i < thoughts2.size()-1; i++) {
			Thought th1 = thoughts2.get(i);
			Thought th2 = thoughts2.get(i+1);
			if (th1.getActiveObject().getType() <= createNewObjectsToAllPairsDepth 
					&& th2.getActiveObject().getType() <= createNewObjectsToAllPairsDepth) {
				thoughtsPairToUnion.add(th1);
				thoughtsPairToUnion.add(th2);
			}			
		}
		if (thoughtsPairToUnion.size() > 0) {
			/** Dohledat existujici asociace */
			Vector<Associations> associations = fastMemory.getAssociations(thoughtsPairToUnion);
			if (associations.contains(null)) {
				for (int i = 0; i < associations.size(); i++) { // najde aspon jednou null? TODO zdokumentovat
					if (associations.get(i) == null) {
						result.add(i * 2); // vraci pozice v thoughts2
					}
				}
			}
		}
		return result;
	}
}
