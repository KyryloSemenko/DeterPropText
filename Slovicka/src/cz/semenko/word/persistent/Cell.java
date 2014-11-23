package cz.semenko.word.persistent;

import org.apache.log4j.Logger;

import cz.semenko.word.Config;

/**
 * Cell class is a data transfer object. It represents a connection of two {@link Associations} or single sign (character).<br>
 * 
 * @author Kyrylo Semenko
 *
 */
public class Cell {
	/** Base type of a {@link Cell} object, for example a single character */
	public static final Long TYPE_PRIMITIVE = 1L;
	/** Cell with id = 0 is used for mark related {@link Associations} as linked with nothing. Please don't remove this Cell from database.*/
	public static final Long DUMMY_CELL_ID = Long.valueOf(0L);
	/** {@link Cell#type} 0 is used for mark this Cell as removed and available for reuse. */
	public static final Long DUMMY_TYPE = Long.valueOf(0L);
	/** The Cell with id = 0 is dummy. It use for referencing from removed associations. */
	private Long id;
	/** Text of the Cell. It saved only when it's size shorter than parameter {@link Config#dbViewer_maxTextLengthToSave} */
	private String src;
	/** When Cell represents a single sign, its type is 1.<br>
	 * When it represents a {@link Associations} of two {@link Cell} objects, its type is higher than 1.<br>
	 * For example Cell "a" (type 1) and Cell "b" (type 1) creates new Cell "ab" (type 2).<br>
	 * When type is 0, it mean that this cell has been removed and its id is available for reuse.
	 **/
	private Long type;
	Logger logger = Logger.getLogger(Cell.class);
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Cell)) return false;
		Cell that = (Cell)obj;
		// Pro zrychleni
//		if (this.getId() == that.getId()) {
//			return true;
//		}
		/** Muze dochazet k ruznym chybam. Cell se stejnym ID bude mit jine hodnoty,
		 * nebo Cell s ruznymi ID budou mit stejne hodnoty.
		 * Overim oba pripady, ale nevim jak rychle bude fungovat tato metoda.
		 * TODO overit rychlost metody.
		 * TODO az bude odladen beh, odstranit overovani duplicit pro vetsi rychlost.
		 */
		if (this.getId().equals(that.getId())) {
			if (this.getSrc().compareTo(that.getSrc()) != 0 
					|| this.getType().equals(that.getType()) == false) {
				String error = ("Objecty maji stejne ID, ale ruzny obsah. ID:" + this.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}
			return true;
		} else {
			/*if (this.getSrc().compareTo(that.getSrc()) == 0) {
				String error = ("Objecty maji ruzne ID, ale stejny obsah. ID1: " + this.getId()
						+ ", ID2: " + that.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}*/
		}
		return false;
	}
	
	public Cell(Long id, String src, Long type) {
		super();
		this.id = id;
		this.src = src;
		this.type = type;
	}
	
	/**
	 * <p>Empty constructor</p>
	 */
	public Cell() {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Long getType() {
		return type;
	}
	
	public void setType(Long type) {
		this.type = type;
	}
	
	public String toString() {
		return ("src '" + src + "', type " + type + ", id " + id);
	}
}
