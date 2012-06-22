package cz.semenko.word.aware;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.PriorityQueue;
import java.util.Vector;

import cz.semenko.word.model.memory.Memory;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Tato trida ma reprezentovat myslenku. Myslenky jsou v mysleni - Knowledge.
 * Myslenky maji priority.
 * Jsou propojeny s emocemi.
 * Tyto myslenky obcas aktivuji rozhodovani ktere spousti akce.
 * Myslenky jsou navzajem spojene pomoci konektoru. Tyto konektory vznikaji pri souvislostech myslenek.
 * Napriklad pr ma konektor s o, e, a, y, um, i, in, ие ...
 * V tomto pripade nejjednodussi myslenkou je treba pro nebo pra
 * Nebo slozitejsi pripad - princezna
 * Nebo jeste slozitejsi - princezna Turandot
 * Nebo myslenka hodna k zapomenuti - tttttttt - jestli jednou vznikne, nejspise nebude mit
 * zadne konektory, nebo bude mit jen jeden.
 * Meli by byt perzistentni a serializovatelne.
 * Vzor Standalone.
 * @author k
 *
 */
public class Thought implements Serializable {
	private Objects activeObject; // Soucast myslenky
	private Vector<Associations> consequenceAssociations; // Toto jsou ty konektory dusledku
	//private Vector<Thought> paralelThoughts; // Napriklad a-br a ab-r, nebo ko-cka, k-ocka, koc-ka, kock-a.
		
	public Thought() {}
	
	public Thought(Objects activeObject,
			Vector<Associations> consequenceAssociations) {
		super();
		this.activeObject = activeObject;
		this.consequenceAssociations = consequenceAssociations;
	}

	public Objects getActiveObject() {
		return activeObject;
	}

	public void setActiveObject(Objects activeObject) {
		this.activeObject = activeObject;
	}

	public Vector<Associations> getConsequenceAssociations() {
		return consequenceAssociations;
	}

	public void setConsequenceAssociations(
			Vector<Associations> consequenceAssociations) {
		this.consequenceAssociations = consequenceAssociations;
	}
	
	@Override
	public String toString() {
		return "Thought[activeObject=" + activeObject
				+ ", consequenceAssociations:\n\t" + consequenceAssociations + "]\n";
	}

	/**
	 * Dohleda u tohoto thought association na objekt v parametru
	 * @param th2
	 * @return Association nebo null
	 */
	public Associations getAssociation(Thought th2) {
		Vector<Associations> vector = getConsequenceAssociations();
		if (vector == null || vector.size() == 0) {
			return null;
		}
		for (int i = 0; i < vector.size(); i++) {
			Associations nextAss = vector.get(i);
			if (nextAss.getTgtId().compareTo(th2.getActiveObject().getId()) == 0) {
				return nextAss;
			}
		}
		return null;
	}

}
