package cz.semenko.word.model.memory;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Objects;
import cz.semenko.word.technology.memory.fast.FastMemory;
import cz.semenko.word.technology.memory.slowly.SlowlyMemory;

/** Pomocí této třídy systém přistupuje k rychlé a pomalé pamětim. **/
public class Memory implements InterfaceMemory {
	// Objekt nasetuje Spring
	private FastMemory fastMemory;
	// Objekt nasetuje Spring
	private SlowlyMemory slowlyMemory;
	
	/**
	 * @param fastMemory the fastMemory to set
	 */
	public void setFastMemory(FastMemory fastMemory) {
		this.fastMemory = fastMemory;
	}

	/**
	 * @param slowlyMemory the slowlyMemory to set
	 */
	public void setSlowlyMemory(SlowlyMemory slowlyMemory) {
		this.slowlyMemory = slowlyMemory;
	}

	public Memory() {
	}
	
	
	@Override
	/**
	 * Vyhleda id znaku ve FastMemory, kdyz nenajde tak v SlowlyMemory,
	 * kdyz nenajde tak zalozi novy znak ve SlowlyMemory.
	 */
	public Long[] getObjects(char[] inputChars) throws Exception {
		Long[] result = fastMemory.getCharsId(inputChars);
		return result;
	}

	/**
	 * Dohleda Objects ve FastMemory a SlowlyMemory.
	 * Najde Objekts pro Vektor IDcek. Jestli ne, tak sahne na SlowlyMemory.
	 * Seradi elementy v objectsTableCollection dle vyuzivanosti.
	 * @param unitedObjectsId
	 * @return
	 * @throws Exception
	 */
	public Vector<Objects> getObjects(Vector<Long> unitedObjectsId) throws Exception {
		Vector<Objects> result = fastMemory.getObjects(unitedObjectsId);
		return result;
	}

	/**
	 * Pri vytvoreni nove Associations je potreba rozhodnout, zda je
	 * potreba i novy Object. Bud se to ma rozhodnout na zaklade
	 * parametru thoughts2, coz jsou prave aktivni myslenky,
	 * anebo object se vytvori vzdy, a pak nasledne bude bud 
	 * zachovan nebo smazan (zapomenout).
	 * Prozatim vzdy vytvorim novy Object.
	 * TODO naprogramovat sofistikovanejsi zpusob rozhodovani o novych objektech.
	 * 
	 * @param srcThought
	 * @param tgtThought
	 * @param thoughts2
	 * @return novy Objects.
	 * @throws Exception 
	 */
	public Objects getNewObject(Thought srcThought, Thought tgtThought,
			Vector<Thought> thoughts2) throws Exception {
		Objects result = fastMemory.getNewObject(srcThought, tgtThought);
		return result;
	}

	/**
	 * Dohleda Association v pameti. Jestli nedohleda, vrati null
	 * @param srcThought
	 * @param tgtThought
	 * @return Association nebo null
	 * @throws Exception 
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		return fastMemory.getAssociation(srcThought, tgtThought);
	}

	/**
	 * Zvysi COST associaci o jednicku.
	 * @param associationsId - IDecka associaci v kterych se ma zvednout ID
	 * @throws Exception
	 */
	public void increaseAssociationsCost(Vector<Long> associationsId) throws Exception {
		fastMemory.increaseAssociationsCost(associationsId);
	}

	/**
	 * Dohleda associations v FastMemory a SlowlyMemory. Nevytvari nove.
	 * @param thoughtsPairToUnion - pary pro spojeni.
	 * @return
	 * @throws Exception 
	 */
	public Vector<Associations> getAssociations(
			Vector<Thought> thoughtsPairToUnion) throws Exception {
		Vector<Associations> result = fastMemory.getAssociations(thoughtsPairToUnion);
		return result;
	}

	/**
	 * Vytvori nove objekty a asociace
	 * @param thoughtPairsToMerge - pary Thought, ktere zarucene nemaji Associations ani spolecny Objects
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public void createNewAssociationsAndObjects(
			Vector<Thought> thoughtPairsToMerge) throws SQLException, Exception {
		fastMemory.createNewAssociationsAndObjects(thoughtPairsToMerge);
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
		fastMemory.createNewAssociationsAndObjects(nonExistsPairs);
		Vector<Associations> result = fastMemory.getAssociations(nonExistsPairs);
		return result;
	}

	/**
	 * Dohleda vsechny associations, ve kterych src_id == objectId z parametru
	 * @param objectsId - Vector idecek Objektu.
	 * @return
	 * @throws Exception 
	 */
	public Vector<Associations> getAllAssociations(Vector<Long> objectsId) throws Exception {
		return fastMemory.getAllAssociations(objectsId);
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
	 * Dohleda spolecny Thought pro par. Nic nevytvari.
	 * @param th1
	 * @param th2
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	public Thought getThought(Thought th1, Thought th2) throws SQLException, Exception {
		return fastMemory.getThought(th1, th2);
	}

	/**
	 * Pridat nove objekty do fastMemory
	 * @param newObjects
	 */
	public void addObjectsToFastMemory(Collection<Objects> newObjects) {
		fastMemory.addObjects(newObjects);		
	}

	/**
	 * Pridat nove associations do fastMemory
	 * @param newAssociations
	 */
	public void addAssociationsToFastMemory(Collection<Associations> newAssociations) {
		fastMemory.addAssociations(newAssociations);
	}
	
	
}
