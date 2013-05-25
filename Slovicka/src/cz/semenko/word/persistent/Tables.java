package cz.semenko.word.persistent;


/**
 * Tables table *
 *
 * @author k
 * @version $Id: $Id
 */
public class Tables {
	private Long id;
	private String connString;
	
	/**
	 * <p>Constructor for Tables.</p>
	 */
	public Tables() {
	}

	/**
	 * <p>Constructor for Tables.</p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 * @param connString a {@link java.lang.String} object.
	 */
	public Tables(Long id, String connString) {
		super();
		this.id = id;
		this.connString = connString;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#getId()
	 */
	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#setId(java.lang.Long)
	 */
	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#getConnString()
	 */
	/**
	 * <p>Getter for the field <code>connString</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getConnString() {
		return connString;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#setConnString(java.lang.String)
	 */
	/**
	 * <p>Setter for the field <code>connString</code>.</p>
	 *
	 * @param connString a {@link java.lang.String} object.
	 */
	public void setConnString(String connString) {
		this.connString = connString;
	}

}

