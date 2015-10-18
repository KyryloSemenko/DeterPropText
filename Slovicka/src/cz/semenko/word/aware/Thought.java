package cz.semenko.word.aware;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;

/**
 * Tato trida ma reprezentovat myslenku. Myslenky jsou v mysleni - Knowledge.<br>
 * Myslenky maji priority.<br>
 * Jsou propojeny s emocemi.<br>
 * Tyto myslenky obcas aktivuji rozhodovani ktere spousti akce.<br>
 * Myslenky jsou navzajem spojene pomoci konektoru. Tyto konektory vznikaji pri souvislostech myslenek.<br>
 * Napriklad pr ma konektor s o, e, a, y, um, i, in, ие ...<br>
 * V tomto pripade nejjednodussi myslenkou je treba pro nebo pra<br>
 * Nebo slozitejsi pripad - princezna<br>
 * Nebo jeste slozitejsi - princezna Turandot<br>
 * Nebo myslenka hodna k zapomenuti - tttttttt - jestli jednou vznikne, nejspise nebude mit<br>
 * zadne konektory, nebo bude mit jen jeden.<br>
 * Meli by byt perzistentni a serializovatelne.<br>
 * Vzor Standalone.<br>
 */
public class Thought implements Serializable {
	/** Soucast myslenky */
	private Cell activeCell;
	
	/** Konektory dusledku */
	private Vector<Associations> consequenceAssociations;
	
	//private Vector<Thought> paralelThoughts; // Napriklad a-br a ab-r, nebo ko-cka, k-ocka, koc-ka, kock-a.
		
	/**
	 * <p>Constructor for Thought.</p>
	 */
	public Thought() {}
	
	/**
	 * <p>Constructor for Thought.</p>
	 *
	 * @param activeCell a {@link cz.semenko.word.persistent.Cell} Cell.
	 * @param consequenceAssociations a {@link java.util.Vector} Cell.
	 */
	public Thought(Cell activeCell,
			Vector<Associations> consequenceAssociations) {
		super();
		this.activeCell = activeCell;
		this.consequenceAssociations = consequenceAssociations;
	}

	/**
	 * <p>Getter for the field <code>activeCell</code>.</p>
	 *
	 * @return a {@link cz.semenko.word.persistent.Cell} object.
	 */
	public Cell getActiveCell() {
		return activeCell;
	}

	/**
	 * <p>Setter for the field <code>activeCell</code>.</p>
	 *
	 * @param activeCell a {@link cz.semenko.word.persistent.Cell} object.
	 */
	public void setActiveCell(Cell activeCell) {
		this.activeCell = activeCell;
	}

	/**
	 * <p>Getter for the field <code>consequenceAssociations</code>.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Collection<Associations> getConsequenceAssociations() {
		return consequenceAssociations;
	}

	/**
	 * <p>Setter for the field <code>consequenceAssociations</code>.</p>
	 *
	 * @param consequenceAssociations a {@link java.util.Vector} object.
	 */
	public void setConsequenceAssociations(
			Vector<Associations> consequenceAssociations) {
		this.consequenceAssociations = consequenceAssociations;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Thought[activeCell=" + activeCell
				+ ", consequenceAssociations:\n\t" + consequenceAssociations + "]\n";
	}

	/**
	 * Dohleda u tohoto thought association na objekt v parametru
	 *
	 * @param th2 a {@link cz.semenko.word.aware.Thought} object.
	 * @return Association nebo null
	 */
	public Associations getAssociation(Thought th2) {
		Vector<Associations> vector = (Vector<Associations>) getConsequenceAssociations();
		if (vector == null || vector.size() == 0) {
			return null;
		}
		for (int i = 0; i < vector.size(); i++) {
			Associations nextAss = vector.get(i);
			if (nextAss.getTgtId().compareTo(th2.getActiveCell().getId()) == 0) {
				return nextAss;
			}
		}
		return null;
	}

}
