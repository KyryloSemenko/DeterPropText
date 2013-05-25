package cz.semenko.word.persistent;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import cz.semenko.word.aware.Thought;
import cz.semenko.word.technology.memory.fast.FastMemory;

/**
 * Associations table *
 *
 * @author k
 * @version $Id: $Id
 */
public class Associations {
	// Objekt doda Spring
	private FastMemory fastMemory;
	private Long id;
	private Long objId; // reference na vytvoreny objekt
	private Long srcId;
	private Long srcTable;
	private Long tgtId;
	private Long tgtTable;
	private Long cost;
	Logger logger = Logger.getLogger(Associations.class);
	
	/**
	 * <p>Constructor for Associations.</p>
	 */
	public Associations() {}

	/**
	 * <p>Setter for the field <code>fastMemory</code>.</p>
	 *
	 * @param memory the memory to set
	 */
	public void setFastMemory(FastMemory memory) {
		this.fastMemory = memory;
	}

	/**
	 * <p>Constructor for Associations.</p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 * @param objId a {@link java.lang.Long} object.
	 * @param srcId a {@link java.lang.Long} object.
	 * @param srcTable a {@link java.lang.Long} object.
	 * @param tgtId a {@link java.lang.Long} object.
	 * @param tgtTable a {@link java.lang.Long} object.
	 * @param cost a {@link java.lang.Long} object.
	 */
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
	
	/** {@inheritDoc} */
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
	}
	
	/** {@inheritDoc} */
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

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * <p>Getter for the field <code>srcId</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getSrcId() {
		return srcId;
	}

	/**
	 * <p>Setter for the field <code>srcId</code>.</p>
	 *
	 * @param srcId a {@link java.lang.Long} object.
	 */
	public void setSrcId(Long srcId) {
		this.srcId = srcId;
	}

	/**
	 * <p>Getter for the field <code>srcTable</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getSrcTable() {
		return srcTable;
	}

	/**
	 * <p>Setter for the field <code>srcTable</code>.</p>
	 *
	 * @param srcTable a {@link java.lang.Long} object.
	 */
	public void setSrcTable(Long srcTable) {
		this.srcTable = srcTable;
	}

	/**
	 * <p>Getter for the field <code>tgtId</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getTgtId() {
		return tgtId;
	}

	/**
	 * <p>Setter for the field <code>tgtId</code>.</p>
	 *
	 * @param tgtId a {@link java.lang.Long} object.
	 */
	public void setTgtId(Long tgtId) {
		this.tgtId = tgtId;
	}

	/**
	 * <p>Getter for the field <code>tgtTable</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getTgtTable() {
		return tgtTable;
	}

	/**
	 * <p>Setter for the field <code>tgtTable</code>.</p>
	 *
	 * @param tgtTable a {@link java.lang.Long} object.
	 */
	public void setTgtTable(Long tgtTable) {
		this.tgtTable = tgtTable;
	}

	/**
	 * <p>Getter for the field <code>cost</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getCost() {
		return cost;
	}

	/**
	 * <p>Setter for the field <code>cost</code>.</p>
	 *
	 * @param cost a {@link java.lang.Long} object.
	 */
	public void setCost(Long cost) {
		this.cost = cost;
	}

	/**
	 * <p>Getter for the field <code>objId</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getObjId() {
		return objId;
	}

	/**
	 * <p>Setter for the field <code>objId</code>.</p>
	 *
	 * @param objId a {@link java.lang.Long} object.
	 */
	public void setObjId(Long objId) {
		this.objId = objId;
	}

	/**
	 * Jestli srcThought je spojena associaci s tgtThought, nalezne a vrati tuto Association. Jinak vrati null.
	 *
	 * @param srcThought a {@link cz.semenko.word.aware.Thought} object.
	 * @param tgtThought a {@link cz.semenko.word.aware.Thought} object.
	 * @return Associations
	 * @throws java.lang.Exception if any.
	 */
	public Associations getAssociation(Thought srcThought, Thought tgtThought) throws Exception {
		Vector<Associations> assocVector = srcThought.getConsequenceAssociations();
		Associations result = null;
		for (int i = 0; i < assocVector.size(); i++) {
			result = assocVector.get(i);
			if (result.getTgtId().compareTo(tgtThought.getActiveObject().getId()) == 0) {
				return result;
			}
		}
		// Dohledat Association v Memory
		result = fastMemory.getAssociation(srcThought, tgtThought);
		return result;
	}

	/**
	 * Seradi asociace dle COST od nejvetsiho po nejmensi
	 *
	 * @param associations a {@link java.util.Vector} object.
	 * @return a {@link java.util.Vector} object.
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
