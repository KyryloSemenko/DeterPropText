package cz.semenko.word.persistent;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;
import cz.semenko.word.aware.Thought;
import cz.semenko.word.model.memory.Memory;
import cz.semenko.word.technology.memory.slowly.SlowlyMemory;

/** Associations table **/
public class Associations {
	private Long id;
	private Long objId; // reference na vytvoreny objekt
	private Long srcId;
	private Long srcTable;
	private Long tgtId;
	private Long tgtTable;
	private Long cost;
	Logger logger = Logger.getLogger(Associations.class);
	
	public Associations() {}

	public Associations(Long id, Long objId, Long srcId, Long srcTable, Long tgtId,
			Long tgtTable, Long cost) {
		super();
		this.id = id;
		this.objId = objId;
		this.srcId = srcId;
		this.srcTable = srcTable;
		this.tgtId = tgtId;
		this.tgtTable = tgtTable;
		this.cost = cost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Associations)) return false;
		Associations that = (Associations)obj;
		// Pro zrychleni
		if (this.getId() == that.getId() && this.getSrcId() == that.getSrcId() && this.getTgtId() == that.getTgtId()) {
			return true;
		}
		return false;
		/** Muze dochazet k ruznym chybam. Associace se stejnym ID bude mit jine hodnoty,
		 * nebo Associace s ruznymi ID budou mit stejne hodnoty.
		 * Overim oba pripady, ale nevim jak rychle bude fungovat tato metoda.
		 * TODO overit rychlost metody.
		 * TODO az bude odladen beh, odstranit overovani duplicit pro vetsi rychlost.
		 
		if (this.getId().compareTo(that.getId()) == 0) {
			if (this.getCost().compareTo(that.getCost()) != 0 
					|| this.getObjId().compareTo(that.getObjId()) != 0
					|| this.getSrcId().compareTo(that.getSrcId()) != 0
					|| this.getTgtId().compareTo(that.getTgtId()) != 0) {
				String error = ("Associace maji stejne ID, ale ruzny obsah. ID:" + this.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}
			return true;
		} else {
			if (this.getCost().compareTo(that.getCost()) == 0 
					&& this.getObjId().compareTo(that.getObjId()) == 0
					&& this.getSrcId().compareTo(that.getSrcId()) == 0
					&& this.getTgtId().compareTo(that.getTgtId()) == 0) {
				String error = ("Associace maji ruzne ID, ale stejny obsah. ID1: " + this.getId()
						+ ", ID2: " + that.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}
		}
		return false;*/
	}
	
	@Override
	public String toString() {
		return ("\nid=" + id
				+ "\t\tobjId=" + objId
				+ "\t\tsrcId=" + srcId
				+ "\t\tsrcTable=" + srcTable
				+ "\t\ttgtId=" + tgtId
				+ "\t\ttgtTable=" + tgtTable
				+ "\t\tcost=" + cost);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSrcId() {
		return srcId;
	}

	public void setSrcId(Long srcId) {
		this.srcId = srcId;
	}

	public Long getSrcTable() {
		return srcTable;
	}

	public void setSrcTable(Long srcTable) {
		this.srcTable = srcTable;
	}

	public Long getTgtId() {
		return tgtId;
	}

	public void setTgtId(Long tgtId) {
		this.tgtId = tgtId;
	}

	public Long getTgtTable() {
		return tgtTable;
	}

	public void setTgtTable(Long tgtTable) {
		this.tgtTable = tgtTable;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}

	public Long getObjId() {
		return objId;
	}

	public void setObjId(Long objId) {
		this.objId = objId;
	}

	/**
	 * Jestli srcThought je spojena associaci s tgtThought, nalezne a vrati tuto Association. Jinak vrati null.
	 * @param srcThought
	 * @param tgtThought
	 * @return Associations
	 * @throws Exception 
	 */
	public static Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		Vector<Associations> assocVector = srcThought.getConsequenceAssociations();
		Associations result = null;
		for (int i = 0; i < assocVector.size(); i++) {
			result = assocVector.get(i);
			if (result.getTgtId().compareTo(tgtThought.getActiveObject().getId()) == 0) {
				return result;
			}
		}
		// Dohledat Association v Memory
		result = Memory.getInstance().getAssociation(srcThought, tgtThought);
		return result;
	}

	/**
	 * Seradi asociace dle COST od nejvetsiho po nejmensi
	 * @param associations
	 * @return
	 */
	public static Vector<Associations> sortByCostDesc(
			Vector<Associations> associations) {
		Vector<Associations> result = new Vector<Associations>();
		Set<Long> set = new TreeSet<Long>();
		for (int i = 0; i < associations.size(); i++) {
			set.add(associations.get(i).getCost());
		}
		Object[] arr = set.toArray();
		for (int i = arr.length-1; i >= 0; i--) {
			Long cost = (Long)arr[i];
			for (int k = associations.size()-1; k >= 0; k--) {
				Associations assoc = associations.get(k);
				if (assoc.getCost() == cost) {
					result.add(associations.remove(k));
				}
			}
		}
		return result;
	}
}
