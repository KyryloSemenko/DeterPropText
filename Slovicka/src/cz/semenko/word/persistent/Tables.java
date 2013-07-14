package cz.semenko.word.persistent;


/**
 * Tables class is data transfer object. Represent table in database.
 * 
 * @author Kyrylo Semenko
 */
public class Tables {
	/** Table ID in particular database */
	private Long id;
	/** Connection string to database */
	private String connString;
	
	/**
	 * <p>Empty constructor</p>
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConnString() {
		return connString;
	}

	public void setConnString(String connString) {
		this.connString = connString;
	}

}

