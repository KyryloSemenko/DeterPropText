package cz.semenko.word.persistent;

/**
 * Associations class is data transfer object. It represents association of two {@link Cell} objects.
 * @author Kyrylo Semenko
 *
 */
public class Associations {
	private Long id;
	/** The {@link Association} object can have a related {@link Cell} object.<br>
	 *  If Association has a related {@link Cell} object, it mean that Association is used often.<br>
	 *  If Association has'nt a related {@link Cell} object, it mean that Association is new.<br>
	 *  When cell_id is 0, it mean that association has been removed and referenced to dummy cell. Such Association id can be reused when a new one is creating.<br>
	 *  Human brain has around 86,000,000,000 neurons and an average adult has 100000000000000 - 1000000000000000 synapses (by information from <a href="http://en.wikipedia.org/wiki/List_of_animals_by_number_of_neurons">Wikipedia</a>) */
	private Long cellId;
	/** ID of source Cells */
	private Long srcId;
	/** Table ID of source Cell */
	private Long srcTable;
	/** ID of target Cell */
	private Long tgtId;
	/** Table ID of target Cell */
	private Long tgtTable;
	/** Cost is a property, that describe a value of Association. Frequently used Associations has higher cost */
	private Long cost;
	
	/**
	 * <p>Empty constructor</p>
	 */
	public Associations() {}

	public Associations(Long id, Long cellId, Long srcId, Long srcTable, Long tgtId,
			Long tgtTable, Long cost) {
		super();
		this.id = id;
		this.cellId = cellId;
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
		// To run faster
		if (this.getId() == that.getId() && this.getSrcId() == that.getSrcId() && this.getTgtId() == that.getTgtId()) {
			return true;
		}
		return false;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return ("\nid=" + id
				+ "\t\tcellId=" + cellId
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
		return cellId;
	}

	public void setObjId(Long cellId) {
		this.cellId = cellId;
	}
}
